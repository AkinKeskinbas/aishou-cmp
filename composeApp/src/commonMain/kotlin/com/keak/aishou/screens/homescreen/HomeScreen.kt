package com.keak.aishou.screens.homescreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.best_matches_home
import aishou.composeapp.generated.resources.cosmic_match_home
import aishou.composeapp.generated.resources.hearth_desc
import aishou.composeapp.generated.resources.hey_text_home
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
import aishou.composeapp.generated.resources.view_all_home
import aishou.composeapp.generated.resources.view_all_tests_home
import aishou.composeapp.generated.resources.your_cosmic_profile_home
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(router: Router, vm: HomeViewModel) {
    vm.test()
    val testResultList = listOf(
        RecentTestsData(
            testerName = "Alex",
            testerMbti = "ENFP",
            testResult = "94",
            testerType = TesterType.PARTNER,
            testerUserId = "1",
            resultBg = Color(0xFF66BB6A),
            testID = "1"
        ),
        RecentTestsData(
            testerName = "Sam",
            testerMbti = "ISTJ",
            testResult = "78",
            testerType = TesterType.FRIEND,
            testerUserId = "2",
            resultBg = Color(0xFFFFA726),
            testID = "1"
        ),
        RecentTestsData(
            testerName = "Jordan",
            testerMbti = "ENTP",
            testResult = "86",
            testerType = TesterType.PARTNER,
            testerUserId = "3",
            resultBg = Color(0xFFFFEB3B),
            testID = "1"
        )
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(
            brush = BackGroundBrush.homNeoBrush
        )
    ) {
        stickyHeader {
            HomeHeader()
        }
        item {
            UserCard()
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
                    router.goToTestResultScreen(recentTestsData.testID)
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
    FRIEND, PARTNER, COWORKER, FAMILY
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
private fun UserCard() {
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
                text = stringResource(Res.string.hey_text_home),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
            )
            Text(
                text = stringResource(Res.string.your_cosmic_profile_home),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
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
                            Text(
                                text = "INFP",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
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
                        Text(
                            text = "Pisces",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier,
                        )
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