package com.keak.aishou.screens.matching

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.cat
import aishou.composeapp.generated.resources.crown
import aishou.composeapp.generated.resources.crown_premium
import aishou.composeapp.generated.resources.instagram
import aishou.composeapp.generated.resources.match_great_job
import aishou.composeapp.generated.resources.match_send_your_friend
import aishou.composeapp.generated.resources.match_share_on_instagram
import aishou.composeapp.generated.resources.premium_insight_button
import aishou.composeapp.generated.resources.premium_insight_message
import aishou.composeapp.generated.resources.unicorn
import aishou.composeapp.generated.resources.unicorn_fish
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.SendToFriendBottomSheet
import com.keak.aishou.data.api.MatchingAnalysis
import com.keak.aishou.data.api.ResultType
import com.keak.aishou.data.models.UserInfo
import com.keak.aishou.misc.orZero
import com.keak.aishou.navigation.Router
import com.keak.aishou.screens.allresults.TestResultViewModel
import com.keak.aishou.utils.ShareableMatchResultCard
import com.keak.aishou.utils.StringResources
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun UserMatchScreen(
    testID: String,
    friendId: String? = null,
    router: Router,
    viewModel: TestResultViewModel = koinViewModel()
) {
    val testResult by viewModel.testResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val showSendToFriendBottomSheet by viewModel.showSendToFriendBottomSheet.collectAsStateWithLifecycle()
    val inviteLink by viewModel.inviteLink.collectAsStateWithLifecycle()
    val isSharing by viewModel.isSharing.collectAsStateWithLifecycle()
    val resultType = ResultType.fromString(testResult?.resultType.orEmpty())

    // Reset sharing state when returning to screen
    LaunchedEffect(Unit) {
        viewModel.resetSharingState()
    }

    println("UserMatchScreen: resultType-->${testResult?.resultType}")
    println("UserMatchScreen: TEST!-->")


    // Load test results when screen starts
    LaunchedEffect(testID, friendId) {
        println("UserMatchScreen: Loading test results for ID: $testID, friendId: $friendId")
        // Pass friendId to API call if available
        viewModel.loadTestResults(testID, friendId)
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
                        text = StringResources.errorPrefix(error ?: "Unknown error"),
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    NeoBrutalButton(
                        text = StringResources.retryButton(),
                        onClick = { viewModel.loadTestResults(testID) }
                    )
                }
            }

            testResult != null -> {

                val result = testResult!!
                val hasSoloResult = result.soloResult != null
                val hasCompatibilityResults = result.compatibilityResults.isNotEmpty()

                // Determine display title based on available data
                val title = when {
                    hasSoloResult && hasCompatibilityResults -> StringResources.testResults()
                    hasSoloResult -> StringResources.soloTestResults()
                    hasCompatibilityResults -> StringResources.matchAnalysis()
                    else -> StringResources.testResults()
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .background(Color(0xFF8565FF))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    item {
                        NeobrutalistHeader(
                            title = title,
                            onBackClick = { router.goBack() },
                        )
                    }
                    when (resultType) {
                        ResultType.SOLO -> {
                            item {
                                NeobrutalistSoloScoreCard(
                                    soloResult = result.soloResult,
                                    router = router
                                )
                            }

                            // Show personalizedInsights if available
                            result.soloResult?.personalizedInsights?.let { insights ->
                                item {
                                    NeobrutalistAnalysisCard(
                                        analysis = insights
                                    )
                                }
                            }

                            // Show "Send to Friend" button if this is a solo result
                            if (viewModel.shouldShowSendToFriendButton()) {
                                item {
                                    NeobrutalistSendToFriendCard(
                                        onSendToFriendClick = {
                                            viewModel.openSendToFriendBottomSheet(
                                                testID
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        ResultType.COMPATIBILITY -> {
                            items(result.compatibilityResults) { compatibilityResult ->
                                // VS Section - Main comparison
                                NeobrutalistVSCard(
                                    compatibilityResult = compatibilityResult,
                                    myDisplayName = testResult?.myDisplayName.orEmpty()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Summary
                                NeobrutalistScoreCard(
                                    summary = compatibilityResult.summary.orEmpty()
                                )

                                // Chemistry analysis if available
                                compatibilityResult.matchingAnalysis?.let { analysis ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NeobrutalistMBTICompatibilityCard(
                                        matchingAnalysis = analysis
                                    )
                                }
                                compatibilityResult.matchingAnalysis?.let { analysis ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NeobrutalistZodiacCompatibilityCard(
                                        matchingAnalysis = analysis
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                NeoBrutalistCardViewWithFlexSize(
                                    modifier = Modifier.fillMaxWidth().clickable(role = Role.Button){
                                        result.let { testResultData ->
                                            val userDisplayName = testResultData.myDisplayName
                                            viewModel.shareToInstagramStory {
                                                ShareableMatchResultCard(
                                                    testResult = testResultData,
                                                    userDisplayName = userDisplayName,
                                                    compatibilityResult
                                                )
                                            }
                                        }
                                    },
                                    backgroundColor = Color(0xFFFECC00),
                                    cornerRadius = 8.dp
                                ) {
                                    Text(
                                        text = stringResource(Res.string.match_share_on_instagram),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        ResultType.BOTH -> {
                            item {
                                NeobrutalistSoloScoreCard(
                                    soloResult = result.soloResult,
                                    isBoth = true
                                )
                            }

                            // Show personalizedInsights if available
                            result.soloResult?.personalizedInsights?.let { insights ->
                                item {
                                    NeobrutalistAnalysisCard(
                                        analysis = insights
                                    )
                                }
                            }
                            items(result.compatibilityResults) { compatibilityResult ->
                                // VS Section - Main comparison
                                NeobrutalistVSCard(
                                    compatibilityResult = compatibilityResult,
                                    myDisplayName = testResult?.myDisplayName.orEmpty()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Summary
                                NeobrutalistScoreCard(
                                    summary = compatibilityResult.summary.orEmpty()
                                )

                                // Chemistry analysis if available
                                compatibilityResult.matchingAnalysis?.let { analysis ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NeobrutalistMBTICompatibilityCard(
                                        matchingAnalysis = analysis
                                    )
                                }
                                compatibilityResult.matchingAnalysis?.let { analysis ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NeobrutalistZodiacCompatibilityCard(
                                        matchingAnalysis = analysis
                                    )
                                }
                            }
                        }

                        ResultType.NONE -> {}
                    }
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            // Share button (only show if callback provided)
//            onShareClick?.let { shareCallback ->
//                Spacer(modifier = Modifier.width(16.dp))
//                Image(
//                    painter = painterResource(Res.drawable.instagram),
//                    contentDescription = null,
//                    modifier = Modifier.size(45.dp).clickable() {
//                        if (isSharing) {
//                            {}
//                        } else shareCallback
//                    }
//                )
//            }
        }
    }
}

@Composable
private fun NeobrutalistVSCard(
    compatibilityResult: com.keak.aishou.data.api.CompatibilityResult,
    myDisplayName: String
) {
    val score = compatibilityResult.score

    // Convert FriendInfo to UserInfo for compatibility with existing components
    val currentUser = UserInfo(
        userId = "current",
        displayName = compatibilityResult.myInfo?.displayName,
        mbtiType = compatibilityResult.myInfo?.mbtiType,
        zodiacSign = compatibilityResult.myInfo?.zodiacSign
    )

    val friendUser = UserInfo(
        userId = compatibilityResult.friendId.orEmpty(),
        displayName = compatibilityResult.friendInfo?.displayName
            ?: StringResources.friendFallback(),
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
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0XFFFECB7F),
                cornerRadius = 16.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = StringResources.matchScore(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    AnimatedScoreCounter(
                        targetScore = score.orZero(),
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
                        text = StringResources.vs(),
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
            Spacer(Modifier.height(8.dp))
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFFD7B5E)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = compatibilityResult.chemistry.orEmpty(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
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
    NeoBrutalistCardViewWithFlexSize(
        modifier = modifier,
        backgroundColor = Color(0XFFFECB7F),
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
//            Text(
//                text = user.displayName ?: "Unknown",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Black,
//                color = Color.Black,
//                textAlign = TextAlign.Center
//            )

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
    soloResult: com.keak.aishou.data.api.SoloResult?,
    isBoth: Boolean = false,
    router: Router? = null
) {
    val score = soloResult?.totalScore
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0XFFFECB7F),
        cornerRadius = 16.dp
    ) {
        Column {
            // Solo Test Title with score
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                backgroundColor = Color(0xFFCEE977),
                cornerRadius = 12.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = StringResources.yourScore(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    AnimatedScoreCounter(
                        targetScore = score.orZero(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (isBoth.not()) {
                // Score Description or Achievement message
                val displayText = soloResult?.scoreDescription
                    ?: stringResource(Res.string.match_great_job)
                Text(
                    text = displayText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
//                NeoBrutalistCardViewWithFlexSize(
//                    modifier = Modifier.fillMaxWidth().padding(8.dp),
//                    backgroundColor = Color(0xFFFDFE99),
//                    cornerRadius = 12.dp
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = displayText,
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = Color(0xFF333333),
//                            textAlign = TextAlign.Center,
//                            lineHeight = 20.sp
//                        )
//                    }
//                }
            }
            // Premium insight button
            if (router != null) {
                Spacer(modifier = Modifier.height(8.dp))
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.rotate(2f).fillMaxWidth().clickable(role = Role.Button) {
                        router.goToPaywall()
                    },
                    backgroundColor = Color(0xFFFDC400),
                    cornerRadius = 8.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
//                        Text(
//                            text = stringResource(Res.string.premium_insight_button),
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White,
//                            textAlign = TextAlign.Center
//                        )
//                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.crown),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.Black),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.premium_insight_message),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Image(
                                painter = painterResource(Res.drawable.crown),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.Black),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                    }
                }
            }
        }
    }

}

@Composable
private fun NeobrutalistSendToFriendCard(
    onSendToFriendClick: () -> Unit
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFB8B9DE),
        cornerRadius = 12.dp
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
                    text = StringResources.shareWithFriends(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.match_send_your_friend),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.width(150.dp).clickable(role = Role.Button) {
                    onSendToFriendClick.invoke()
                },
                backgroundColor = Color(0xFF85D0E9),
                cornerRadius = 8.dp
            ) {
                Text(
                    text = StringResources.sendToFriend(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun NeobrutalistScoreCard(
    summary: String
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFADFF86),
        cornerRadius = 12.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.cat),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = StringResources.matchSummary(),
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
private fun NeobrutalistMBTICompatibilityCard(
    matchingAnalysis: MatchingAnalysis
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFFF73B4),
        cornerRadius = 12.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.unicorn),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = StringResources.mbtiCompatibility(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = matchingAnalysis.mbtiMatch?.explanation.orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = StringResources.strengths(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            matchingAnalysis.mbtiMatch?.strengths?.forEach { strength ->
                Text(
                    text = strength,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = StringResources.challenges(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            matchingAnalysis.mbtiMatch?.challenges?.forEach { strength ->
                Text(
                    text = strength,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    lineHeight = 20.sp
                )
            }
        }
    }

}

@Composable
private fun NeobrutalistZodiacCompatibilityCard(
    matchingAnalysis: MatchingAnalysis
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFB8B9DE),
        cornerRadius = 12.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.unicorn_fish),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = StringResources.zodiacCompatibility(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = matchingAnalysis.zodiacMatch?.explanation.orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = StringResources.strengths(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            matchingAnalysis.zodiacMatch?.strengths?.forEach { strength ->
                Text(
                    text = strength,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = StringResources.challenges(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            matchingAnalysis.zodiacMatch?.challenges?.forEach { strength ->
                Text(
                    text = strength,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    lineHeight = 20.sp
                )
            }
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
                    text = StringResources.detailedExplanations(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

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
                    text = StringResources.detailedAnalysis(),
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