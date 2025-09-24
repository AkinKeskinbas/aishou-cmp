package com.keak.aishou.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
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

            // Instagram Stories specific intent
            val instagramIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                putExtra("interactive_asset_uri", imageUri)
                type = "image/png"
                setPackage("com.instagram.android")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Check if Instagram is available
            if (context.packageManager.resolveActivity(instagramIntent, 0) != null) {
                context.startActivity(instagramIntent)
            } else {
                // Fallback to regular share if Instagram is not available
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
}

actual object ImageShareHelperFactory {
    private lateinit var _context: Context

    val context: Context get() = _context

    fun initialize(context: Context) {
        this._context = context.applicationContext
    }

    actual fun create(): ImageShareHelper {
        return ImageShareHelper(_context)
    }
}

// For Android, create a composable bitmap using ComposeView
actual suspend fun captureComposableAsBitmap(
    content: @Composable () -> Unit
): ImageBitmap? = withContext(Dispatchers.Main) {
    suspendCoroutine { continuation ->
        try {
            val context = ImageShareHelperFactory.context
            val composeView = ComposeView(context)

            // Set the composable content
            composeView.setContent(content)

            // Measure and layout the view - use pixel values for Instagram Story size
            val density = context.resources.displayMetrics.density
            val widthPx = (400 * density).toInt()  // 400dp in pixels
            val heightPx = (600 * density).toInt() // 600dp in pixels
            val widthSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)

            composeView.measure(widthSpec, heightSpec)
            composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

            // Create bitmap
            val bitmap = Bitmap.createBitmap(
                composeView.measuredWidth,
                composeView.measuredHeight,
                Bitmap.Config.ARGB_8888
            )

            // Draw the view to bitmap
            val canvas = Canvas(bitmap)
            composeView.draw(canvas)

            continuation.resume(bitmap.asImageBitmap())
        } catch (e: Exception) {
            println("captureComposableAsBitmap error: ${e.message}")
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
        val density = ImageShareHelperFactory.context.resources.displayMetrics.density
        val widthPx = (400 * density).toInt()
        val heightPx = (600 * density).toInt()

        // Create bitmap with gradient background
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw gradient background
        val paint = android.graphics.Paint().apply {
            shader = android.graphics.LinearGradient(
                0f, 0f, 0f, heightPx.toFloat(),
                intArrayOf(
                    android.graphics.Color.parseColor("#8565FF"),
                    android.graphics.Color.parseColor("#6B46C1")
                ),
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)

        // Add text elements
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        // Title
        textPaint.textSize = 28f * density
        textPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText(
            "My Test Results",
            widthPx / 2f,
            100f * density,
            textPaint
        )

        // User name and score
        testResult.soloResult?.let { solo ->
            textPaint.textSize = 22f * density
            canvas.drawText(
                userDisplayName,
                widthPx / 2f,
                300f * density,
                textPaint
            )

            textPaint.textSize = 32f * density
            textPaint.color = android.graphics.Color.parseColor("#8565FF")
            canvas.drawText(
                "Score: ${solo.totalScore}/100",
                widthPx / 2f,
                360f * density,
                textPaint
            )
        }

        // AISHOU branding
        textPaint.color = android.graphics.Color.WHITE
        textPaint.textSize = 24f * density
        canvas.drawText(
            "AISHOU",
            widthPx / 2f,
            (heightPx - 50f * density),
            textPaint
        )

        bitmap.asImageBitmap()
    } catch (e: Exception) {
        println("generateShareableBitmap error: ${e.message}")
        e.printStackTrace()
        null
    }
}