package com.keak.aishou.components

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.empty_star
import aishou.composeapp.generated.resources.hearth_desc
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.screens.homescreen.TesterType
import com.keak.aishou.data.api.SoloQuizResult
import com.keak.aishou.data.api.CompatibilityResult
import org.jetbrains.compose.resources.painterResource

// Overloaded function for SoloQuizResult
@Composable
fun RecentTestCard(
    soloQuiz: SoloQuizResult,
    userDisplayName: String,
    modifier: Modifier = Modifier,
    clickAction: () -> Unit
) {
    RecentTestCard(
        testerName = userDisplayName,
        friendName = "",
        friendMbti = "",
        testerMbti = "", // We don't have MBTI in solo quiz result
        testResult = soloQuiz.totalScore?.toString() ?: "0",
        modifier = modifier,
        testerType = TesterType.MYSELF,
        testType = "solo",
        bgColor = Color(0xFF4CAF50),
        clickAction = clickAction
    )
}

// Overloaded function for CompatibilityResult
@Composable
fun RecentTestCard(
    compatibilityResult: CompatibilityResult,
    userDisplayName: String,
    modifier: Modifier = Modifier,
    clickAction: () -> Unit
) {
    RecentTestCard(
        testerName = userDisplayName,
        friendName = compatibilityResult.friendInfo?.displayName ?: "Unknown",
        friendMbti = compatibilityResult.friendInfo?.mbtiType ?: "",
        testerMbti = "", // We could get this from user profile
        testResult = compatibilityResult.score?.toString() ?: "0",
        modifier = modifier,
        testerType = TesterType.PARTNER,
        testType = "match",
        bgColor = Color(0xFFFF9800),
        clickAction = clickAction
    )
}

// Original function for backward compatibility
@Composable
fun RecentTestCard(
    testerName: String,
    friendName: String = "",
    friendMbti: String = "",
    testerMbti: String,
    testResult: String,
    modifier: Modifier = Modifier,
    testerType: TesterType,
    testType: String,
    bgColor: Color,
    clickAction: () -> Unit
) {
    val cardImage =
        if (testerType == TesterType.PARTNER) Res.drawable.hearth_desc else Res.drawable.empty_star
    val cardBgColor = if (testerType == TesterType.PARTNER) Color(0xFFEC407A) else Color(0xFF4DD0E1)
    val isMatchCard = testType == "match"
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = Color.White,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable() {
            clickAction.invoke()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).background(cardBgColor).border(1.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(cardImage),
                    modifier = Modifier.size(20.dp),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(if (testerType == TesterType.PARTNER) Color.White else Color.Black)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = testerName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier,
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.background(
                    if (isMatchCard) Color(0xFFEF5350) else Color(
                        0xFFBA68C8
                    )
                ).border(1.dp, Color.Black)
                    .wrapContentSize().padding(4.dp)
            ) {
                Text(
                    text = if (isMatchCard) friendMbti else testerMbti,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier,
                    color = Color.White
                )
            }
            if (isMatchCard) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = friendName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier,
                )
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier.background(bgColor).border(1.dp, Color.Black).wrapContentSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = "$testResult%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier,
                )
            }
        }


    }
}