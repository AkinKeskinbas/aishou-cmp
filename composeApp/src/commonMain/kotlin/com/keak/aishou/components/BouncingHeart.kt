// imports
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity

// Yumuşak geçiş için helper
private fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
    val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

/**
 * Akıcı (jitter yok), aşağı-yukarı zıplayan kalp.
 * - Döngü: tek tween + RepeatMode.Reverse (başa dönüşte atlama yok)
 * - Çarpışma: dip yakınında smoothstep ile squash & stretch
 */
@Composable
fun BouncingHeartSmooth(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    amplitude: Dp = 18.dp,          // ne kadar aşağı insin
    cycleMillis: Int = 800,        // tek gidiş süresi (yukarı→aşağı); reverse ile geri dönüş aynı
    color: Color = Color(0xFFE11D48),
    impact: Float = 0f,          // squash gücü (0..0.3 önerilir)
    impactWindow: Float = 0.10f,    // dip çevresinde etkilesin (0..0.4)
    showShadow: Boolean = true
) {
    val density = LocalDensity.current
    val ampPx = with(density) { amplitude.toPx() }

    val infinite = rememberInfiniteTransition(label = "bounce")

    // Faz 0f→1f, sonra 1f→0f (smooth)
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = cycleMillis,
                easing = FastOutSlowInEasing   // akıcı ease-in-out
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    // Yukarıdan aşağı konum (px)
    val yPx = phase * ampPx

    // Dipte yumuşak squash: phase 1'e yaklaştıkça artar, reverse'te de pürüzsüz
    val squash = impact * smoothstep(1f - impactWindow, 1f, phase)
    val sx = 1f + squash      // yatay genişle
    val sy = 1f - squash      // dikey kısal

    // Gölge (dipte genişleyip koyulaşsın) — tamamen akıcı fonksiyonlar
    val shadowScale = 0.8f + 0.4f * phase
    val shadowAlpha = 0.18f + 0.32f * phase

    // Toplam yükseklik: kalp boyu + hareket payı
    val containerHeight = size + amplitude

    Box(
        modifier = modifier
            .width(size)
            .height(containerHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        if (showShadow) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        scaleX = shadowScale
                        alpha = shadowAlpha
                    }
                    .size(width = size * 0.9f, height = 18.dp)
            ) {
                drawOval(color = Color.Black.copy(alpha = 0.25f))
            }
        }

        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "bouncing heart",
            tint = color,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationY = yPx
                    scaleX = sx
                    scaleY = sy
                }
                .size(size)
        )
        // İstersen ikon yerine emoji:
        // Text("❤️", fontSize = with(LocalDensity.current) { size.toSp() }, modifier = Modifier
        //   .align(Alignment.TopCenter)
        //   .graphicsLayer { translationY = yPx; scaleX = sx; scaleY = sy })
    }
}
