package com.keak.aishou.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class ImageShareHelper(private val context: Context) {

    actual suspend fun shareComposableAsImage(
        imageBitmap: ImageBitmap,
        fileName: String
    ) {
        try {
            val bitmap = imageBitmap.asAndroidBitmap()
            val imageUri = saveBitmapToCache(bitmap, fileName)

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share your result")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            println("ImageShareHelper: Error sharing image: ${e.message}")
            e.printStackTrace()
        }
    }

    actual suspend fun shareToInstagramStory(
        imageBitmap: ImageBitmap,
        fileName: String
    ) {
        try {
            val bitmap = imageBitmap.asAndroidBitmap()
            val imageUri = saveBitmapToCache(bitmap, fileName)

            println("ImageShareHelper: Generated URI for Instagram: $imageUri")

            // Alternative approach: Save to external storage instead of cache
            val externalImageUri = saveBitmapToExternalStorage(bitmap, fileName)

            println("ImageShareHelper: Generated external URI for Instagram: $externalImageUri")

            // Try multiple Instagram sharing approaches
            val instagramStoryIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                putExtra("interactive_asset_uri", externalImageUri ?: imageUri)
                type = "image/png"
                setPackage("com.instagram.android")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                // Grant URI permissions explicitly to Instagram
                val uriToUse = externalImageUri ?: imageUri
                context.grantUriPermission(
                    "com.instagram.android",
                    uriToUse,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            // Check if Instagram app is installed first
            val isInstagramInstalled = try {
                context.packageManager.getPackageInfo("com.instagram.android", 0)
                true
            } catch (e: Exception) {
                println("ImageShareHelper: Instagram app not installed: ${e.message}")
                false
            }

            if (isInstagramInstalled) {
                try {
                    println("ImageShareHelper: Attempting to launch Instagram Stories")
                    context.startActivity(instagramStoryIntent)
                    println("ImageShareHelper: Instagram Stories launched successfully")
                } catch (e: Exception) {
                    println("ImageShareHelper: Instagram Stories failed, trying regular Instagram: ${e.message}")

                    // Fallback: Try regular Instagram sharing
                    val regularInstagramIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        type = "image/png"
                        setPackage("com.instagram.android")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        // Grant URI permissions explicitly to Instagram
                        context.grantUriPermission(
                            "com.instagram.android",
                            imageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

                    try {
                        context.startActivity(regularInstagramIntent)
                        println("ImageShareHelper: Regular Instagram sharing launched")
                    } catch (e2: Exception) {
                        println("ImageShareHelper: All Instagram sharing methods failed: ${e2.message}")
                        shareComposableAsImage(imageBitmap, fileName)
                    }
                }
            } else {
                println("ImageShareHelper: Instagram not installed, falling back to regular share")
                shareComposableAsImage(imageBitmap, fileName)
            }
        } catch (e: Exception) {
            println("ImageShareHelper: Error sharing to Instagram: ${e.message}")
            e.printStackTrace()
            // Fallback to regular share
            shareComposableAsImage(imageBitmap, fileName)
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap, fileName: String): Uri {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()

        val file = File(cachePath, fileName)
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    private fun saveBitmapToExternalStorage(bitmap: Bitmap, fileName: String): Uri? {
        return try {
            // Save to Pictures directory in external storage
            val imagesDir = File(context.getExternalFilesDir(null), "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val imageFile = File(imagesDir, fileName)
            val fileOutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            println("ImageShareHelper: Saved to external storage: ${imageFile.absolutePath}")

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            println("ImageShareHelper: Error saving to external storage: ${e.message}")
            null
        }
    }
}

actual object ImageShareHelperFactory {
    private lateinit var _context: Context
    private lateinit var _activityContext: Context

    val context: Context get() = _context
    val activityContext: Context get() = _activityContext

    fun initialize(context: Context) {
        this._context = context.applicationContext
        // Keep the original context (likely Activity) for UI operations
        this._activityContext = context
    }

    actual fun create(): ImageShareHelper {
        return ImageShareHelper(_context)
    }
}

// Enhanced JetCapture approach - attach to window for proper rendering
actual suspend fun captureComposableAsBitmap(
    content: @Composable () -> Unit
): ImageBitmap? = withContext(Dispatchers.Main) {
    suspendCoroutine { continuation ->
        try {
            println("captureComposableAsBitmap: Starting enhanced JetCapture approach")

            val activityContext = ImageShareHelperFactory.activityContext
            val activity = activityContext as? android.app.Activity

            if (activity == null) {
                println("captureComposableAsBitmap: Activity context is not an Activity: ${activityContext::class.java.simpleName}, falling back")
                continuation.resume(null)
                return@suspendCoroutine
            }

            println("captureComposableAsBitmap: Activity context found: ${activity::class.java.simpleName}")

            val composeView = ComposeView(activity)
            val density = activity.resources.displayMetrics.density
            // Instagram Stories aspect ratio 9:16
            val widthPx = (1080 * density).toInt()  // 1080p width
            val heightPx = (1920 * density).toInt() // 1920p height (9:16)

            println("captureComposableAsBitmap: Density: $density")
            println("captureComposableAsBitmap: Target dimensions: ${widthPx}x${heightPx}")
            println("captureComposableAsBitmap: DP dimensions: 1080x1920")

            // Set up the composable content with fixed Instagram Stories size
            composeView.setContent {
                Box(
                    modifier = Modifier
                        .width((widthPx / density).dp)
                        .height((heightPx / density).dp)
                ) {
                    content()
                }
            }

            // Create a temporary invisible container
            val tempContainer = android.widget.FrameLayout(activity).apply {
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    widthPx, heightPx
                )
                visibility = View.INVISIBLE
            }

            // Add ComposeView to temp container
            tempContainer.addView(
                composeView,
                android.widget.FrameLayout.LayoutParams(widthPx, heightPx)
            )

            // Add temp container to activity's root
            val rootView = activity.findViewById<android.view.ViewGroup>(android.R.id.content)
            rootView.addView(tempContainer)

            // Wait for composition and layout
            composeView.post {
                try {
                    // Force measure and layout
                    composeView.measure(
                        View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
                    )
                    composeView.layout(0, 0, widthPx, heightPx)

                    println("captureComposableAsBitmap: After measurement:")
                    println("captureComposableAsBitmap: ComposeView measured: ${composeView.measuredWidth}x${composeView.measuredHeight}")
                    println("captureComposableAsBitmap: Expected: ${widthPx}x${heightPx}")

                    // Wait one more frame to ensure complete composition
                    composeView.post {
                        try {
                            val bitmap = Bitmap.createBitmap(
                                widthPx, heightPx, Bitmap.Config.ARGB_8888
                            )
                            val canvas = Canvas(bitmap)

                            // Draw the ComposeView to bitmap
                            composeView.draw(canvas)

                            // Clean up - remove temp container
                            rootView.removeView(tempContainer)

                            println("captureComposableAsBitmap: SUCCESS! Bitmap size: ${bitmap.width}x${bitmap.height}")

                            // DEBUG: Log bitmap details to understand what we captured
                            println("captureComposableAsBitmap: DEBUG - Bitmap details:")
                            println("captureComposableAsBitmap: DEBUG - Width: ${bitmap.width}, Height: ${bitmap.height}")
                            println("captureComposableAsBitmap: DEBUG - Config: ${bitmap.config}")
                            println("captureComposableAsBitmap: DEBUG - Bytes count: ${bitmap.byteCount}")
                            println("captureComposableAsBitmap: DEBUG - ComposeView measured: ${composeView.measuredWidth}x${composeView.measuredHeight}")
                            println("captureComposableAsBitmap: DEBUG - This should be the real ShareableMatchResultCard!")

                            continuation.resume(bitmap.asImageBitmap())

                        } catch (e: Exception) {
                            println("captureComposableAsBitmap: Drawing failed: ${e.message}")
                            rootView.removeView(tempContainer)
                            continuation.resume(null)
                        }
                    }
                } catch (e: Exception) {
                    println("captureComposableAsBitmap: Layout failed: ${e.message}")
                    rootView.removeView(tempContainer)
                    continuation.resume(null)
                }
            }

        } catch (e: Exception) {
            println("captureComposableAsBitmap: Setup failed: ${e.message}")
            e.printStackTrace()
            continuation.resume(null)
        }
    }
}

actual fun generateShareableBitmap(
    testResult: com.keak.aishou.data.api.TestResultResponse,
    userDisplayName: String
): ImageBitmap? {
    return try {
        println("generateShareableBitmap: Starting bitmap generation matching ShareableMatchResultCard")
        val density = ImageShareHelperFactory.context.resources.displayMetrics.density
        val widthPx = (400 * density).toInt() // Instagram Stories width
        val heightPx = (600 * density).toInt() // Instagram Stories height

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val compatibilityResult = testResult.compatibilityResults?.firstOrNull()

        // Main card background with shadow (NeoBrutalist style)
        val shadowPaint = android.graphics.Paint().apply { color = android.graphics.Color.BLACK }
        canvas.drawRect(6f * density, 6f * density, widthPx.toFloat(), heightPx.toFloat(), shadowPaint)

        val backgroundPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#C8FBAD") // Same as ShareableMatchResultCard
        }
        canvas.drawRect(0f, 0f, widthPx - 6f * density, heightPx - 6f * density, backgroundPaint)

        val borderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 3f * density
        }
        canvas.drawRect(1.5f * density, 1.5f * density, widthPx - 6f * density - 1.5f * density, heightPx - 6f * density - 1.5f * density, borderPaint)

        var currentY = 50f * density

        // MBTI cards at top (yellow background like ShareableMatchResultCard)
        val yellowPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FDFEA5") // Same as ShareableMatchResultCard
        }

        val cardShadowPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK // NeoBrutalist shadow
        }

        val cardShadowOffset = 6f * density // NeoBrutalist default shadow offset

        compatibilityResult?.let { result ->

            // Left MBTI card (weight(1f) means equal width with spacing)
            val cardWidth = (widthPx - 50f * density) / 2f // Total width minus margins and spacing, divided by 2
            val leftCardRect = android.graphics.RectF(
                20f * density, currentY,
                20f * density + cardWidth, currentY + 50f * density
            )
            val leftShadowRect = android.graphics.RectF(
                leftCardRect.left + cardShadowOffset,
                leftCardRect.top + cardShadowOffset,
                leftCardRect.right + cardShadowOffset,
                leftCardRect.bottom + cardShadowOffset
            )

            // Draw shadow first, then card
            canvas.drawRect(leftShadowRect, cardShadowPaint)
            canvas.drawRect(leftCardRect, yellowPaint)
            canvas.drawRect(leftCardRect, borderPaint)

            // Right MBTI card (weight(1f) means equal width)
            val rightCardRect = android.graphics.RectF(
                leftCardRect.right + 8f * density, currentY, // Spacer(8.dp)
                leftCardRect.right + 8f * density + cardWidth, currentY + 50f * density
            )
            val rightShadowRect = android.graphics.RectF(
                rightCardRect.left + cardShadowOffset,
                rightCardRect.top + cardShadowOffset,
                rightCardRect.right + cardShadowOffset,
                rightCardRect.bottom + cardShadowOffset
            )

            // Draw shadow first, then card
            canvas.drawRect(rightShadowRect, cardShadowPaint)
            canvas.drawRect(rightCardRect, yellowPaint)
            canvas.drawRect(rightCardRect, borderPaint)

            // MBTI text
            val mbtiTextPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textSize = 14f * density
            }

            canvas.drawText(
                "${result.myInfo?.mbtiType.orEmpty()} - ${result.myInfo?.zodiacSign.orEmpty()}",
                leftCardRect.centerX(), leftCardRect.centerY() + 5f * density, mbtiTextPaint
            )
            canvas.drawText(
                "${result.friendInfo?.mbtiType.orEmpty()} - ${result.friendInfo?.zodiacSign.orEmpty()}",
                rightCardRect.centerX(), rightCardRect.centerY() + 5f * density, mbtiTextPaint
            )
        }

        currentY += 60f * density // Spacer(16.dp) after MBTI cards

        // "Match Results" title (centered like in Box)
        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            textSize = 18f * density
        }
        canvas.drawText("Match Results", (widthPx - 6f * density) / 2f, currentY, titlePaint)
        currentY += 24f * density // Spacer(8.dp) before score section

        // Score section (blue background like ShareableMatchResultCard)
        compatibilityResult?.let { result ->
            val bluePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#B3CDFF") // Same as ShareableMatchResultCard
            }
            val scoreRect = android.graphics.RectF(
                36f * density, currentY,
                widthPx - 36f * density - 6f * density, currentY + 60f * density
            )
            val scoreShadowRect = android.graphics.RectF(
                scoreRect.left + cardShadowOffset,
                scoreRect.top + cardShadowOffset,
                scoreRect.right + cardShadowOffset,
                scoreRect.bottom + cardShadowOffset
            )

            // Draw shadow first, then card
            canvas.drawRect(scoreShadowRect, cardShadowPaint)
            canvas.drawRect(scoreRect, bluePaint)
            canvas.drawRect(scoreRect, borderPaint)

            val scorePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textSize = 20f * density
            }
            canvas.drawText(
                "${result.score}%",
                scoreRect.centerX(), scoreRect.centerY() + 7f * density, scorePaint
            )
        }

        currentY += 60f * density // Spacer(16.dp) after score section

        // Chemistry header (like BrutalHeader with shadows and skew effect)
        compatibilityResult?.chemistry?.let { chemistry ->
            // BrutalHeader shadow effect (white shadow behind black text)
            val shadowPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textSize = 35f * density // textSize = 35 like in ShareableMatchResultCard
            }

            val chemistryPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textSize = 35f * density // textSize = 35 like in ShareableMatchResultCard
            }

            val shadowOffset = 4f * density // BrutalHeader uses 4dp shadow

            // Draw white shadow first (offset)
            canvas.drawText(chemistry, 20f * density + shadowOffset, currentY + shadowOffset, shadowPaint)
            // Draw black text on top
            canvas.drawText(chemistry, 20f * density, currentY, chemistryPaint)
        }

        currentY += 24f * density // Spacer(8.dp) after chemistry

        // Summary section (green background like ShareableMatchResultCard)
        compatibilityResult?.summary?.let { summary ->
            val greenPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#ADFF86") // Same as ShareableMatchResultCard
            }
            val summaryRect = android.graphics.RectF(
                20f * density, currentY,
                widthPx - 20f * density - 6f * density, currentY + 120f * density // Increased height for cat icon + title
            )
            val summaryShadowRect = android.graphics.RectF(
                summaryRect.left + cardShadowOffset,
                summaryRect.top + cardShadowOffset,
                summaryRect.right + cardShadowOffset,
                summaryRect.bottom + cardShadowOffset
            )

            // Draw shadow first, then card
            canvas.drawRect(summaryShadowRect, cardShadowPaint)
            canvas.drawRect(summaryRect, greenPaint)
            canvas.drawRect(summaryRect, borderPaint)

            // Draw cat icon - use emoji-style representation
            val iconSize = 25f * density
            val iconX = summaryRect.left + 12f * density
            val iconY = summaryRect.top + 12f * density

            // Draw a simple cat face using text emoji
            val emojiPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                textSize = 20f * density // Slightly smaller than the icon size
            }
            canvas.drawText(
                "🐱", // Cat emoji
                iconX + iconSize / 2f,
                iconY + iconSize / 2f + 7f * density, // Center vertically with text baseline adjustment
                emojiPaint
            )

            // Title next to cat icon (fontSize = 24sp like updated ShareableMatchResultCard)
            val titleTextPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textSize = 24f * density // Updated to match fontSize = 24.sp
            }
            canvas.drawText(
                "Match Summary", // "StringResources.matchSummary()"
                summaryRect.left + 12f * density + iconSize + 12f * density,
                summaryRect.top + 12f * density + iconSize/2 + 8f * density,
                titleTextPaint
            )

            // Summary text (12.dp spacing like in ShareableMatchResultCard)
            val summaryTextPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#333333") // Color(0xFF333333)
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT
                textSize = 14f * density // fontSize = 14.sp
            }
            // Simple text wrapping with 12.dp top spacing
            val lines = summary.chunked(45)
            lines.take(3).forEachIndexed { index, line ->
                canvas.drawText(
                    line,
                    summaryRect.left + 12f * density,
                    summaryRect.top + 12f * density + iconSize + 12f * density + (index * 18f * density),
                    summaryTextPaint
                )
            }
        }

        // App branding at bottom (centered with app icon like in ShareableMatchResultCard)
        val brandingY = heightPx - 40f * density
        val appIconSize = 30f * density

        // App icon - use text-based logo representation
        val appIconRect = android.graphics.RectF(
            (widthPx - 6f * density) / 2f - appIconSize - 4f * density, // Center minus half of (icon + spacing + text)
            brandingY - appIconSize / 2f,
            (widthPx - 6f * density) / 2f - 4f * density,
            brandingY + appIconSize / 2f
        )

        // Draw app icon background
        val appIconPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FF8565FF") // App brand color
        }
        canvas.drawRoundRect(appIconRect, 6f * density, 6f * density, appIconPaint)

        // Draw "A" letter inside the icon
        val iconTextPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            textSize = 18f * density
        }
        canvas.drawText(
            "A",
            appIconRect.centerX(),
            appIconRect.centerY() + 6f * density,
            iconTextPaint
        )

        // App text next to icon
        val brandingPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.LEFT
            isAntiAlias = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            textSize = 20f * density
        }
        canvas.drawText(
            "Aishou App",
            appIconRect.right + 8f * density, // Spacer(8.dp) after icon
            brandingY + 6f * density, // Vertically centered
            brandingPaint
        )

        println("generateShareableBitmap: ShareableMatchResultCard-style bitmap generated successfully")
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        println("generateShareableBitmap error: ${e.message}")
        e.printStackTrace()
        null
    }
}