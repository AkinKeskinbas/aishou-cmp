package com.keak.aishou.screens.matching

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.keak.aishou.data.models.UserMatch
import com.keak.aishou.data.models.UserInfo
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.screens.allresults.TestResultViewModel
import com.keak.aishou.data.api.TestResultResponse
import com.keak.aishou.data.api.ResultType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.keak.aishou.components.SendToFriendBottomSheet

@Composable
fun UserMatchScreen(
    testID: String,
    router: Router,
    viewModel: TestResultViewModel = koinViewModel()
) {
    val testResult by viewModel.testResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val showSendToFriendBottomSheet by viewModel.showSendToFriendBottomSheet.collectAsStateWithLifecycle()
    val inviteLink by viewModel.inviteLink.collectAsStateWithLifecycle()

    // Load test results when screen starts
    LaunchedEffect(testID) {
        viewModel.loadTestResults(testID)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                // Loading state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Loading test results...",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            error != null -> {
                // Error state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: $error",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    NeoBrutalButton(
                        text = "Retry",
                        onClick = { viewModel.loadTestResults(testID) }
                    )
                }
            }

            testResult != null -> {
                val result = testResult!!
                val isSoloTest = result.resultType == "solo"
                val hasCompatibilityResults = result.compatibilityResults.isNotEmpty()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .background(brush = BackGroundBrush.homNeoBrush)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    item {
                        NeobrutalistHeader(
                            title = if (isSoloTest) "Solo Test Results" else "Match Analysis",
                            onBackClick = { router.goBack() }
                        )
                    }

                    // Solo Test Results
                    if (isSoloTest && result.soloResult != null) {
                        item {
                            NeobrutalistSoloScoreCard(
                                soloResult = result.soloResult
                            )
                        }

                        // Show personalizedInsights if available
                        result.soloResult.personalizedInsights?.let { insights ->
                            item {
                                NeobrutalistAnalysisCard(
                                    analysis = insights
                                )
                            }
                        }

                        // Solo test: Add "Send to Friend" button
                        item {
                            NeobrutalistSendToFriendCard(
                                onSendToFriendClick = { viewModel.openSendToFriendBottomSheet(testID) }
                            )
                        }
                    }

                    // Compatibility Results
                    if (hasCompatibilityResults) {
                        items(result.compatibilityResults) { compatibilityResult ->
                            // VS Section - Main comparison
                            NeobrutalistVSCard(
                                compatibilityResult = compatibilityResult
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Summary
                            NeobrutalistScoreCard(
                                summary = compatibilityResult.summary
                            )

                            // Chemistry analysis if available
                            compatibilityResult.chemistry?.let { chemistry ->
                                Spacer(modifier = Modifier.height(16.dp))
                                NeobrutalistChemistryCard(
                                    chemistry = chemistry
                                )
                            }

                            // Detailed explanations if available
                            compatibilityResult.explanations?.let { explanations ->
                                Spacer(modifier = Modifier.height(16.dp))
                                NeobrutalistExplanationsCard(
                                    explanations = explanations
                                )
                            }

                            // Matching analysis if available
                            compatibilityResult.matchingAnalysis?.overallAnalysis?.let { analysis ->
                                Spacer(modifier = Modifier.height(16.dp))
                                NeobrutalistAnalysisCard(
                                    analysis = analysis
                                )
                            }
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // Send to Friend Bottom Sheet
        if (showSendToFriendBottomSheet) {
            SendToFriendBottomSheet(
                inviteLink = inviteLink,
                soloResult = testResult?.soloResult,
                testId = testID,
                isVisible = showSendToFriendBottomSheet,
                onDismiss = { viewModel.closeSendToFriendBottomSheet() }
            )
        }
    }
}

@Composable
private fun NeobrutalistHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 4.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeobrutalistIconButton(
                icon = "←",
                backgroundColor = Color(0xFFFF6B6B),
                onClick = onBackClick
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun NeobrutalistVSCard(
    compatibilityResult: com.keak.aishou.data.api.CompatibilityResult
) {
    val score = compatibilityResult.score

    // Convert FriendInfo to UserInfo for compatibility with existing components
    val currentUser = UserInfo(
        userId = "current",
        displayName = "You",
        mbtiType = compatibilityResult.matchingAnalysis?.mbtiCompatibility?.user1Type,
        zodiacSign = compatibilityResult.matchingAnalysis?.zodiacCompatibility?.user1Sign
    )

    val friendUser = UserInfo(
        userId = compatibilityResult.friendId,
        displayName = compatibilityResult.friendInfo?.displayName ?: "Friend",
        mbtiType = compatibilityResult.friendInfo?.mbtiType,
        zodiacSign = compatibilityResult.friendInfo?.zodiacSign
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFE4E1),
                        Color(0xFFFFF0F5)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 4.dp,
                color = Color.Black,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            // VS Title with score
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color(0xFFFFEB3B),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MATCH SCORE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    AnimatedScoreCounter(
                        targetScore = score,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Users comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current User
                NeobrutalistUserCard(
                    user = currentUser,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )

                // VS Divider
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(50),
                            ambientColor = Color.Black
                        )
                        .background(
                            color = Color(0xFFFF9800),
                            shape = RoundedCornerShape(50)
                        )
                        .border(
                            width = 3.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Friend User
                NeobrutalistUserCard(
                    user = friendUser,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NeobrutalistUserCard(
    user: UserInfo,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = color,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (user.displayName ?: "U").first().toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name
            Text(
                text = user.displayName ?: "Unknown",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            // MBTI & Zodiac
            val details = listOfNotNull(user.mbtiType, user.zodiacSign)
                .joinToString(" • ")
            if (details.isNotBlank()) {
                Text(
                    text = details,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun NeobrutalistSoloScoreCard(
    soloResult: com.keak.aishou.data.api.SoloResult
) {
    val score = soloResult.totalScore

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE8F5E8),
                        Color(0xFFF0FFF0)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 4.dp,
                color = Color.Black,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            // Solo Test Title with score
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YOUR SCORE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    AnimatedScoreCounter(
                        targetScore = score,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Achievement message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "🎉 Great job completing this test! Share your results with friends to see how compatible you are.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun NeobrutalistSendToFriendCard(
    onSendToFriendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Share with Friends",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Invite your friends to take this test and compare your compatibility!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            NeoBrutalButton(
                text = "Send to Friend",
                onClick = onSendToFriendClick,
                backgroundColor = Color(0xFFFF9800),
                textColor = Color.White
            )
        }
    }
}

@Composable
private fun NeobrutalistScoreCard(
    summary: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFE8F5E8),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Match Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = summary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun NeobrutalistChemistryCard(
    chemistry: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🧪",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Chemistry Analysis",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = chemistry,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun NeobrutalistExplanationsCard(
    explanations: List<com.keak.aishou.data.api.CompatibilityExplanation>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFF3E5F5),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📝",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Detailed Explanations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            explanations.forEachIndexed { index, explanation ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                NeobrutalistExplanationCard(explanation = explanation.description)
            }
        }
    }
}

@Composable
private fun NeobrutalistAnalysisCard(
    analysis: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔍",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Detailed Analysis",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = analysis,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun NeobrutalistSectionHeader(text: String) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFF9C27B0),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
private fun NeobrutalistExplanationCard(explanation: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(6.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = explanation,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NeobrutalistIconButton(
    icon: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(role = Role.Button) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
private fun AnimatedScoreCounter(
    targetScore: Int,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    color: Color,
    durationMillis: Int = 2500
) {
    var currentScore by remember { mutableStateOf(0) }

    // Custom animation with ease-out effect (slow down at the end)
    val animationSpec = remember {
        tween<Float>(
            durationMillis = durationMillis,
            easing = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f) // Ease-out curve
        )
    }

    val animatedScore by animateFloatAsState(
        targetValue = targetScore.toFloat(),
        animationSpec = animationSpec,
        label = "score_animation"
    )

    // Update current score based on animated value
    LaunchedEffect(animatedScore) {
        currentScore = animatedScore.roundToInt()
    }

    // Start animation when component is composed
    LaunchedEffect(targetScore) {
        // Animation automatically starts due to animateFloatAsState
    }

    Text(
        text = "${currentScore}%",
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color
    )
}

@Composable
private fun NeoBrutalButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color.Black,
    textColor: Color = Color.White
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}