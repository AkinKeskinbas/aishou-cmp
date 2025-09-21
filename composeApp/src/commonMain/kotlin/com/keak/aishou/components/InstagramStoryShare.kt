package com.keak.aishou.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.data.api.SoloResult

@Composable
fun InstagramStoryShareCard(
    soloResult: SoloResult,
    modifier: Modifier = Modifier
) {

    // Instagram story dimensions: 1080x1920 (9:16 ratio)
    // We'll use 270x480 dp (scaled down version)
    Box(
        modifier = modifier
            .width(270.dp)
            .height(480.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF6B73FF),
                        Color(0xFF000DFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .border(6.dp, Color.Black, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Compact Header with App Title and Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo/Title - Compact
                NeoBrutalistCardViewWithFlexSize(
                    backgroundColor = Color.White,
                    borderColor = Color.Black,
                    borderWidth = 2.dp,
                    shadowColor = Color.Black,
                    shadowOffset = 3.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "🎯 AISHOU",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Compact Score Circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawScoreCircle(soloResult.totalScore)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${soloResult.totalScore}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "PT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Key Insight
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = Color(0xFFFFF3CD),
                borderColor = Color.Black,
                borderWidth = 3.dp,
                shadowColor = Color.Black,
                shadowOffset = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💡 KEY INSIGHT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = extractKeyInsight(soloResult),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Call to Action with Link
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = null,
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(Color(0xFF06FFA5), Color(0xFF4FFFB3))
                ),
                borderColor = Color.Black,
                borderWidth = 3.dp,
                shadowColor = Color.Black,
                shadowOffset = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚀 Benimle Birlikte Çöz!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "AISHOU.APP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun DrawScope.drawScoreCircle(score: Int) {
    val strokeWidth = 12.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    // Background circle
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = radius,
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
    )

    // Progress circle
    val sweepAngle = (score / 100f) * 360f
    drawArc(
        color = Color.White,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}

private fun extractKeyInsight(soloResult: SoloResult): String {
    // Priority 1: Use AI insights if available
    if (!soloResult.personalizedInsights.isNullOrBlank()) {
        val insights = soloResult.personalizedInsights

        // Extract first meaningful sentence (ends with . ! or ?)
        val sentences = insights.split(Regex("[.!?]")).filter { it.trim().length > 20 }
        if (sentences.isNotEmpty()) {
            val firstSentence = sentences[0].trim()
            return if (firstSentence.length > 80) {
                "${firstSentence.take(75)}..."
            } else {
                firstSentence
            }
        }
    }

    // Priority 2: Use score-based insight
    return when {
        soloResult.totalScore >= 90 -> "Mükemmel bir kişilik profili! Hem analitik hem de empatiğin."
        soloResult.totalScore >= 80 -> "Güçlü bir kişiliğe sahipsin! Liderlik özeliklerin öne çıkıyor."
        soloResult.totalScore >= 70 -> "Dengeli bir karakterin var. İlişkilerde başarılısın."
        soloResult.totalScore >= 60 -> "Yaratıcı ruhun ve sosyal yeteneklerin dikkat çekici."
        else -> "Benzersiz kişiliğin ve potansiyelin keşfedilmeyi bekliyor!"
    }
}

