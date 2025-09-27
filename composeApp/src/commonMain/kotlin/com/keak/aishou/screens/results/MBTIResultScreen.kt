package com.keak.aishou.screens.results

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.mbti_result_based_on_your_response
import aishou.composeapp.generated.resources.mbti_result_be_premium
import aishou.composeapp.generated.resources.mbti_result_go_home
import aishou.composeapp.generated.resources.mbti_result_personal_insight
import aishou.composeapp.generated.resources.mbti_your_personality
import aishou.composeapp.generated.resources.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.data.api.PersonalityAssessResponse
import com.keak.aishou.navigation.Router
import org.jetbrains.compose.resources.stringResource

@Composable
fun MBTIResultScreen(
    personalityResult: PersonalityAssessResponse,
    router: Router
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Celebration Title
        Text(
            text = stringResource(Res.string.mbti_your_personality),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.mbti_result_based_on_your_response),
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // MBTI Type Card
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF4FFFB3),
            borderColor = Color.Black,
            shadowColor = Color.Black,
            borderWidth = 4.dp,
            shadowOffset = 8.dp,
            contentPadding = 24.dp,
            showBadge = true,
            badgeText = "MBTI TYPE",
            badgeBg = Color(0xFFFF66B2),
            badgeTextColor = Color.White
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = personalityResult.mbtiType,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = getMBTITypeName(personalityResult.mbtiType),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = getMBTIDescription(personalityResult.mbtiType),
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Zodiac Sign Card
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFFFE66D),
            borderColor = Color.Black,
            shadowColor = Color.Black,
            borderWidth = 4.dp,
            shadowOffset = 8.dp,
            contentPadding = 24.dp,
            showBadge = true,
            badgeText = "ZODIAC",
            badgeBg = Color(0xFF74A0FF),
            badgeTextColor = Color.White
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getZodiacEmoji(personalityResult.zodiacSign),
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = personalityResult.zodiacSign,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = getZodiacDescription(personalityResult.zodiacSign),
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Personality Insights (if available)
        personalityResult.personalityInsights?.let { insights ->
            Spacer(modifier = Modifier.height(24.dp))

            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFF87171),
                borderColor = Color.Black,
                shadowColor = Color.Black,
                borderWidth = 3.dp,
                shadowOffset = 6.dp,
                contentPadding = 20.dp
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.mbti_result_personal_insight),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = insights,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Continue to Home Button
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF4FFFB3),
                borderColor = Color.Black,
                shadowColor = Color.Black,
                borderWidth = 3.dp,
                shadowOffset = 4.dp,
                contentPadding = 0.dp,
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { router.goToHome() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(Res.string.mbti_result_go_home),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // View All Results Button
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White,
                borderColor = Color.Black,
                shadowColor = Color.Black,
                borderWidth = 2.dp,
                shadowOffset = 3.dp,
                contentPadding = 0.dp,
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { router.goToPaywall() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(Res.string.mbti_result_be_premium),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun getMBTITypeName(mbtiType: String): String {
    return when (mbtiType.uppercase()) {
        "INTJ" -> stringResource(Res.string.mbti_intj_name)
        "INTP" -> stringResource(Res.string.mbti_intp_name)
        "ENTJ" -> stringResource(Res.string.mbti_entj_name)
        "ENTP" -> stringResource(Res.string.mbti_entp_name)
        "INFJ" -> stringResource(Res.string.mbti_infj_name)
        "INFP" -> stringResource(Res.string.mbti_infp_name)
        "ENFJ" -> stringResource(Res.string.mbti_enfj_name)
        "ENFP" -> stringResource(Res.string.mbti_enfp_name)
        "ISTJ" -> stringResource(Res.string.mbti_istj_name)
        "ISFJ" -> stringResource(Res.string.mbti_isfj_name)
        "ESTJ" -> stringResource(Res.string.mbti_estj_name)
        "ESFJ" -> stringResource(Res.string.mbti_esfj_name)
        "ISTP" -> stringResource(Res.string.mbti_istp_name)
        "ISFP" -> stringResource(Res.string.mbti_isfp_name)
        "ESTP" -> stringResource(Res.string.mbti_estp_name)
        "ESFP" -> stringResource(Res.string.mbti_esfp_name)
        else -> stringResource(Res.string.mbti_unique_name)
    }
}

@Composable
private fun getMBTIDescription(mbtiType: String): String {
    return when (mbtiType.uppercase()) {
        "INTJ" -> stringResource(Res.string.mbti_intj_description)
        "INTP" -> stringResource(Res.string.mbti_intp_description)
        "ENTJ" -> stringResource(Res.string.mbti_entj_description)
        "ENTP" -> stringResource(Res.string.mbti_entp_description)
        "INFJ" -> stringResource(Res.string.mbti_infj_description)
        "INFP" -> stringResource(Res.string.mbti_infp_description)
        "ENFJ" -> stringResource(Res.string.mbti_enfj_description)
        "ENFP" -> stringResource(Res.string.mbti_enfp_description)
        "ISTJ" -> stringResource(Res.string.mbti_istj_description)
        "ISFJ" -> stringResource(Res.string.mbti_isfj_description)
        "ESTJ" -> stringResource(Res.string.mbti_estj_description)
        "ESFJ" -> stringResource(Res.string.mbti_esfj_description)
        "ISTP" -> stringResource(Res.string.mbti_istp_description)
        "ISFP" -> stringResource(Res.string.mbti_isfp_description)
        "ESTP" -> stringResource(Res.string.mbti_estp_description)
        "ESFP" -> stringResource(Res.string.mbti_esfp_description)
        else -> stringResource(Res.string.mbti_unique_description)
    }
}

private fun getZodiacEmoji(zodiacSign: String): String {
    return when (zodiacSign.lowercase()) {
        "aries" -> "♈"
        "taurus" -> "♉"
        "gemini" -> "♊"
        "cancer" -> "♋"
        "leo" -> "♌"
        "virgo" -> "♍"
        "libra" -> "♎"
        "scorpio" -> "♏"
        "sagittarius" -> "♐"
        "capricorn" -> "♑"
        "aquarius" -> "♒"
        "pisces" -> "♓"
        else -> "⭐"
    }
}

@Composable
private fun getZodiacDescription(zodiacSign: String): String {
    return when (zodiacSign.lowercase()) {
        "aries" -> stringResource(Res.string.zodiac_aries_description)
        "taurus" -> stringResource(Res.string.zodiac_taurus_description)
        "gemini" -> stringResource(Res.string.zodiac_gemini_description)
        "cancer" -> stringResource(Res.string.zodiac_cancer_description)
        "leo" -> stringResource(Res.string.zodiac_leo_description)
        "virgo" -> stringResource(Res.string.zodiac_virgo_description)
        "libra" -> stringResource(Res.string.zodiac_libra_description)
        "scorpio" -> stringResource(Res.string.zodiac_scorpio_description)
        "sagittarius" -> stringResource(Res.string.zodiac_sagittarius_description)
        "capricorn" -> stringResource(Res.string.zodiac_capricorn_description)
        "aquarius" -> stringResource(Res.string.zodiac_aquarius_description)
        "pisces" -> stringResource(Res.string.zodiac_pisces_description)
        else -> stringResource(Res.string.zodiac_unknown_description)
    }
}