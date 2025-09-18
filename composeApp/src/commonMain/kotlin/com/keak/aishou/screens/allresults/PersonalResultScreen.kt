package com.keak.aishou.screens.allresults

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.personal_screen_send_to_friend
import aishou.composeapp.generated.resources.quick_tests_mbti
import aishou.composeapp.generated.resources.quick_tests_zodiac
import aishou.composeapp.generated.resources.quiz_screen_finish_test
import aishou.composeapp.generated.resources.quiz_screen_title
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.ScreenHeader
import com.keak.aishou.misc.BackGroundBrush.resultScreenBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun PersonalResultScreen(
    router: Router
){
    val screenSizeParam = screenSize()
    val desireWidth = screenSizeParam.width / 3

    Box(
        modifier = Modifier.fillMaxSize().background(
            resultScreenBrush
        ).padding(horizontal = 16.dp)
    ){
        Column(
            modifier = Modifier.zIndex(2f)
        ){
            ScreenHeader(
                modifier = Modifier.fillMaxWidth(),
                screenName = stringResource(Res.string.quiz_screen_title),
                backGroundColor = Color.Black,
                textColor = Color.White,
                fontSize = 15,
                backAction = {
                    router.goBack()
                }
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn (
                modifier = Modifier.zIndex(1f)
            ){
                item {
                    HeaderMatchResult()
                    Spacer(Modifier.height(16.dp))
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        UserCardOne(
                            desireWidth,
                            modifier = Modifier.weight(1f),
                            userName = "Sakura",
                            userMbti = "ENFP",
                            userZodiac = "Gemini",
                            colorListMbti = listOf(
                                Color(0xFFA78BFA), // purple-400
                                Color(0xFFFEC407A),
                            ),
                            colorListZodiac = listOf(
                                Color(0xFFFFEE58), // purple-400
                                Color(0xFFFFA726),
                            )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    Explanation(
                        mbtiTitle = stringResource(Res.string.quick_tests_mbti),
                        zodiacTitle = stringResource(Res.string.quick_tests_zodiac),
                    )
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(
                            role = Role.Button
                        ){

                        },
                        backgroundColor = Color.Cyan
                    ) {
                        Text(
                            text = stringResource(Res.string.personal_screen_send_to_friend),
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }




        }
    }
}