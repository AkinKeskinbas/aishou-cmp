package com.keak.aishou.misc

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object BackGroundBrush {
     val neoBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFA78BFA), // purple-400
            Color(0xFFF9A8D4), // pink-300
            Color(0xFFFDE68A)  // yellow-300
        )
    )
    val homNeoBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE9D5FF), // purple-200
            Color(0xFFFBCFE8), // pink-200
            Color(0xFFFEF08A)  // yellow-200
        ),
        start = Offset.Zero,        // top-left
        end = Offset.Infinite       // bottom-right
    )
    val homeHeaderHeartBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFA78BFA), // purple-400
            Color(0xFFE91E63), // pink-500
        )
    )
}
