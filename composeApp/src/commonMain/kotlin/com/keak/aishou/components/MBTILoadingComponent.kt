package com.keak.aishou.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MBTILoadingOverlay() {
    var currentStepIndex by remember { mutableStateOf(0) }

    val loadingSteps = listOf(
        "🧠 Analyzing your responses...",
        "🔍 Identifying personality patterns...",
        "⚡ Processing cognitive functions...",
        "✨ Determining your MBTI type...",
        "🎯 Matching with zodiac traits...",
        "📊 Generating insights..."
    )

    // Cycle through loading steps
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // Change every 2 seconds
            currentStepIndex = (currentStepIndex + 1) % loadingSteps.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            backgroundColor = Color.White,
            borderColor = Color.Black,
            shadowColor = Color.Black,
            borderWidth = 4.dp,
            shadowOffset = 8.dp,
            contentPadding = 32.dp,
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated brain emoji
                AnimatedBrainEmoji()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Analyzing Your Personality",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animated loading steps
                AnimatedText(
                    text = loadingSteps[currentStepIndex],
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Animated dots
                LoadingDots()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This may take a few moments...",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AnimatedBrainEmoji() {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        text = "🧠",
        fontSize = 48.sp,
        modifier = Modifier.scale(scale)
    )
}

@Composable
private fun AnimatedText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    color: Color,
    textAlign: TextAlign
) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500)
    )

    Text(
        text = text,
        fontSize = fontSize,
        color = color,
        textAlign = textAlign,
        modifier = Modifier.alpha(alpha)
    )
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0..2) {
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(i * 200)
                )
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .alpha(alpha)
                    .background(
                        Color(0xFF4FFFB3),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun MBTIQuickLoadingIndicator() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🧠",
            fontSize = 16.sp
        )

        Text(
            text = "Analyzing...",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        // Small animated dots
        val infiniteTransition = rememberInfiniteTransition()

        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 150)
                )
            )

            Box(
                modifier = Modifier
                    .size(4.dp)
                    .alpha(alpha)
                    .background(Color.Gray, CircleShape)
            )
        }
    }
}