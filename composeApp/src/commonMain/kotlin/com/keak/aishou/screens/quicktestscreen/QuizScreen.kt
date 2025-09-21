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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.NeoBrutalistProgressBar
import com.keak.aishou.components.ScreenHeader
import com.keak.aishou.components.MBTILoadingOverlay
import com.keak.aishou.screens.results.MBTIResultScreen
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuizScreen(
    router: Router,
    quizID: String?,
    viewModel: QuizViewModel
) {
    val screenWidth = screenSize().width
    val desiredWidth = screenWidth / 1.5

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(quizID) {
        if (quizID != null) {
            viewModel.onEvent(QuizUiEvent.LoadQuestions(quizID, 1))
        } else {
            viewModel.onEvent(QuizUiEvent.LoadQuickQuiz)
        }
    }

    val progress = viewModel.getProgress()
    val totalQuestions = uiState.questions.size

    // Show MBTI Result Screen if personality test is completed
    if (uiState.isMBTITest && uiState.submissionSuccess && uiState.personalityResult != null) {
        MBTIResultScreen(
            personalityResult = uiState.personalityResult!!,
            router = router
        )
        return
    }



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
                current = (progress * totalQuestions).toInt(),
                total = totalQuestions,
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
                items(uiState.questions, key = { it.index }) { question ->
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
                                text = "Question ${question.index}",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = question.text,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            question.choices.forEach { choice ->
                                val isSelected = uiState.answers[question.index] == choice.key
                                val bg = if (isSelected) Color.Cyan else Color.White
                                val shadow = if (isSelected) Color(0xFF34D399) else Color.Yellow
                                val textColor = if (isSelected) Color.Black else Color.Black
                                //Choices
                                NeoBrutalistCardViewWithFlexSize(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable(role = Role.Button) {
                                            viewModel.onEvent(QuizUiEvent.SelectAnswer(question.index, choice.key))
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
                            enabled = !uiState.isSubmitting && !uiState.submissionSuccess,
                            role = Role.Button
                        ){
                            if (uiState.submissionError != null) {
                                // Reset state and retry
                                viewModel.onEvent(QuizUiEvent.ResetSubmissionState)
                            } else {
                                if (quizID != null) {
                                    viewModel.onEvent(QuizUiEvent.SubmitQuiz(quizID, 1))
                                } else {
                                    viewModel.onEvent(QuizUiEvent.SubmitQuickQuiz)
                                }
                            }
                        },
                        backgroundColor = when {
                            uiState.isSubmitting -> Color.Gray
                            uiState.submissionSuccess -> Color(0xFF4FFFB3) // Green for success
                            uiState.submissionError != null -> Color(0xFFF87171) // Red for error
                            viewModel.canFinish() -> Color.Cyan
                            else -> Color(0xFFFFB74D) // Orange for incomplete
                        }
                    ) {
                        Text(
                            text = when {
                                uiState.isSubmitting -> if (uiState.isMBTITest) "Analyzing your personality..." else "Submitting..."
                                uiState.submissionSuccess -> "✅ Completed Successfully!"
                                uiState.submissionError != null -> "❌ Error - Tap to retry"
                                viewModel.canFinish() -> stringResource(Res.string.quiz_screen_finish_test)
                                else -> "Complete all questions to finish"
                            },
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }

        // Incomplete Warning Dialog
        if (uiState.showIncompleteWarning) {
            IncompleteQuizWarningDialog(
                unansweredCount = uiState.questions.size - uiState.answers.size,
                totalQuestions = uiState.questions.size,
                onDismiss = { viewModel.onEvent(QuizUiEvent.DismissIncompleteWarning) }
            )
        }

        // MBTI Loading Overlay
        if (uiState.isSubmitting && uiState.isMBTITest) {
            MBTILoadingOverlay()
        }
    }
}

@Composable
private fun IncompleteQuizWarningDialog(
    unansweredCount: Int,
    totalQuestions: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Quiz Not Complete",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Text(
                text = "You have $unansweredCount unanswered questions out of $totalQuestions total questions.\n\nPlease answer all questions before submitting your test to get accurate results.",
                color = Color.Black
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "OK, I'll finish",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.White,
        tonalElevation = 8.dp
    )
}