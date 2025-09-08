package com.keak.aishou

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
actual fun screenSize(): Size {
    val context = LocalConfiguration.current
    return Size(context.screenWidthDp.toFloat(), context.screenHeightDp.toFloat())
}