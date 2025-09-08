package com.keak.aishou

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@Composable
actual fun screenSize(): Size {
    val screenSize = UIScreen.mainScreen.bounds.useContents { size.width to size.height }
    return Size(screenSize.first.toFloat(), screenSize.second.toFloat())
}