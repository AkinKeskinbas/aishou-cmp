package com.keak.aishou.screens.allresults

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.screen_name_all_results
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.keak.aishou.components.RecentTestCard
import com.keak.aishou.components.ScreenHeader
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.screens.homescreen.RecentTestsData
import com.keak.aishou.screens.homescreen.TesterType
import org.jetbrains.compose.resources.stringResource

@Composable
fun AllResultsScreen(
    router: Router
) {
    val testResultList = listOf(
        RecentTestsData(
            testerName = "Alex",
            testerMbti = "ENFP",
            testResult = "94",
            testerType = TesterType.PARTNER,
            testerUserId = "1",
            resultBg = Color(0xFF66BB6A)
        ),
        RecentTestsData(
            testerName = "Sam",
            testerMbti = "ISTJ",
            testResult = "78",
            testerType = TesterType.FRIEND,
            testerUserId = "2",
            resultBg = Color(0xFFFFA726)
        ),
        RecentTestsData(
            testerName = "Jordan",
            testerMbti = "ENTP",
            testResult = "86",
            testerType = TesterType.PARTNER,
            testerUserId = "3",
            resultBg = Color(0xFFFFEB3B)
        )
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(
            brush = BackGroundBrush.homNeoBrush
        )
    ) {

        stickyHeader {
            ScreenHeader(
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
                bgColor = recentTestsData.resultBg
            )
            Spacer(Modifier.height(8.dp))
        }
    }

}