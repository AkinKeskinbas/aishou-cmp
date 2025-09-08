package com.keak.aishou.screens.paywall

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun HeartFillMeter(
    modifier: Modifier = Modifier,
    // animasyon hedefi (0..100). 100 olduğunda pulse durur.
    targetPercent: Int = 100,
    fillDurationMillis: Int = 3500,
    pulseScale: Float = 1.08f,      // kalp atışı genliği
    strokeWidth: Dp = 4.dp,
    heartColorTop: Color = Color(0xFFFF6B6B),  // üst renk (açık kırmızı/koral)
    heartColorBottom: Color = Color(0xFFE11D48), // alt renk (rose-600)
    outlineColor: Color = Color(0xFF111111),
    backgroundInside: Color = Color(0x11E11D48), // iç boş kısım hafif tint
    startOnFirstComposition: Boolean = true
) {
    // 0f..1f arası progres
    val progress = remember { Animatable(0f) }

    // Pulse için ayrı bir Animatable kullanıp duruma göre döngüye sokacağız
    val scale = remember { Animatable(1f) }

    // Yüzdeyi hesapla (0..100)
    val percent by derivedStateOf { (progress.value * 100f).coerceIn(0f, 100f) }

    // Doldurma animasyonu
    LaunchedEffect(targetPercent, startOnFirstComposition) {
        if (!startOnFirstComposition) return@LaunchedEffect
        progress.animateTo(
            targetValue = (targetPercent / 100f).coerceIn(0f, 1f),
            animationSpec = tween(fillDurationMillis, easing = LinearEasing)
        )
    }

    // Pulse: progress < 1 iken atan, =1 olunca 1.0’a dön ve dur
    LaunchedEffect(progress.value >= 1f) {
        val done = progress.value >= 0.999f
        if (done) {
            // bitince scale'ı yumuşakça 1f'e getir
            scale.animateTo(1f, tween(200, easing = FastOutSlowInEasing))
        } else {
            // döngü: 1 -> pulseScale -> 1
            while (isActive && progress.value < 0.999f) {
                scale.animateTo(pulseScale, tween(220, easing = FastOutSlowInEasing))
                scale.animateTo(1f, tween(220, easing = FastOutSlowInEasing))
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        HeartCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // kare alan
                .padding(8.dp),
            progress = progress.value,
            scale = scale.value,
            outlineColor = outlineColor,
            strokeWidth = strokeWidth,
            backgroundInside = backgroundInside,
            fillBrush = Brush.verticalGradient(
                colors = listOf(heartColorTop, heartColorBottom)
            )
        )

        // Orta yüzde yazısı
        Text(
            text = "${percent.roundToInt()}%",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun HeartCanvas(
    modifier: Modifier = Modifier,
    progress: Float,                              // 0f..1f
    scale: Float = 1f,                            // pulse scale
    outlineColor: Color = Color.Black,
    strokeWidth: Dp = 4.dp,
    backgroundInside: Color = Color(0x11000000),
    fillBrush: Brush = Brush.verticalGradient(
        listOf(Color(0xFFFF8A80), Color(0xFFD50000))
    )
) {
    Canvas(modifier = modifier) {
        // Kalp path’ini oluştur
        val heart = makeHeartPath(size)

        // Pulse ölçeği uygula
        withTransform({
            val cx = size.width / 2
            val cy = size.height / 2
            translate(left = cx, top = cy)
            scale(scaleX = scale, scaleY = scale)
            translate(left = -cx, top = -cy)
        }) {
            // İç boş alanı çok hafif boya (daha net görsel)
            clipPath(heart) {
                drawRect(color = backgroundInside)
            }

            // İlerlemeye göre aşağıdan yukarı doldur
            val topY = size.height * (1f - progress.coerceIn(0f, 1f))
            clipPath(heart) {
                drawRect(
                    brush = fillBrush,
                    topLeft = Offset(0f, topY),
                    size = Size(size.width, size.height - topY)
                )
            }

            // Dış hat
            drawPath(
                path = heart,
                color = outlineColor,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    join = StrokeJoin.Round,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

/** Basit, dengeli bir kalp path’i (Bezier'lerle). Kare alanda güzel görünür. */
// Simetrik, güvenilir kalp path'i — örnekleme ile (t: 0..2π)
private fun makeHeartPath(size: Size): Path {
    val n = 360                               // örnek sayısı (daha pürüzsüz için arttır)
    val pts = ArrayList<Offset>(n + 1)
    val twoPi = PI * 2.0

    // Parametrik kalp:
    // x(t) = 16 sin^3 t
    // y(t) = 13 cos t − 5 cos 2t − 2 cos 3t − cos 4t
    // (Canvas'ta Y aşağı aktığı için y'yi ters çeviriyoruz)
    var minX = Float.POSITIVE_INFINITY
    var maxX = Float.NEGATIVE_INFINITY
    var minY = Float.POSITIVE_INFINITY
    var maxY = Float.NEGATIVE_INFINITY

    for (i in 0..n) {
        val t = twoPi * i / n
        val x = 16.0 * kotlin.math.sin(t).let { it * it * it } // sin^3
        val y = 13.0 * kotlin.math.cos(t) -
                5.0 * kotlin.math.cos(2 * t) -
                2.0 * kotlin.math.cos(3 * t) -
                kotlin.math.cos(4 * t)

        val p = Offset(x.toFloat(), (-y).toFloat()) // Y eksenini tersle
        pts += p
        if (p.x < minX) minX = p.x
        if (p.x > maxX) maxX = p.x
        if (p.y < minY) minY = p.y
        if (p.y > maxY) maxY = p.y
    }

    // Boyutlandır ve merkeze yerleştir
    val bw = maxX - minX
    val bh = maxY - minY
    val scale = 0.90f * min(size.width / bw, size.height / bh) // %10 margin
    val cx = size.width / 2f
    val cy = size.height / 2f + size.height * 0.02f            // ucu hafif aşağı kaydır
    val ox = (minX + maxX) / 2f
    val oy = (minY + maxY) / 2f

    return Path().apply {
        val p0 = pts.first()
        moveTo(cx + (p0.x - ox) * scale, cy + (p0.y - oy) * scale)
        for (k in 1 until pts.size) {
            val p = pts[k]
            lineTo(cx + (p.x - ox) * scale, cy + (p.y - oy) * scale)
        }
        close()
    }
}




