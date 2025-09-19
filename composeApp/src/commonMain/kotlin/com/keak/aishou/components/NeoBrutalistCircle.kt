package com.keak.aishou.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NeoBrutalistCircle(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Black,
    shadowColor: Color = Color.Black,
    borderWith: Dp = 3.dp,
    shadowOffset: Dp = 6.dp,
    backgroundBrush: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFEC4899),
            Color(0xFFEF4444),
            Color(0xFFEC4899),
        ),
        start = Offset(0f,0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    ),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .offset(x = shadowOffset, y = shadowOffset)
            .background(shadowColor, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = -shadowOffset, y = -shadowOffset)
                .background(brush = backgroundBrush, shape = CircleShape)
                .border(borderWith, borderColor, shape = CircleShape)
                ,
            contentAlignment = Alignment.Center
        ) {
            content()

        }
    }
}
