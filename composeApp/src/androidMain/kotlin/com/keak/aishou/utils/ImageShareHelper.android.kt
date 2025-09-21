package com.keak.aishou.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    actual fun create(): ImageShareHelper {
        return ImageShareHelper(context)
    }
}

// For Android, we'll use a simpler approach for capturing composables
actual suspend fun captureComposableAsBitmap(
    content: @Composable () -> Unit
): ImageBitmap? {
    // Note: This is a simplified version. In a real implementation,
    // you might want to use ComposeView and capture it as bitmap
    // For now, we'll return null and handle it in the UI layer
    return null
}