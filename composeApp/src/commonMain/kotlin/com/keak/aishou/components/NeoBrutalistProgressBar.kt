package com.keak.aishou.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil

@Composable
fun NeoBrutalistProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    barHeight: Dp = 24.dp,
    trackColor: Color = Color.White,
    fillColor: Color = Color(0xFF34D399), // Tailwind emerald-400
    borderColor: Color = Color.Black,
    borderWidth: Dp = 3.dp,
    cornerRadius: Dp = 0.dp,
    showStripes: Boolean = true
) {
    val target = if (total > 0) (current.coerceAtMost(total)).toFloat() / total.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val stripeShift by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "stripeShift"
    )

    Box(
        modifier = modifier
            .height(barHeight)
            .fillMaxWidth()
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
            val bw = borderWidth.toPx()
            val rr = cornerRadius.toPx()

            // Track (zemin)
            drawRoundRect(
                color = trackColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(rr, rr)
            )

            // Dolu kısım (clipRect ile genişlik kontrolü)
            val w = size.width * animatedProgress
            clipRect(right = w) {
                // Dolgu
                drawRoundRect(
                    color = fillColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(rr, rr)
                )

                // Opsiyonel hareketli çizgiler (Neo-brutalist hissi)
                if (showStripes && w > 0f) {
                    val spacing = 24f
                    val thickness = 4f
                    val diagonal = size.height // ~45°
                    val offset = stripeShift * spacing

                    val steps = ceil((size.width + size.height) / spacing).toInt() + 2
                    for (i in -1..steps) {
                        val x = i * spacing + offset
                        drawLine(
                            color = Color.White.copy(alpha = 0.25f),
                            start = Offset(x, 0f),
                            end = Offset(x - diagonal, diagonal),
                            strokeWidth = thickness
                        )
                    }
                }
            }

            // Kalın sınır (üstte)
            drawRoundRect(
                color = borderColor,
                style = Stroke(width = bw),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(rr, rr)
            )
        }
    }
}