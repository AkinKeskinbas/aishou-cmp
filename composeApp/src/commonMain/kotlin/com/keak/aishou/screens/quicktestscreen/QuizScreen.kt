package com.keak.aishou.screens.quicktestscreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.quiz_screen_finish_test
import aishou.composeapp.generated.resources.quiz_screen_title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.NeoBrutalistProgressBar
import com.keak.aishou.components.ScreenHeader
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuizScreen(
    router: Router,
    quizID: String
) {
    val screenWidth = screenSize().width
    val desiredWidth = screenWidth / 1.5
    val test = listOf(
        Question(
            testId = "personal",
            testTitle = "Personality",
            version = 1,
            index = 1,
            quizType = QuizType.Single,
            questionText = "At a party, you would rather:",
            choices = listOf(
                Choice(
                    key = "A",
                    text = "Talk to many different people"
                ),
                Choice(
                    key = "B",
                    text = "Have deep conversations with a few close friends"
                ),
            )
        ),
        Question(
            testId = "personal",
            testTitle = "Personality",
            version = 1,
            index = 2,
            quizType = QuizType.Single,
            questionText = "At a party, you would rather:",
            choices = listOf(
                Choice(
                    key = "A",
                    text = "Talk to many different people"
                ),
                Choice(
                    key = "B",
                    text = "Have deep conversations with a few close friends"
                ),
            )
        ),
        Question(
            testId = "personal",
            testTitle = "Personality",
            version = 1,
            index = 3,
            quizType = QuizType.Single,
            questionText = "At a party, you would rather:",
            choices = listOf(
                Choice(
                    key = "A",
                    text = "Talk to many different people"
                ),
                Choice(
                    key = "B",
                    text = "Have deep conversations with a few close friends"
                ),
            )
        )
    )
    val selections = remember { mutableStateMapOf<Int, String>() }
    val currentQuestion by remember { derivedStateOf { selections.size } }



    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFDCE775), Color(0xFFFFA726), Color(0xFFF44336)
                    )
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .zIndex(2f)               // Üstte dursun
        ) {
            ScreenHeader(
                modifier = Modifier.width(desiredWidth.dp),
                screenName = stringResource(Res.string.quiz_screen_title),
                backGroundColor = Color.Black,
                textColor = Color.White,
                fontSize = 15,
                backAction = {
                    router.goBack()
                }
            )
            Spacer(Modifier.height(16.dp))
            NeoBrutalistProgressBar(
                current = currentQuestion,
                total = 15,
                barHeight = 24.dp,
                trackColor = Color.White,
                fillColor = Color(0xFF34D399), // Emerald
                borderColor = Color.Black,
                borderWidth = 3.dp,
                cornerRadius = 0.dp,
                showStripes = true
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFDCE775),
                            Color(0xFFFFA726),
                            Color(0xFFF44336),
                        )
                    )
                )
                    .clipToBounds()            // gölgeler/çizimler üstten sızmasın
                    .zIndex(1f),
            ) {
                items(test, key = { it.index }) { question ->
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        backgroundColor = Color.Black,
                        borderColor = Color.White,
                        shadowColor = Color.Magenta
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = question.testTitle,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = question.questionText,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            question.choices.forEach { choice ->
                                val isSelected = selections[question.index] == choice.key
                                val bg = if (isSelected) Color.Cyan else Color.White
                                val shadow = if (isSelected) Color(0xFF34D399) else Color.Yellow
                                val textColor = if (isSelected) Color.Black else Color.Black
                                //Choices
                                NeoBrutalistCardViewWithFlexSize(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable(role = Role.Button) {
                                            val prev = selections[question.index]
                                            if (prev == null) {
                                                // İlk kez bu soruya cevap veriliyor → progress artacak (derived)
                                                selections[question.index] = choice.key
                                            } else if (prev != choice.key) {
                                                // Seçimi değiştiriyor → progress DEĞİŞMEZ (derived), sadece seçim güncellenir
                                                selections[question.index] = choice.key
                                            } else {
                                                // Aynı seçeneğe tekrar bastı → hiçbir şey yapma (progress artmaz)
                                            }
                                        },
                                    backgroundColor = bg,
                                    shadowColor = shadow
                                ) {
                                    Text(
                                        text = choice.text,
                                        color = textColor,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }

                        }

                    }
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(
                            enabled = currentQuestion == 15,
                            role = Role.Button
                        ){

                        },
                        backgroundColor = Color.Cyan
                    ) {
                        Text(
                            text = stringResource(Res.string.quiz_screen_finish_test),
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

        }
    }
}