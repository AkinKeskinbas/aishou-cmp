package com.keak.aishou.misc

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}

fun Int?.orZero():Int{
    return this ?: 0
}
fun Modifier.optionalBackground(
    shape: Shape,
    color: Color? = null,
    brush: Brush? = null
): Modifier = when {
    brush != null -> this.background(brush, shape)
    color != null -> this.background(color, shape)
    else -> this
}