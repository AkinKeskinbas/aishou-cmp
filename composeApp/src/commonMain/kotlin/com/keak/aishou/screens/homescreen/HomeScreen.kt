package com.keak.aishou.screens.homescreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.cosmic_match_home
import aishou.composeapp.generated.resources.hearth_desc
import aishou.composeapp.generated.resources.lightining_home
import aishou.composeapp.generated.resources.mbti_zodiac_home
import aishou.composeapp.generated.resources.new_test_home
import aishou.composeapp.generated.resources.personality_type_home
import aishou.composeapp.generated.resources.profile_home
import aishou.composeapp.generated.resources.quick_actions_home
import aishou.composeapp.generated.resources.quick_quiz_home
import aishou.composeapp.generated.resources.recent_tests_home
import aishou.composeapp.generated.resources.settings_home
import aishou.composeapp.generated.resources.star
import aishou.composeapp.generated.resources.star_dec
import aishou.composeapp.generated.resources.view_all_tests_home
import aishou.composeapp.generated.resources.zodiac_sign_profile_home
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.RecentTestCard
import com.keak.aishou.data.UserState
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import com.keak.aishou.data.api.QuizType
import com.keak.aishou.util.FormatUtils
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(router: Router, vm: HomeViewModel = koinViewModel()) {
    val userState by vm.userState.collectAsStateWithLifecycle()
    val userProfile by vm.userProfile.collectAsStateWithLifecycle()
    val isLoadingProfile by vm.isLoadingProfile.collectAsStateWithLifecycle()
    val profileError by vm.profileError.collectAsStateWithLifecycle()

    // Load user profile when HomeScreen opens or reopens
    LaunchedEffect(Unit) {
        println("HomeScreen: LaunchedEffect triggered - loading user profile")
        vm.loadUserProfile()
    }

    // Convert API solved tests to our display format
    val testResultList = userProfile?.let { profile ->
        println("HomeScreen: Profile MBTI: ${profile.mbti}, Zodiac: ${profile.zodiac}")
        println("HomeScreen: Solo quizzes count: ${profile.soloQuizzes.size}")
        println("HomeScreen: Match quizzes count: ${profile.matchQuizzes.size}")
        println("HomeScreen: Solved tests count: ${profile.solvedTests.size}")

        profile.solvedTests.mapIndexed { index, solvedTest ->
            println("HomeScreen: Converting test $index: id=${solvedTest.id}, type=${solvedTest.type}")
            RecentTestsData(
                testerName = "Me",
                testerMbti = profile.mbti ?: "Unknown",
                testResult = solvedTest.score?.toString() ?: "N/A",
                testerType = TesterType.MYSELF,
                testerUserId = solvedTest.id ?: "unknown_${index}",
                resultBg = when (index) {
                    0 -> Color(0xFF66BB6A)
                    1 -> Color(0xFFFFA726)
                    else -> Color(0xFFFFEB3B)
                },
                testID = solvedTest.title ?: "unknown_${index}",
                testType = if (solvedTest.type == "match") QuizType.Compat else QuizType.Single
            )
        }.filter { it.testID.startsWith("unknown_").not() }
    } ?: emptyList()

    println("HomeScreen: Final test result list size: ${testResultList.size}")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(brush = BackGroundBrush.homNeoBrush)
    ) {
        stickyHeader {
            HomeHeader()
        }
        item {
            UserCard(
                userState = userState,
                userProfile = userProfile,
                isLoadingProfile = isLoadingProfile,
                onRetryProfile = { vm.retryLoadProfile() }
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            TextHeader(
                text = stringResource(Res.string.quick_actions_home),
                image = Res.drawable.lightining_home
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            UserActions(
                onClickAction = { actionName->
                    when(actionName){
                        "New Test"->{
                            router.goToQuickTestScreen()
                        }
                        "Quick Quiz" -> {
                            router.goToQuickQuizScreen()
                        }
                        else -> {

                        }
                    }

                }
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            TextHeader(
                text = stringResource(Res.string.recent_tests_home),
                image = Res.drawable.star_dec
            )
            Spacer(Modifier.height(16.dp))
        }
        items(testResultList) { recentTestsData ->
            RecentTestCard(
                testerName = recentTestsData.testerName,
                testResult = recentTestsData.testResult,
                testerMbti = recentTestsData.testerMbti,
                testerType = recentTestsData.testerType,
                bgColor = recentTestsData.resultBg,
                clickAction = {
                    if (recentTestsData.testType == QuizType.Single){
                        router.goToTestResultScreen(recentTestsData.testID)
                    }else{
                        router.goToTestResultScreen(recentTestsData.testID)
                    }

                }
            )
            Spacer(Modifier.height(8.dp))
        }
        item {
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .clickable(role = Role.Button) {
                        router.goToAllResultScreen()
                    }
            ) {
                Text(
                    text = stringResource(Res.string.view_all_tests_home),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 8.dp).align(Alignment.Center),
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}


enum class TesterType {
    FRIEND, PARTNER, COWORKER, FAMILY, MYSELF
}

@Composable
private fun UserActions(
    onClickAction: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        QuickActionsCard(
            backgroundColor = Color(0xFFEC407A),
            image = Res.drawable.hearth_desc,
            actionText = stringResource(Res.string.new_test_home),
            textColor = Color.White,
            imageColor = Color.White,
            onClickAction = onClickAction
        )
        Spacer(Modifier.width(8.dp))
        QuickActionsCard(
            backgroundColor = Color(0xFF66BB6A),
            image = Res.drawable.star,
            actionText = stringResource(Res.string.quick_quiz_home),
            onClickAction=onClickAction
        )
//        QuickActionsCard(
//            backgroundColor = Color(0xFF4DD0E1),
//            image = Res.drawable.profile_home,
//            actionText = stringResource(Res.string.view_all_home),
//            onClickAction = onClickAction
//        )
    }
    Spacer(Modifier.height(8.dp))
//    Row(
//        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        QuickActionsCard(
//            backgroundColor = Color(0xFFFFEE58),
//            image = Res.drawable.star_dec,
//            actionText = stringResource(Res.string.best_matches_home),
//            onClickAction = onClickAction
//        )
//        Spacer(Modifier.width(8.dp))
//        QuickActionsCard(
//            backgroundColor = Color(0xFF66BB6A),
//            image = Res.drawable.star,
//            actionText = stringResource(Res.string.quick_quiz_home),
//            onClickAction=onClickAction
//        )
//    }
}

@Composable
private fun TextHeader(
    text: String,
    image: DrawableResource
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            alignment = Alignment.Center,
            modifier = Modifier.size(25.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
        )
    }
}

@Composable
private fun QuickActionsCard(
    backgroundColor: Color,
    image: DrawableResource,
    actionText: String,
    textColor: Color = Color.Black,
    imageColor: Color = Color.Black,
    onClickAction:(actionId: String)-> Unit
) {
    val screenSize = screenSize()
    val cardWidth = screenSize.width / 2.2
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = backgroundColor,
        modifier = Modifier.width(cardWidth.dp).clickable(){
            onClickAction.invoke(actionText)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(imageColor)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = actionText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier,
                color = textColor
            )
        }
    }

}

@Composable
private fun UserCard(
    userState: UserState,
    userProfile: com.keak.aishou.data.api.UserProfileResponse?,
    isLoadingProfile: Boolean,
    onRetryProfile: () -> Unit
) {
    val phoneScreenSize = screenSize()
    val screenWidth = phoneScreenSize.width / 2.2
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        backgroundColor = null,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE1BEE7), // purple-100
                Color(0xFFF8BBD0), // pink-100
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (userState.isFirstTimeUser) "Welcome to Aishou! ✨" else "Welcome back! 👋",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
            )
            Text(
                text = if (userState.isFirstTimeUser) {
                    "Let's discover your cosmic personality!"
                } else {
                    val days = userState.daysSinceFirstLaunch ?: 0
                    val formattedLaunchCount = FormatUtils.formatLaunchCount(userState.appLaunchCount)
                    if (days > 0) {
                        val formattedDays = FormatUtils.formatDaysActive(days)
                        "Launch #$formattedLaunchCount • $formattedDays active"
                    } else {
                        "Launch #$formattedLaunchCount"
                    }
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier,
                color = if (userState.isFirstTimeUser) Color.Black else Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.width(screenWidth.dp),
                    backgroundColor = Color.White
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().background(
                                color = Color(0xFFA78BFA)
                            ).border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoadingProfile) {
                                Text(
                                    text = "...",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                Text(
                                    text = userProfile?.mbti ?: "Unknown",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.personality_type_home),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier,
                            color = Color.Gray
                        )
                    }


                }
                Spacer(Modifier.width(8.dp))
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.star),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            modifier = Modifier.size(25.dp)
                        )
                        if (isLoadingProfile) {
                            Text(
                                text = "...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier,
                            )
                        } else {
                            Text(
                                text = userProfile?.zodiac ?: "Unknown",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier,
                            )
                        }
                        Text(
                            text = stringResource(Res.string.zodiac_sign_profile_home),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.width(70.dp),
                backgroundColor = Color(0xFFE91E63)
            ) {
                Image(
                    painter = painterResource(Res.drawable.hearth_desc),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(Res.string.cosmic_match_home),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                )
                // Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.mbti_zodiac_home),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.profile_home),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(Modifier.width(4.dp))

                Image(
                    painter = painterResource(Res.drawable.settings_home),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                )
            }

        }
    }
}