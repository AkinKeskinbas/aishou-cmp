package com.keak.aishou.screens.quicktestscreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.brain
import aishou.composeapp.generated.resources.crown
import aishou.composeapp.generated.resources.right_arrow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BrutalCard(
    title: String,
    subtitle: String,
    gradient: List<Color>,
    shadowColor: Color,
    isPremium: Boolean,
    cardImage: DrawableResource,
    onClick: () -> Unit,
) {
    NeoBrutalistHeader(
        background = Brush.linearGradient(gradient),
        borderWidth = 4.dp,
        borderColor = Color.Black,
        shadowOffset = 10.dp,
        cornerRadius = 0.dp,
        shadowColor = shadowColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .clickable { onClick() }
    ) {
        Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Image(
                    painter = painterResource(cardImage),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )

            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = Color.Black,
                borderColor = Color.White,
                modifier = Modifier.fillMaxWidth(),

                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.right_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isPremium) "Premium Test" else "Start Test",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier,
                        color = Color.White
                    )
                }

            }
        }
    }
}
@Composable
fun NeoBrutalistHeader(
    modifier: Modifier = Modifier,
    background: Brush,
    borderWidth: Dp,
    borderColor: Color,
    shadowOffset: Dp,
    cornerRadius: Dp,
    shadowColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape: Shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .drawBehind {
                val off = shadowOffset.toPx()
                drawRoundRect(
                    color = shadowColor,
                    topLeft = androidx.compose.ui.geometry.Offset(off, off),
                    size = androidx.compose.ui.geometry.Size(size.width - off, size.height - off),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        cornerRadius.toPx(), cornerRadius.toPx()
                    )
                )
            }
            .padding(end = shadowOffset, bottom = shadowOffset)
            .background(brush = background, shape = shape)
            .border(width = borderWidth, color = borderColor, shape = shape)
    ) {
        Column(Modifier.fillMaxWidth()) { content() }
    }
}