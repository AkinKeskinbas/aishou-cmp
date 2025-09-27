package com.keak.aishou.screens.homescreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.bell
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
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
import com.keak.aishou.misc.orFalse
import com.keak.aishou.purchase.PremiumChecker
import com.keak.aishou.util.FormatUtils
import com.keak.aishou.utils.StringResources
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

    var showSettingsSheet by remember { mutableStateOf(false) }

    // Load user profile and notification count when HomeScreen opens or reopens
    LaunchedEffect(Unit) {
        println("HomeScreen: LaunchedEffect triggered - loading user profile and notification count")
        vm.loadUserProfile()
        vm.loadUnreadNotificationCount()
    }


    // Request notification permission when HomeScreen is ready
    LaunchedEffect(userState) {
        if (userState.isFirstTimeUser) {
            // For first time users, request permission immediately after a short delay
            println("HomeScreen: First time user - requesting notification permission after 2s delay")
            kotlinx.coroutines.delay(2000)
            vm.requestNotificationPermission()
        } else {
            // For returning users, request permission after a longer delay to avoid disruption
            println("HomeScreen: Returning user - requesting notification permission after 5s delay")
            kotlinx.coroutines.delay(5000)
            vm.requestNotificationPermission()
        }
    }

    // Convert API data to display format - combining solo and match quizzes
    val testResultList = userProfile?.let { profile ->
        println("HomeScreen: Profile MBTI: ${profile.mbti}, Zodiac: ${profile.zodiac}")
        println("HomeScreen: Solo quizzes count: ${profile.soloQuizzes.size}")
        println("HomeScreen: Match quizzes count: ${profile.matchQuizzes.size}")

        val soloTests = profile.soloQuizzes.map { soloQuiz ->
            RecentTestsData(
                testerName = StringResources.me(),
                testerMbti = profile.mbti ?: StringResources.unknown(),
                testResult = soloQuiz.totalScore?.toString() ?: "N/A",
                testerType = TesterType.MYSELF,
                testerUserId = soloQuiz.submissionId ?: "",
                resultBg = Color(0xFF66BB6A), // Green for solo tests
                testID = soloQuiz.testId ?: "", // Use testId, not submissionId
                friendInfo = null,
                testType = QuizType.Single
            )
        }

        val matchTests = profile.matchQuizzes.map { matchQuiz ->
            RecentTestsData(
                testerName = StringResources.me(),
                testerMbti = profile.mbti ?: StringResources.unknown(),
                testResult = matchQuiz.score?.toString() ?: "N/A",
                testerType = TesterType.PARTNER,
                testerUserId = matchQuiz.compatibilityId ?: "",
                resultBg = Color(0xFFFFA726), // Orange for match tests
                testID = matchQuiz.testId
                    ?: "", // Use testId for endpoint, store compatibilityId in testerUserId
                friendInfo = matchQuiz.friendInfo,
                testType = QuizType.Compat
            )
        }

        // Combine and sort by most recent
        (soloTests + matchTests)
            .filter { it.testID.isNotBlank() }
            .take(5) // Show only last 5 tests
    } ?: emptyList()

    println("HomeScreen: Final test result list size: ${testResultList.size}")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            //.background(brush = BackGroundBrush.homNeoBrush)
            .background(Color(0xFF49DC9C))
    ) {
        stickyHeader {
            HomeHeader(
                vm = vm,
                onNotificationClick = {
                    router.goToNotifications()
                },
                onProfileClick = {
                    router.goToProfile()
                },
                onSettingsClick = {
                    showSettingsSheet = true
                }
            )
        }
        item {
            // Remember stable values to prevent unnecessary recompositions
            val stableUserState = remember(userState.isFirstTimeUser, userState.appLaunchCount, userState.daysSinceFirstLaunch) { userState }
            val stableUserProfile = remember(userProfile?.displayName, userProfile?.mbti, userProfile?.zodiac) { userProfile }
            val stableIsLoading = remember(isLoadingProfile) { isLoadingProfile }

            UserCard(
                userState = stableUserState,
                userProfile = stableUserProfile,
                isLoadingProfile = stableIsLoading,
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
            val browseTestsText = stringResource(Res.string.new_test_home)
            val quickStartText = stringResource(Res.string.quick_quiz_home)
            val userMatchText = StringResources.userMatchHome()

            UserActions(
                onClickAction = { actionName ->
                    when (actionName) {
                        browseTestsText -> { // "Browse Tests"
                            router.goToQuickTestScreen()
                        }

                        quickStartText -> { // "Quick Start"
                            router.goToDefaultQuizScreen()
                        }

                        userMatchText -> {
                            // TODO: Implement user match functionality
                        }

                        else -> {

                        }
                    }

                }
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            FriendsSection(
                onFriendsClick = {
                    router.goToFriends()
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
        if (isLoadingProfile) {
            items(3) { // Show 3 loading placeholders
                NeoBrutalistLoadingCard()
                Spacer(Modifier.height(8.dp))
            }
        } else {
            items(testResultList) { recentTestsData ->
                println("RecentTestData-->TestType-->${recentTestsData.testType}")
                RecentTestCard(
                    testerName = recentTestsData.testerName,
                    friendName = recentTestsData.friendInfo?.displayName ?: "",
                    friendMbti = recentTestsData.friendInfo?.mbtiType ?: "",
                    testerMbti = recentTestsData.testerMbti,
                    testResult = recentTestsData.testResult,
                    testerType = recentTestsData.testerType,
                    bgColor = recentTestsData.resultBg,
                    testType = if (recentTestsData.testType == QuizType.Compat) "match" else "solo",
                    clickAction = {
                        if (recentTestsData.testType == QuizType.Compat) {
                            router.goToUserMatchWithFriend(
                                recentTestsData.testID,
                                recentTestsData.friendInfo?.userId.orEmpty()
                            )
                        } else {
                            router.goToUserMatch(recentTestsData.testID)
                        }

//                    if (recentTestsData.testType == QuizType.Single){
//                        router.goToTestResultScreen(recentTestsData.testID)
//                    }else{
//                        router.goToTestResultScreen(recentTestsData.testID)
//                    }

                    }
                )
                Spacer(Modifier.height(8.dp))
            }
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

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        println("HomeScreen: User.hasChangedName-->${userProfile?.hasChangedName}")
        SettingsBottomSheet(
            router = router,
            isPremium = PremiumChecker.isPremium,
            currentName = userProfile?.displayName ?: "User",
            hasChangedName = userProfile?.hasChangedName.orFalse(),
            onDismiss = {
                showSettingsSheet = false
            },
            onNameChange = { newName ->
                val success = vm.updateProfile(newName)
                if (success) {
                    showSettingsSheet = false
                }
            },
            onUpgradeTapped = {
                router.goToPaywall()
                showSettingsSheet = false
            }
        )
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
            onClickAction = onClickAction
        )
    }
    Spacer(Modifier.height(8.dp))
//    Row(
//        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        QuickActionsCard(
//            backgroundColor = Color(0xFF9C27B0),
//            image = Res.drawable.hearth_desc,
//            actionText = "User Match",
//            textColor = Color.White,
//            imageColor = Color.White,
//            onClickAction = onClickAction
//        )
//        Spacer(Modifier.width(8.dp))
//        QuickActionsCard(
//            backgroundColor = Color(0xFF4DD0E1),
//            image = Res.drawable.profile_home,
//            actionText = "View All",
//            onClickAction = { onClickAction("View All") }
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
    onClickAction: (actionId: String) -> Unit
) {
    val screenSize = screenSize()
    val cardWidth = screenSize.width / 2.2
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = backgroundColor,
        modifier = Modifier.width(cardWidth.dp).clickable() {
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
    // Remember stable text values to prevent flicker - use actual string values as keys
    val mbtiValue = userProfile?.mbti
    val zodiacValue = userProfile?.zodiac
    val displayNameValue = userProfile?.displayName

    val stableMbti = remember(mbtiValue) { mbtiValue }
    val stableZodiac = remember(zodiacValue) { zodiacValue }
    val stableDisplayName = remember(displayNameValue) { displayNameValue }

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
                text = if (userState.isFirstTimeUser) {
                    StringResources.welcomeToAishou()
                } else {
                    if (!stableDisplayName.isNullOrBlank()) {
                        StringResources.welcomeBackWithName(stableDisplayName)
                    } else {
                        StringResources.welcomeBack()
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
            )
            Text(
                text = if (userState.isFirstTimeUser) {
                    StringResources.cosmicPersonalityDiscover()
                } else {
                    val days = userState.daysSinceFirstLaunch ?: 0
                    val formattedLaunchCount =
                        FormatUtils.formatLaunchCount(userState.appLaunchCount)
                    if (days > 0) {
                        val formattedDays = FormatUtils.formatDaysActive(days)
                        StringResources.launchCountDaysActive(formattedLaunchCount, formattedDays)
                    } else {
                        StringResources.launchCountOnly(formattedLaunchCount)
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
                                    text = stableMbti ?: StringResources.unknown(),
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
                                text = stableZodiac ?: StringResources.unknown(),
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
private fun FriendsSection(
    onFriendsClick: () -> Unit
) {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = Color(0xFF9C27B0), // Purple color for friends
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(role = Role.Button) {
                onFriendsClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.profile_home),
                    contentDescription = "Friends",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Friends",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = StringResources.connectWithFriends(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            Text(
                text = "→",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
private fun HomeHeader(
    vm: HomeViewModel,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val notificationCount by vm.unreadNotificationCount.collectAsStateWithLifecycle()

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Notification Button with Badge
                Box {
                    Image(
                        painter = painterResource(Res.drawable.bell),
                        contentDescription = StringResources.notificationsContentDescription(),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(role = Role.Button) {
                                onNotificationClick()
                            }
                            .padding(2.dp)
                    )

                    // Badge
                    if (notificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .offset(x = 6.dp, y = (-2).dp)
                                .background(
                                    Color(0xFFE91E63),
                                    androidx.compose.foundation.shape.CircleShape
                                )
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (notificationCount > 9) "9+" else notificationCount.toInt()
                                    .toString(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                // Profile Button
//                Image(
//                    painter = painterResource(Res.drawable.profile_home),
//                    contentDescription = "Profile",
//                    alignment = Alignment.Center,
//                    modifier = Modifier.size(24.dp).clickable(){
//                        onProfileClick.invoke()
//                    }
//                )

                // Settings Button
                Image(
                    painter = painterResource(Res.drawable.settings_home),
                    contentDescription = StringResources.settingsContentDescription(),
                    alignment = Alignment.Center,
                    modifier = Modifier.size(24.dp)
                        .clickable(role = Role.Button) {
                            onSettingsClick()
                        }
                        .padding(2.dp)
                )
            }
        }
    }
}

@Composable
private fun NeoBrutalistLoadingCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated loading circle with neobrutalist style
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color(0xFF66BB6A),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Loading placeholder for text
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                            color = Color(0xFFF0F0F0),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }

            // Loading score placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(10.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color(0xFFFFA726),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}