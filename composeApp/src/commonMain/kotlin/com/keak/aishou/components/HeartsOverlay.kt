package com.keak.aishou.components// imports:
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * React'teki map(...) ile çok benzer: hearts listesini dolaşır,
 * her kalbi ekrana random konumda, farklı süre/delay ile yerleştirip animasyonlar.
 */
@Composable
fun HeartsOverlay(
    showScore: Boolean,
    hearts: List<String>,                 // React'teki "hearts.map((heart) => ...)"
    modifier: Modifier = Modifier,
    heartSize: Dp = 16.dp,                // "w-4 h-4"
    amplitude: Dp = 20.dp,                // animate-bounce yüksekliği
    color: Color = Color(0xFFEC4899),     // Tailwind pink-500
    baseDelayStepMs: Int = 50             // index * 50ms
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        val maxW = maxWidth
        val maxH = maxHeight

        // showScore false ise hiç çizme (performans/temizlik)
        if (!showScore) return@BoxWithConstraints

        hearts.forEachIndexed { index, id ->
            // Her kalp için sabit random parametreler (recomposition'da değişmesin diye remember)
            val fracX = remember(id) { Random.nextFloat() }               // 0..1
            val fracY = remember(id) { Random.nextFloat() }               // 0..1
            val bounceMs = remember(id) { Random.nextInt(2000, 4001) }    // 2s..4s
            val pulseMs = remember(id) { Random.nextInt(900, 1501) }      // ~1s..1.5s
            val delayMs = remember(id) { index * baseDelayStepMs }

            // Ekrandan taşmaması için "left/top" konumunu (top-left anchor) güvenli hesapla:
            val safeX = (maxW - heartSize) * fracX
            val safeY = (maxH - heartSize) * fracY

            FloatingHeart(
                x = safeX,
                y = safeY,
                size = heartSize,
                amplitude = amplitude,
                bounceDurationMs = bounceMs,
                pulseDurationMs = pulseMs,
                delayMillis = delayMs,
                color = color
            )
        }
    }
}

/**
 * Tek bir kalbin "absolute" yerleşimi + bounce (yukarı-aşağı) + pulse (ölçek)
 */
@Composable
private fun FloatingHeart(
    x: Dp,
    y: Dp,
    size: Dp,
    amplitude: Dp,
    bounceDurationMs: Int,
    pulseDurationMs: Int,
    delayMillis: Int,
    color: Color
) {
    val transition = rememberInfiniteTransition(label = "heart-$delayMillis")
    val ampPx = with(LocalDensity.current) { amplitude.toPx() }

    // Smooth bounce: 0→1→0 (Reverse) — başa dönüşte zıplama yok
    val bounce by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = bounceDurationMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delayMillis)
        ),
        label = "bounce"
    )

    // Pulse: 0.9x ↔ 1.1x
    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = pulseDurationMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset((delayMillis * 0.7f).roundToInt())
        ),
        label = "pulse"
    )

    // Yukarı-aşağı offset (px) — negatif yukarı gider
    val yOffsetPx = -ampPx * bounce

    Icon(
        imageVector = Icons.Filled.Favorite,
        contentDescription = "heart",
        tint = color,
        modifier = Modifier
            // absolute gibi konumla
            .offset(x = x, y = y)
            // z-index küçükçe artır, diğer içeriklerle üstte kalsın
            .zIndex(1f)
            .graphicsLayer {
                translationY = yOffsetPx
                scaleX = scale
                scaleY = scale
            }
            .size(size)
    )
}
