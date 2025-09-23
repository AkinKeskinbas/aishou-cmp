package com.keak.aishou.screens.allresults

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.screen_name_all_results
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.keak.aishou.components.RecentTestCard
import com.keak.aishou.components.ScreenHeader
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.screens.homescreen.RecentTestsData
import com.keak.aishou.screens.homescreen.TesterType
import com.keak.aishou.data.api.QuizType
import org.jetbrains.compose.resources.stringResource

@Composable
fun AllResultsScreen(
    router: Router,
    viewModel: AllResultsViewModel = koinViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isLoadingProfile by viewModel.isLoadingProfile.collectAsStateWithLifecycle()
    val profileError by viewModel.profileError.collectAsStateWithLifecycle()

    // Convert all API data to display format (no limit for All Results)
    val testResultList = userProfile?.let { profile ->
        println("AllResultsScreen: Profile MBTI: ${profile.mbti}, Zodiac: ${profile.zodiac}")
        println("AllResultsScreen: Solo quizzes count: ${profile.soloQuizzes.size}")
        println("AllResultsScreen: Match quizzes count: ${profile.matchQuizzes.size}")

        val soloTests = profile.soloQuizzes.mapIndexed { index, soloQuiz ->
            RecentTestsData(
                testerName = "Me",
                testerMbti = profile.mbti ?: "Unknown",
                testResult = soloQuiz.totalScore?.toString() ?: "N/A",
                testerType = TesterType.MYSELF,
                testerUserId = soloQuiz.submissionId ?: "",
                resultBg = when (index % 3) {
                    0 -> Color(0xFF66BB6A)
                    1 -> Color(0xFFFFA726)
                    else -> Color(0xFFFFEB3B)
                },
                testID = soloQuiz.testId ?: "",
                friendInfo = null,
                testType = QuizType.Single
            )
        }

        val matchTests = profile.matchQuizzes.mapIndexed { index, matchQuiz ->
            RecentTestsData(
                testerName = "Me",
                testerMbti = profile.mbti ?: "Unknown",
                testResult = matchQuiz.score?.toString() ?: "N/A",
                testerType = TesterType.PARTNER,
                testerUserId = matchQuiz.compatibilityId ?: "",
                resultBg = when (index % 3) {
                    0 -> Color(0xFFEC407A)
                    1 -> Color(0xFFFF7043)
                    else -> Color(0xFF9C27B0)
                },
                testID = matchQuiz.testId ?: "",
                friendInfo = matchQuiz.friendInfo,
                testType = QuizType.Compat
            )
        }

        // Combine all tests
        (soloTests + matchTests).filter { it.testID.isNotBlank() }
    } ?: emptyList()

    println("AllResultsScreen: Final test result list size: ${testResultList.size}")
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(
           Color(0xFFFEA079)
        )
    ) {

        stickyHeader {
            ScreenHeader(
                modifier = Modifier.fillMaxWidth(),
                screenName = stringResource(Res.string.screen_name_all_results),
                backAction = {
                    router.goBack()
                }
            )
            Spacer(Modifier.height(16.dp))
        }
        items(testResultList) { recentTestsData ->
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
                    router.goToTestResultScreen(recentTestsData.testID)
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }

}