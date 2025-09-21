package com.keak.aishou.screens.allresults

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.match_results
import aishou.composeapp.generated.resources.quick_tests_mbti_match
import aishou.composeapp.generated.resources.quick_tests_zodiac_match
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.ScreenHeader
import com.keak.aishou.data.api.TestResultResponse
import com.keak.aishou.data.api.ResultType
import com.keak.aishou.data.api.SoloResult
import com.keak.aishou.purchase.PremiumChecker
import com.keak.aishou.misc.BackGroundBrush.resultScreenBrush
import com.keak.aishou.misc.BackGroundBrush.resultScreenHeaderBrush
import com.keak.aishou.navigation.Router
import org.jetbrains.compose.resources.stringResource
import aishou.composeapp.generated.resources.result_loading_test_results
import aishou.composeapp.generated.resources.result_error_loading_results
import aishou.composeapp.generated.resources.result_unknown_error
import aishou.composeapp.generated.resources.result_retry
import aishou.composeapp.generated.resources.result_your_test_results
import aishou.composeapp.generated.resources.result_love_matches
import aishou.composeapp.generated.resources.result_complete_results
import aishou.composeapp.generated.resources.result_test_results
import aishou.composeapp.generated.resources.result_points
import aishou.composeapp.generated.resources.result_personality_breakdown
import aishou.composeapp.generated.resources.result_detailed_breakdown_coming_soon
import aishou.composeapp.generated.resources.result_ai_psychologist_says
import aishou.composeapp.generated.resources.result_generate_detailed_insights
import aishou.composeapp.generated.resources.result_premium_user_generate_analysis
import aishou.composeapp.generated.resources.result_generating
import aishou.composeapp.generated.resources.result_generate_insights

@Composable
fun TestResultScreen(
    router: Router,
    testID: String,
    viewModel: TestResultViewModel = koinViewModel()
) {
    val testResult by viewModel.testResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isReprocessing by viewModel.isReprocessing.collectAsStateWithLifecycle()

    // Load test results when screen starts
    LaunchedEffect(testID) {
        viewModel.loadTestResults(testID)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            resultScreenBrush
        ).padding(horizontal = 16.dp)
    ) {
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
                        text = stringResource(Res.string.result_loading_test_results),
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
                        text = stringResource(Res.string.result_error_loading_results),
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = error ?: stringResource(Res.string.result_unknown_error),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    NeoBrutalistCardViewWithFlexSize(
                        backgroundColor = Color.White,
                        modifier = Modifier.clickable(role = Role.Button) {
                            viewModel.retryLoad(testID)
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.result_retry),
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
            testResult != null -> {
                // Success state - show results based on result type
                TestResultContent(
                    testResult = testResult!!,
                    viewModel = viewModel,
                    router = router,
                    testID = testID,
                    isReprocessing = isReprocessing
                )
            }
        }
    }
}

@Composable
fun TestResultContent(
    testResult: TestResultResponse,
    viewModel: TestResultViewModel,
    router: Router,
    testID: String,
    isReprocessing: Boolean
) {
    val resultType = ResultType.fromString(testResult.resultType)

    Column(
        modifier = Modifier.zIndex(2f).verticalScroll(rememberScrollState())
    ) {
        // Back button header
        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            screenName = when (resultType) {
                ResultType.SOLO -> stringResource(Res.string.result_your_test_results)
                ResultType.COMPATIBILITY -> stringResource(Res.string.result_love_matches)
                ResultType.BOTH -> stringResource(Res.string.result_complete_results)
                ResultType.NONE -> stringResource(Res.string.result_test_results)
            },
            backGroundColor = Color.Black,
            textColor = Color.White,
            fontSize = 16,
            backAction = {
                router.goBack()
            }
        )

        Spacer(Modifier.height(16.dp))

        when (resultType) {
            ResultType.SOLO -> {
                // Show solo result + send to friend button
                SoloResultContent(
                    testResult = testResult,
                    router = router,
                    viewModel = viewModel,
                    testID = testID,
                    isReprocessing = isReprocessing
                )
                Spacer(Modifier.height(16.dp))
                SendToFriendButton(onClick = {
                    // TODO: Implement send to friend functionality
                })
            }
            ResultType.COMPATIBILITY -> {
                // Show compatibility results
                CompatibilityResultsContent(testResult = testResult)
            }
            ResultType.BOTH -> {
                // Show both solo and compatibility results
                SoloResultContent(
                    testResult = testResult,
                    router = router,
                    viewModel = viewModel,
                    testID = testID,
                    isReprocessing = isReprocessing
                )
                Spacer(Modifier.height(16.dp))
                CompatibilityResultsContent(testResult = testResult)
                Spacer(Modifier.height(16.dp))
                SendToFriendButton(onClick = {
                    // TODO: Implement send to friend functionality
                })
            }
            ResultType.NONE -> {
                // No results available
                NoResultsContent(onRetakeTest = {
                    // TODO: Navigate to quiz screen
                })
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}


@Composable
fun SoloResultContent(
    testResult: TestResultResponse,
    router: Router,
    viewModel: TestResultViewModel,
    testID: String,
    isReprocessing: Boolean
) {
    testResult.soloResult?.let { soloResult ->
        // Animated score display with emoji
        val animatedPercent = rememberCountUpPercentage(soloResult.totalScore)

        Column(modifier = Modifier.fillMaxWidth()) {
            // Big animated score card
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = null,
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF6B6B),
                        Color(0xFF4ECDC4),
                        Color(0xFF45B7D1)
                    )
                ),
                modifier = Modifier.fillMaxWidth(),
                shadowColor = Color.Black,
                shadowOffset = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 48.sp
                    )

                    Text(
                        text = "$animatedPercent",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(Res.string.result_points),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = getScoreEmoji(soloResult.totalScore),
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Score breakdown with personality traits
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                shadowColor = Color(0xFF6C5CE7)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.result_personality_breakdown),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Creative score vector display
                    val traits = listOf("E/I", "S/N", "T/F", "J/P", "💪", "❤️", "🧠", "✨")
                    val colors = listOf(
                        Color(0xFFFFE66D), Color(0xFF06FFA5), Color(0xFF4ECDC4),
                        Color(0xFFFF6B6B), Color(0xFFA8E6CF), Color(0xFFFFD93D),
                        Color(0xFF6C5CE7), Color(0xFFFDCB6E)
                    )

                    Column {
                        if (soloResult.scoreVector.isNotEmpty()) {
                            soloResult.scoreVector.chunked(4).forEachIndexed { rowIndex, chunk ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    chunk.forEachIndexed { index, score ->
                                        val actualIndex = rowIndex * 4 + index
                                        if (actualIndex < traits.size) {
                                            TraitScoreCard(
                                                trait = traits[actualIndex],
                                                score = score,
                                                color = colors[actualIndex],
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                                if (rowIndex == 0) Spacer(Modifier.height(8.dp))
                            }
                        } else {
                            // Fallback when no score vector available
                            Text(
                                text = stringResource(Res.string.result_detailed_breakdown_coming_soon),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // AI insights with premium check and reprocess logic
            when {
                // Case 1: Premium user with insights or non-premium with insights -> Show AI insights
                (PremiumChecker.isPremium || !soloResult.personalizedInsights.isNullOrBlank()) && !soloResult.personalizedInsights.isNullOrBlank() -> {
                    NeoBrutalistCardViewWithFlexSize(
                        backgroundColor = null,
                        backgroundBrush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🤖",
                                    fontSize = 32.sp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = stringResource(Res.string.result_ai_psychologist_says),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            NeoBrutalistCardViewWithFlexSize(
                                backgroundColor = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 16.dp
                            ) {
                                Text(
                                    text = "💭 ${soloResult.personalizedInsights}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
                // Case 2: Premium user without insights -> Show reprocess button
                PremiumChecker.isPremium && soloResult.personalizedInsights.isNullOrBlank() -> {
                    ReprocessSection(
                        isReprocessing = isReprocessing,
                        onReprocessClick = {
                            viewModel.reprocessTestResults(testID)
                        }
                    )
                }
                // Case 3: Non-premium user without insights -> Show premium prompt
                else -> {
                    PremiumPromptSection(
                        onUpgradeClick = {
                            router.goToPaywall()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReprocessSection(
    isReprocessing: Boolean,
    onReprocessClick: () -> Unit
) {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = null,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF4ECDC4),
                Color(0xFF44A08D)
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔄",
                fontSize = 48.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.result_generate_detailed_insights),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.result_premium_user_generate_analysis),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = if (isReprocessing) Color.Gray else Color.White,
                modifier = Modifier.fillMaxWidth()
                    .clickable(enabled = !isReprocessing) {
                        onReprocessClick()
                    },
                borderWidth = 3.dp,
                shadowOffset = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isReprocessing) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.result_generating),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.result_generate_insights),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TraitScoreCard(
    trait: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = color,
        modifier = modifier.padding(2.dp),
        borderWidth = 3.dp,
        shadowOffset = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = trait,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = Color.Black
            )
            Text(
                text = score.toString(),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

fun getScoreEmoji(score: Int): String {
    return when {
        score >= 90 -> "🔥🎉"
        score >= 80 -> "⭐😎"
        score >= 70 -> "👍😊"
        score >= 60 -> "🙂💪"
        score >= 50 -> "😐🤔"
        else -> "😅💪"
    }
}

@Composable
fun CompatibilityResultsContent(testResult: TestResultResponse) {
    if (testResult.compatibilityResults.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header with hearts
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = null,
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF9A9E),
                        Color(0xFFFAD0C4),
                        Color(0xFFFFE5E5)
                    )
                ),
                modifier = Modifier.fillMaxWidth(),
                shadowColor = Color(0xFFE91E63)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💕💕💕",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "LOVE MATCHES",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "💕💕💕",
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            testResult.compatibilityResults.forEachIndexed { index, compatibility ->
                val matchEmoji = getMatchEmoji(compatibility.score)
                val cardColors = getMatchColors(compatibility.score)

                NeoBrutalistCardViewWithFlexSize(
                    backgroundColor = null,
                    backgroundBrush = Brush.linearGradient(cardColors),
                    modifier = Modifier.fillMaxWidth(),
                    shadowColor = Color.Black,
                    shadowOffset = 8.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "👤 Friend #${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "$matchEmoji ${compatibility.score}%",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 32.sp,
                                    color = Color.White
                                )
                            }

                            // Visual match meter
                            MatchMeter(score = compatibility.score)
                        }

                        Spacer(Modifier.height(16.dp))

                        // Relationship summary in chat bubble
                        NeoBrutalistCardViewWithFlexSize(
                            backgroundColor = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 20.dp,
                            borderWidth = 3.dp
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💬",
                                    fontSize = 24.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = compatibility.summary ?: "Compatibility analysis coming soon! 💫",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Match advice
                        Text(
                            text = getMatchAdvice(compatibility.score),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MatchMeter(score: Int) {
    val hearts = (score / 20).coerceAtMost(5) // Max 5 hearts

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { index ->
            Text(
                text = if (index < hearts) "❤️" else "🤍",
                fontSize = 20.sp
            )
        }
    }
}

fun getMatchEmoji(score: Int): String {
    return when {
        score >= 90 -> "🔥"
        score >= 80 -> "💖"
        score >= 70 -> "💕"
        score >= 60 -> "💛"
        score >= 50 -> "💙"
        else -> "💜"
    }
}

fun getMatchColors(score: Int): List<Color> {
    return when {
        score >= 90 -> listOf(Color(0xFFFF416C), Color(0xFFFF4B2B)) // Hot red
        score >= 80 -> listOf(Color(0xFFFF6B6B), Color(0xFFFFE66D)) // Pink-yellow
        score >= 70 -> listOf(Color(0xFF4ECDC4), Color(0xFF44A08D)) // Teal
        score >= 60 -> listOf(Color(0xFF667eea), Color(0xFF764ba2)) // Purple
        score >= 50 -> listOf(Color(0xFF36D1DC), Color(0xFF5B86E5)) // Blue
        else -> listOf(Color(0xFFA8E6CF), Color(0xFFDCEDC1)) // Green
    }
}

fun getMatchAdvice(score: Int): String {
    return when {
        score >= 90 -> "🔥 PERFECT MATCH! You two are cosmic soulmates!"
        score >= 80 -> "⭐ AMAZING! This relationship has incredible potential!"
        score >= 70 -> "💖 GREAT MATCH! You complement each other beautifully!"
        score >= 60 -> "😊 GOOD COMPATIBILITY! Some differences but worth exploring!"
        score >= 50 -> "🤔 MODERATE MATCH! Communication will be key!"
        else -> "💪 CHALLENGING! But opposites can attract with effort!"
    }
}

@Composable
fun PremiumPromptSection(onUpgradeClick: () -> Unit) {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = null,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF6B6B),
                Color(0xFFFFE66D),
                Color(0xFF4ECDC4)
            )
        ),
        modifier = Modifier.fillMaxWidth(),
        shadowColor = Color(0xFFFF4757),
        shadowOffset = 10.dp,
        borderWidth = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔮✨",
                fontSize = 48.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "UNLOCK AI INSIGHTS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Get personalized AI analysis of your personality!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PremiumFeature("🧠", "Deep Analysis")
                PremiumFeature("💡", "Insights")
                PremiumFeature("🎯", "Predictions")
            }

            Spacer(Modifier.height(20.dp))

            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth().clickable(role = Role.Button) {
                    onUpgradeClick()
                },
                shadowColor = Color(0xFF00D2FF),
                shadowOffset = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👑",
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "UPGRADE TO PREMIUM",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "⚡",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumFeature(emoji: String, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 32.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SendToFriendButton(onClick: () -> Unit) {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = null,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF06FFA5),
                Color(0xFF4FFFB3),
                Color(0xFF06FFA5)
            )
        ),
        modifier = Modifier.fillMaxWidth().clickable(role = Role.Button) { onClick() },
        shadowColor = Color(0xFF00D084),
        shadowOffset = 8.dp,
        borderWidth = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📤",
                fontSize = 24.sp
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "SEND TO FRIEND",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "💌",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun NoResultsContent(onRetakeTest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Big emoji display
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = null,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFE5E5),
                    Color(0xFFFFF0F0),
                    Color(0xFFFFE5E5)
                )
            ),
            modifier = Modifier.fillMaxWidth(),
            shadowColor = Color(0xFFE0E0E0)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🤷‍♂️",
                    fontSize = 64.sp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "OOPS!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = "No Results Yet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Explanation card
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = Color.White,
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📝",
                    fontSize = 32.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "This test hasn't been completed yet!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Take the test to discover your personality insights and compatibility matches!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Action button
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = null,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF667eea),
                    Color(0xFF764ba2)
                )
            ),
            modifier = Modifier.fillMaxWidth().clickable(role = Role.Button) { onRetakeTest() },
            shadowColor = Color(0xFF5A67D8),
            shadowOffset = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚀",
                    fontSize = 24.sp
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "START TEST NOW",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "✨",
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun Explanation(
    mbtiTitle: String = stringResource(Res.string.quick_tests_mbti_match),
    zodiacTitle: String = stringResource(Res.string.quick_tests_zodiac_match)
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = null,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFAB47BC),
                    Color(0xFFEC407A),
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = mbtiTitle,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    thickness = 5.dp,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)).border(
                            3.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    Text(
                        text = "ENFP and INTJ make a powerful combination! Your spontaneous creativity (ENFP) perfectly complements their strategic planning (INTJ). This creates a dynamic balance of inspiration and execution.",
                        // textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 12.sp,

                        )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = null,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFEE58),
                    Color(0xFFFFA726),
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = zodiacTitle,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    thickness = 5.dp,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)).border(
                            3.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    Text(
                        text = "Gemini's adaptability and Scorpio's intensity create an exciting dynamic! Gemini brings lightness and curiosity while Scorpio adds depth and passion to the relationship.",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }

        }
    }
}

@Composable
fun UserCardOne(
    desireWidth: Float,
    modifier: Modifier,
    userName: String,
    userMbti: String,
    userZodiac: String,
    colorListMbti: List<Color>,
    colorListZodiac: List<Color>,
) {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = Color.White,
        modifier = modifier,
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = userName,
                modifier = Modifier,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(8.dp))
            //Name and Info Cards 1
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = null,
                cornerRadius = 8.dp,
                backgroundBrush = Brush.linearGradient(
                    colors = colorListMbti
                ),
                modifier = Modifier.width(desireWidth.dp)
            ) {
                Column {
                    Text(
                        text = "MBTI",
                        // textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = userMbti,
                        //textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = null,
                cornerRadius = 8.dp,
                backgroundBrush = Brush.linearGradient(
                    colors = colorListZodiac
                ),
                modifier = Modifier.width(desireWidth.dp)
            ) {
                Column {
                    Text(
                        text = "ZODIAC",
                        //textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = userZodiac,
                        //  textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderMatchResult() {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = null,
        backgroundBrush = resultScreenHeaderBrush
    ) {
        Text(
            text = stringResource(Res.string.match_results),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            fontWeight = FontWeight.Black,
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}


@Composable
fun rememberCountUpPercentage(
    targetPercent: Int,
    durationMillis: Int = 4200
): Int {
    val clampedTarget = targetPercent.coerceIn(0, 100)
    val anim = remember { Animatable(0f) }

    LaunchedEffect(clampedTarget) {
        anim.stop()
        anim.animateTo(
            targetValue = clampedTarget.toFloat(),
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = LinearOutSlowInEasing
            )
        )
    }
    return anim.value.toInt()
}

