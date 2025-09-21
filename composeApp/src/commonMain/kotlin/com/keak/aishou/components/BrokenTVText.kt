package com.keak.aishou.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun BrokenTVText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.White
) {
    var glitchState by remember { mutableStateOf(0) }
    var flickerState by remember { mutableStateOf(true) }
    var staticNoise by remember { mutableStateOf("") }

    // Glitch animation
    val glitchOffset by animateFloatAsState(
        targetValue = when (glitchState) {
            0 -> 0f
            1 -> Random.nextFloat() * 4f - 2f
            2 -> Random.nextFloat() * 8f - 4f
            else -> 0f
        },
        animationSpec = tween(50, easing = LinearEasing),
        label = "glitch"
    )

    // Scale animation for distortion
    val scaleX by animateFloatAsState(
        targetValue = when (glitchState) {
            0 -> 1f
            1 -> 0.98f + Random.nextFloat() * 0.04f
            2 -> 0.95f + Random.nextFloat() * 0.1f
            else -> 1f
        },
        animationSpec = tween(100, easing = LinearEasing),
        label = "scale"
    )

    // Flicker alpha
    val alpha by animateFloatAsState(
        targetValue = if (flickerState) 1f else 0.3f,
        animationSpec = tween(Random.nextInt(50, 200)),
        label = "flicker"
    )

    // Random glitch trigger
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(100, 800))
            glitchState = when (Random.nextInt(10)) {
                in 0..6 -> 0 // Normal 70%
                in 7..8 -> 1 // Light glitch 20%
                else -> 2     // Heavy glitch 10%
            }

            // Generate static noise occasionally
            if (Random.nextFloat() < 0.15f) {
                staticNoise = generateStaticNoise()
                delay(Random.nextLong(50, 150))
                staticNoise = ""
            }
        }
    }

    // Flicker effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(80, 400))
            flickerState = !flickerState
            delay(Random.nextLong(20, 100))
            flickerState = !flickerState
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Main text with glitch effects
        Text(
            text = if (staticNoise.isNotEmpty()) staticNoise else text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color.copy(red = color.red + (glitchState * 0.1f).coerceAtMost(1f)),
            modifier = Modifier
                .offset(x = glitchOffset.dp, y = (glitchOffset * 0.3f).dp)
                .scale(scaleX = scaleX, scaleY = 1f)
                .alpha(alpha),
            textAlign = TextAlign.Center
        )

        // Red ghost text for chromatic aberration
        if (glitchState > 0) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = Color.Red.copy(alpha = 0.3f),
                modifier = Modifier
                    .offset(x = (glitchOffset + 1f).dp, y = 0.dp)
                    .alpha(alpha * 0.7f),
                textAlign = TextAlign.Center
            )
        }

        // Blue ghost text for chromatic aberration
        if (glitchState > 1) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = Color.Cyan.copy(alpha = 0.2f),
                modifier = Modifier
                    .offset(x = (glitchOffset - 1f).dp, y = 0.dp)
                    .alpha(alpha * 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun generateStaticNoise(): String {
    val noiseChars = "█▓▒░▄▀▐▌▆▇■□▪▫"
    return (1..Random.nextInt(8, 15))
        .map { noiseChars.random() }
        .joinToString("")
}