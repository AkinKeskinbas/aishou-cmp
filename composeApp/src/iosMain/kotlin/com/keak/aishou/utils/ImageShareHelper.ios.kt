package com.keak.aishou.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import platform.UIKit.*
import platform.Foundation.*
import kotlinx.cinterop.*

actual class ImageShareHelper {

    actual suspend fun shareComposableAsImage(
        imageBitmap: ImageBitmap,
        fileName: String
    ) {
        try {
            // Convert ImageBitmap to UIImage
            val uiImage = imageBitmap.toUIImage()

            val activityViewController = UIActivityViewController(
                activityItems = listOf(uiImage),
                applicationActivities = null
            )

            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                activityViewController,
                animated = true,
                completion = null
            )
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
            val instagramUrl = "instagram-stories://share"
            val url = NSURL.URLWithString(instagramUrl)

            if (UIApplication.sharedApplication.canOpenURL(url!!)) {
                // Convert ImageBitmap to UIImage
                val uiImage = imageBitmap.toUIImage()

                // Save to pasteboard for Instagram
                UIPasteboard.generalPasteboard.setData(
                    UIImagePNGRepresentation(uiImage)!!,
                    forPasteboardType = "public.png"
                )

                // Open Instagram Stories
                UIApplication.sharedApplication.openURL(url)
            } else {
                // Instagram not available, fallback to regular share
                shareComposableAsImage(imageBitmap, fileName)
            }
        } catch (e: Exception) {
            println("ImageShareHelper: Error sharing to Instagram: ${e.message}")
            e.printStackTrace()
            // Fallback to regular share
            shareComposableAsImage(imageBitmap, fileName)
        }
    }

    private fun ImageBitmap.toUIImage(): UIImage {
        // This is a simplified conversion
        // In a real implementation, you'd need proper bitmap-to-UIImage conversion
        // For now, we'll create a placeholder
        return UIImage.imageNamed("placeholder") ?: UIImage()
    }
}

actual object ImageShareHelperFactory {
    actual fun create(): ImageShareHelper {
        return ImageShareHelper()
    }
}

actual suspend fun captureComposableAsBitmap(
    content: @Composable () -> Unit
): ImageBitmap? {
    // For iOS, this would require creating a UIView from the Composable
    // This is complex and would need native iOS implementation
    // For now, we'll return null and handle it in the UI layer
    return null
}