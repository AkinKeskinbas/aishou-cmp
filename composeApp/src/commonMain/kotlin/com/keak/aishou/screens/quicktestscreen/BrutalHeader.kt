package com.keak.aishou.screens.quicktestscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset

@Composable
fun BrutalHeader(
    modifier: Modifier,
    headerText: String,
    textSize: Int
) {
    val density = LocalDensity.current
    val shadowPx = with(density) { 4.dp.toPx() } // drop-shadow-[8px_8px_0px_white]

    Box(
        modifier = modifier // mb-6
    ) {
        // Sol "Zap" yerine Star; istersen FlashOn ikonunu import edip kullanabilirsin

        Text(
            text = headerText,
            // drop-shadow + skew-x-12 eşleniği
            style = TextStyle(
                fontSize = textSize.sp,               // md:text-7xl
                fontWeight = FontWeight.Black,
                color = Color.Black,
                shadow = Shadow(
                    color = Color.White,
                    offset = Offset(shadowPx, shadowPx),
                    blurRadius = 0f
                ),
                textGeometricTransform = TextGeometricTransform(
                    skewX = -0.2f                 // yaklaşık -skew-x-12
                )
            ),
            textAlign = TextAlign.Center
        )

    }
}
