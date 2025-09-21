package com.keak.aishou.screens.results

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
            text = "🎉 Your Personality Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Based on your responses, here's what we discovered about you!",
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
                        text = "💡 Personal Insights",
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
                        text = "Continue to Home",
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
                    onClick = { router.goToAllResultScreen() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "View All My Results",
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

private fun getMBTITypeName(mbtiType: String): String {
    return when (mbtiType.uppercase()) {
        "INTJ" -> "The Architect"
        "INTP" -> "The Thinker"
        "ENTJ" -> "The Commander"
        "ENTP" -> "The Debater"
        "INFJ" -> "The Advocate"
        "INFP" -> "The Mediator"
        "ENFJ" -> "The Protagonist"
        "ENFP" -> "The Campaigner"
        "ISTJ" -> "The Logistician"
        "ISFJ" -> "The Protector"
        "ESTJ" -> "The Executive"
        "ESFJ" -> "The Consul"
        "ISTP" -> "The Virtuoso"
        "ISFP" -> "The Adventurer"
        "ESTP" -> "The Entrepreneur"
        "ESFP" -> "The Entertainer"
        else -> "The Unique One"
    }
}

private fun getMBTIDescription(mbtiType: String): String {
    return when (mbtiType.uppercase()) {
        "INTJ" -> "Imaginative and strategic thinkers, with a plan for everything"
        "INTP" -> "Innovative inventors with an unquenchable thirst for knowledge"
        "ENTJ" -> "Bold, imaginative and strong-willed leaders"
        "ENTP" -> "Smart and curious thinkers who love intellectual challenges"
        "INFJ" -> "Creative and insightful, inspired and independent"
        "INFP" -> "Poetic, kind and altruistic people, always eager to help"
        "ENFJ" -> "Charismatic and inspiring leaders, able to mesmerize listeners"
        "ENFP" -> "Enthusiastic, creative and sociable free spirits"
        "ISTJ" -> "Practical and fact-minded, reliable and responsible"
        "ISFJ" -> "Warm-hearted and dedicated, always ready to protect loved ones"
        "ESTJ" -> "Excellent administrators, unsurpassed at managing things"
        "ESFJ" -> "Extraordinarily caring, social and popular people"
        "ISTP" -> "Bold and practical experimenters, masters of all kinds of tools"
        "ISFP" -> "Flexible and charming artists, always ready to explore possibilities"
        "ESTP" -> "Smart, energetic and very perceptive people"
        "ESFP" -> "Spontaneous, energetic and enthusiastic people"
        else -> "A unique personality with special qualities"
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

private fun getZodiacDescription(zodiacSign: String): String {
    return when (zodiacSign.lowercase()) {
        "aries" -> "Bold, ambitious, and always ready for action"
        "taurus" -> "Reliable, patient, and devoted to comfort"
        "gemini" -> "Curious, adaptable, and great communicators"
        "cancer" -> "Nurturing, intuitive, and deeply emotional"
        "leo" -> "Confident, generous, and natural-born leaders"
        "virgo" -> "Analytical, practical, and detail-oriented"
        "libra" -> "Diplomatic, fair-minded, and harmony-seeking"
        "scorpio" -> "Passionate, resourceful, and determined"
        "sagittarius" -> "Adventurous, optimistic, and freedom-loving"
        "capricorn" -> "Ambitious, disciplined, and goal-oriented"
        "aquarius" -> "Independent, innovative, and humanitarian"
        "pisces" -> "Compassionate, artistic, and deeply intuitive"
        else -> "Unique and special in every way"
    }
}