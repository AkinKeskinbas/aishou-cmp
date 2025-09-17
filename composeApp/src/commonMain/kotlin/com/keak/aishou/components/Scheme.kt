// --- imports ---
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt

// ============== 1) Senin panelin (aynen) ==============
@Composable
fun NeoBrutalistPanel(
    modifier: Modifier = Modifier,
    background: Color = Color.White,
    borderWidth: Dp = 6.dp,
    borderColor: Color = Color.Black,
    shadowOffset: Dp = 8.dp,
    cornerRadius: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape: Shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .drawBehind {
                val off = shadowOffset.toPx()
                drawRoundRect(
                    color = Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(off, off),
                    size = androidx.compose.ui.geometry.Size(size.width - off, size.height - off),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        cornerRadius.toPx(), cornerRadius.toPx()
                    )
                )
            }
            .padding(end = shadowOffset, bottom = shadowOffset)
            .background(background, shape)
            .border(borderWidth, borderColor, shape)
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

// ============== 2) Renk paleti & yardımcılar ==============
private data class Scheme(
    val fillStart: Color, val fillEnd: Color,
    val chipBg: Color, val chipBorder: Color, val chipText: Color
)

private val S0  = Scheme(Color(0xFFF87171), Color(0xFFEF4444), Color(0xFFF87171), Color(0xFFDC2626), Color(0xFF450A0A)) // 0..40
private val S40 = Scheme(Color(0xFFF97316), Color(0xFFEA580C), Color(0xFFF97316), Color(0xFFEA580C), Color(0xFF431407)) // 40..60
private val S60 = Scheme(Color(0xFFFACC15), Color(0xFFEAB308), Color(0xFFFACC15), Color(0xFFEAB308), Color(0xFF422006)) // 60..80
private val S80 = Scheme(Color(0xFF22C55E), Color(0xFF16A34A), Color(0xFF4ADE80), Color(0xFF16A34A), Color(0xFF052E16)) // 80..100

private fun lerpScheme(a: Scheme, b: Scheme, t: Float) = Scheme(
    fillStart = lerp(a.fillStart, b.fillStart, t),
    fillEnd   = lerp(a.fillEnd,   b.fillEnd,   t),
    chipBg    = lerp(a.chipBg,    b.chipBg,    t),
    chipBorder= lerp(a.chipBorder,b.chipBorder,t),
    chipText  = lerp(a.chipText,  b.chipText,  t),
)

private fun schemeForPercent(p: Float): Scheme {
    val x = p.coerceIn(0f, 100f)
    return when {
        x < 40f -> lerpScheme(S0,  S40, (x - 0f)  / 40f)
        x < 60f -> lerpScheme(S40, S60, (x - 40f) / 20f)
        x < 80f -> lerpScheme(S60, S80, (x - 60f) / 20f)
        else    -> S80
    }
}

private fun labelFor(p: Float) = when {
    p >= 80f -> "SOUL MATES"
    p >= 60f -> "GREAT MATCH"
    p >= 40f -> "GOOD VIBES"
    else     -> "WORK IN PROGRESS"
}

@Composable
private fun MatchChip(text: String, bg: Color, border: Color, txt: Color) {
    Box(
        modifier = Modifier
            .border(4.dp, Color.Black, RoundedCornerShape(6.dp))
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) { Text(text, color = txt, fontSize = 14.sp, fontWeight = FontWeight.Black) }
}

// ============== 3) Panel içinde dinamik progress ==============
@Composable
fun MatchProgressCard(
    score: Int,
    maxScore: Int,
    modifier: Modifier = Modifier,
    animateMillis: Int = 1400,
    easing: Easing = FastOutSlowInEasing,
    barHeight: Dp = 24.dp,
    cornerRadius: Dp = 8.dp,
    showHeaderScore: Boolean = true,
    percentAlignment: Alignment = Alignment.Center,
    percentPadding: Dp = 8.dp
) {
    val safeMax = maxOf(maxScore, 1)
    val rawTargetPercent = (score.toFloat() / safeMax) * 100f
    val clampedTargetPercent = rawTargetPercent.coerceIn(0f, 100f)
    val targetFraction = clampedTargetPercent / 100f

    // 0 → hedefe akıcı animasyon (progress için)
    val anim = remember { Animatable(0f) }
    LaunchedEffect(clampedTargetPercent) {
        anim.stop()
        anim.snapTo(0f)
        anim.animateTo(
            targetValue = targetFraction,
            animationSpec = tween(durationMillis = animateMillis, easing = easing)
        )
    }

    // ---- YENİ: Skoru 0'dan saydır ----
    // anim.value 0..targetFraction aralığında; 0..1'e normalize edip skoru onunla sayıyoruz.
    val normalized = if (targetFraction > 0f) (anim.value / targetFraction).coerceIn(0f, 1f) else 0f
    val displayScore = (normalized * score).roundToInt()

    val currentPercent = anim.value * 100f
    val scheme = schemeForPercent(currentPercent)
    val label = labelFor(currentPercent)
    val shape = RoundedCornerShape(cornerRadius)

    NeoBrutalistPanel(
        modifier = modifier,
        background = Color.White,
        borderWidth = 6.dp,
        borderColor = Color.Black,
        shadowOffset = 8.dp,
        cornerRadius = 0.dp
    ) {
        if (showHeaderScore) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = displayScore.toString(),     // ← 0'dan artıyor
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "/$safeMax",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B5563)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Dinamik chip (renk/etiket yüzde ile smooth)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MatchChip(text = label, bg = scheme.chipBg, border = scheme.chipBorder, txt = scheme.chipText)
        }

        Spacer(Modifier.height(16.dp))

        // Progress bar (yüzde içerde)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .border(4.dp, Color.Black, shape)
                .background(Color(0xFFF5F5F5), shape)
        ) {
            // dolan kısım
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = anim.value)
                    .background(Brush.horizontalGradient(listOf(scheme.fillStart, scheme.fillEnd)), shape)
            )
            // yüzde metni (içerde)
            val percentInt = currentPercent.roundToInt()
            val percentTextColor = if (anim.value > 0.15f) Color.White else Color.Black
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = percentPadding),
                contentAlignment = percentAlignment
            ) {
                Text(
                    text = "$percentInt%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = percentTextColor
                )
            }
        }
    }
}


// ============== 4) Örnek kullanım ==============
@Composable
fun MatchProgressCardPreview() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MatchProgressCard(
            score = 63, maxScore = 100,
            modifier = Modifier.width(340.dp),
            animateMillis = 1400,
            barHeight = 26.dp,
            cornerRadius = 8.dp,
            percentAlignment = Alignment.Center
        )
    }
}
