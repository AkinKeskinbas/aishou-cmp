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

    // Convert all API solved tests to display format (no limit)
    val testResultList = userProfile?.let { profile ->
        println("AllResultsScreen: Profile MBTI: ${profile.mbti}, Zodiac: ${profile.zodiac}")
        println("AllResultsScreen: Total tests: ${profile.solvedTests.size}")

        profile.solvedTests.mapIndexed { index, solvedTest ->
            println("AllResultsScreen: Converting test $index: id=${solvedTest.id}, type=${solvedTest.type}")
            RecentTestsData(
                testerName = "Me",
                testerMbti = profile.mbti ?: "Unknown",
                testResult = solvedTest.score?.toString() ?: "N/A",
                testerType = TesterType.MYSELF,
                testerUserId = solvedTest.id ?: "unknown_${index}",
                resultBg = when (index % 3) {
                    0 -> Color(0xFF66BB6A)
                    1 -> Color(0xFFFFA726)
                    else -> Color(0xFFFFEB3B)
                },
                testID = solvedTest.id ?: "unknown_${index}",
                testType = if (solvedTest.type == "match") QuizType.Compat else QuizType.Single
            )
        }.filter { it.testID.startsWith("unknown_").not() }
    } ?: emptyList()

    println("AllResultsScreen: Final test result list size: ${testResultList.size}")
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(
            brush = BackGroundBrush.homNeoBrush
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
    }

}