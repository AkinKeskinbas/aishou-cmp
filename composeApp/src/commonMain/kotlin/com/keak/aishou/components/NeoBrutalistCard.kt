package com.keak.aishou.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.misc.optionalBackground

@Composable
fun NeoBrutalistCardView(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
    shadowColor: Color = Color.Black,
    borderWith: Dp = 3.dp,
    shadowOffset: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.offset(x = shadowOffset, y = shadowOffset)
            .background(shadowColor, RoundedCornerShape(0.dp))
    ) {
        Box(
            modifier = Modifier.matchParentSize().offset(-shadowOffset, -shadowOffset)
                .background(backgroundColor, RoundedCornerShape(0.dp))
                .border(borderWith, borderColor, RoundedCornerShape(0.dp)),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
@Composable
fun NeoBrutalistCardViewWithFlexSize(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = Color.White,
    backgroundBrush: Brush? =  null,
    borderColor: Color = Color.Black,
    shadowColor: Color = Color.Black,
    borderWidth: Dp = 3.dp,
    shadowOffset: Dp = 6.dp,
    cornerRadius: Dp = 0.dp,
    contentPadding: Dp = 12.dp,
    contentAlignment: Alignment = Alignment.Center,
    showBadge: Boolean = false,
    badgeText: String = "POPULAR!",
    badgeBg: Color = Color(0xFFF87171), // Tailwind red-400
    badgeTextColor: Color = Color.White,
    badgeBorderWidth: Dp = 4.dp,        // Tailwind border-4
    badgeCornerRadius: Dp = 6.dp,
    badgeRotation: Float = 12f,         // rotate-12
    badgeOffsetX: Dp = (16).dp,        // -right-4 ≈ -16dp
    badgeOffsetY: Dp = (-16).dp,        // -top-4  ≈ -16dp
    badgeTextSize: Int = 18,        // -top-4  ≈ -16dp
    badgeHorizontal: Int = 16,        // -top-4  ≈ -16dp
    badgeVertical: Int = 8,        // -top-4  ≈ -16dp
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val badgeShape = RoundedCornerShape(badgeCornerRadius)
    Box(
        modifier = modifier
            // 1) Gölgeyi arka plana çizeceğiz
            .drawBehind {
                val r = cornerRadius.toPx()
                val off = shadowOffset.toPx()
                // Box boyutu (padding eklenmeden önceki alan)
                // Sağ/alt tarafa gölgeyi kaydırıp, iç alan kadar dikdörtgen çiziyoruz
                drawRoundRect(
                    color = shadowColor,
                    topLeft = androidx.compose.ui.geometry.Offset(off, off),
                    size = androidx.compose.ui.geometry.Size(
                        width = size.width - off,
                        height = size.height - off
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
                )
            }
            // 2) Gölgeye yer aç: sağ & alt
            .padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        // 3) Asıl kart
        Box(
            modifier = Modifier
                .fillMaxWidth() // Row.weight ile verilen genişliği doldur
                .optionalBackground(shape, color = backgroundColor, brush = backgroundBrush)
                .border(borderWidth, borderColor, shape)
                .padding(contentPadding),
            contentAlignment = contentAlignment,
            content = content
        )
        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = badgeOffsetX, y = badgeOffsetY)
                    .graphicsLayer { rotationZ = badgeRotation }
                    .background(badgeBg, badgeShape)
                    .border(badgeBorderWidth, Color.Black, badgeShape)
                    .padding(horizontal = badgeHorizontal.dp, vertical = badgeVertical.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeTextColor,
                    fontSize = badgeTextSize.sp,           // ~ text-xl
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

