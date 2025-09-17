package com.keak.aishou.screens.quicktestscreen

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class TestCardModel(
    val title: String,
    val subtitle: String,
    val from: Color,
    val via: Color,
    val to: Color,
    val shadowColor: Color,
    val cardImage: DrawableResource
)