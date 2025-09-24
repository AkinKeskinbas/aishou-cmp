package com.keak.aishou.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.data.api.TestResultResponse

@Composable
fun ShareableMatchResultCard(
    testResult: TestResultResponse,
    userDisplayName: String
) {
    Box(
        modifier = Modifier
            .size(400.dp, 600.dp) // Instagram Story dimensions (16:9 aspect ratio)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8565FF),
                        Color(0xFF6B46C1)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎯",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "My Test Results",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Result Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 4.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // User name
                    Text(
                        text = userDisplayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Solo result if available
                    testResult.soloResult?.let { solo ->
                        Text(
                            text = "Score: ${solo.totalScore}/100",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF8565FF),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = testResult.resultType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Compatibility results summary
                    if (testResult.compatibilityResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "💕 ${testResult.compatibilityResults.size} Compatibility Results",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B6B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✨ Take your own test ✨",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AISHOU",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Alternative approach: Generate bitmap programmatically
expect fun generateShareableBitmap(
    testResult: TestResultResponse,
    userDisplayName: String
): ImageBitmap?