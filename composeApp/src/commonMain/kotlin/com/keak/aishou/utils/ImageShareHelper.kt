package com.keak.aishou.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

expect class ImageShareHelper {
    suspend fun shareComposableAsImage(
        imageBitmap: ImageBitmap,
        fileName: String = "aishou_result.png"
    )

    suspend fun shareToInstagramStory(
        imageBitmap: ImageBitmap,
        fileName: String = "aishou_story.png"
    )
}

expect object ImageShareHelperFactory {
    fun create(): ImageShareHelper
}

// Compose component capture helper
expect suspend fun captureComposableAsBitmap(
    content: @Composable () -> Unit
): ImageBitmap?