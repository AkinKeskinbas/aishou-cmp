package com.keak.aishou.screens.allresults

import BouncingHeartSmooth
import MatchProgressCard
import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.match_results
import aishou.composeapp.generated.resources.quick_tests_mbti_match
import aishou.composeapp.generated.resources.quick_tests_zodiac_match
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.NeoBrutalistCircle
import com.keak.aishou.misc.BackGroundBrush.resultScreenBrush
import com.keak.aishou.misc.BackGroundBrush.resultScreenHeaderBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun TestResultScreen(router: Router, testID: String) {
    val animatedPercent = rememberCountUpPercentage(63)
    val screenSizeParam = screenSize()
    val desireWidth = screenSizeParam.width / 3
    val hearts = remember { List(50) { "h$it" } } // React’teki hearts array

    Box(
        modifier = Modifier.fillMaxSize().background(
            resultScreenBrush
        ).padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.zIndex(2f).verticalScroll(rememberScrollState())
        ) {
            HeaderMatchResult()
            Spacer(Modifier.height(16.dp))
            Row {
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
                Spacer(Modifier.width(16.dp))
                UserCardOne(
                    desireWidth, modifier = Modifier.weight(1f),
                    userName = "Akira",
                    userMbti = "INTJ",
                    userZodiac = "Scorpio",
                    colorListMbti = listOf(
                        Color(0xFF42A5F5), // purple-400
                        Color(0xFF26C6DA),
                    ),
                    colorListZodiac = listOf(
                        Color(0xFF66BB6A),
                        Color(0xFFD4E157),

                        )
                )

            }
            Spacer(Modifier.height(8.dp))
            MatchProgressCard(
                score = 95,
                maxScore = 100,
                modifier = Modifier.fillMaxWidth(),
                animateMillis = 7500,
                easing = FastOutSlowInEasing,
                barHeight = 22.dp,
                cornerRadius = 0.dp // 8.dp verirsen daha modern köşe
            )
//            Score(
//                modifier = Modifier.align(Alignment.CenterHorizontally),
//                percentage = animatedPercent
//            )
            Spacer(Modifier.height(8.dp))
            Explanation()
            Spacer(Modifier.height(8.dp))


        }
//        HeartsOverlay(
//            modifier = Modifier.zIndex(1f),
//            showScore = true,          // koşuluna göre aç/kapat
//            hearts = hearts,
//            heartSize = 16.dp,
//            amplitude = 22.dp,
//            color = Color(0xFFEC4899)  // pink-500
//        )

    }

}

@Composable
fun Explanation(
    mbtiTitle: String = stringResource(Res.string.quick_tests_mbti_match),
    zodiacTitle: String = stringResource(Res.string.quick_tests_zodiac_match)
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = null,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFAB47BC),
                    Color(0xFFEC407A),
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = mbtiTitle,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    thickness = 5.dp,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)).border(
                            3.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    Text(
                        text = "ENFP and INTJ make a powerful combination! Your spontaneous creativity (ENFP) perfectly complements their strategic planning (INTJ). This creates a dynamic balance of inspiration and execution.",
                        // textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 12.sp,

                        )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = null,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFEE58),
                    Color(0xFFFFA726),
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = zodiacTitle,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    thickness = 5.dp,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)).border(
                            3.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    Text(
                        text = "Gemini's adaptability and Scorpio's intensity create an exciting dynamic! Gemini brings lightness and curiosity while Scorpio adds depth and passion to the relationship.",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }

        }
    }
}

@Composable
fun UserCardOne(
    desireWidth: Float,
    modifier: Modifier,
    userName: String,
    userMbti: String,
    userZodiac: String,
    colorListMbti: List<Color>,
    colorListZodiac: List<Color>,
) {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = Color.White,
        modifier = modifier,
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = userName,
                modifier = Modifier,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(8.dp))
            //Name and Info Cards 1
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = null,
                cornerRadius = 8.dp,
                backgroundBrush = Brush.linearGradient(
                    colors = colorListMbti
                ),
                modifier = Modifier.width(desireWidth.dp)
            ) {
                Column {
                    Text(
                        text = "MBTI",
                        // textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = userMbti,
                        //textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = null,
                cornerRadius = 8.dp,
                backgroundBrush = Brush.linearGradient(
                    colors = colorListZodiac
                ),
                modifier = Modifier.width(desireWidth.dp)
            ) {
                Column {
                    Text(
                        text = "ZODIAC",
                        //textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = userZodiac,
                        //  textAlign = TextAlign.Center,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderMatchResult() {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = null,
        backgroundBrush = resultScreenHeaderBrush
    ) {
        Text(
            text = stringResource(Res.string.match_results),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            fontWeight = FontWeight.Black,
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}


@Composable
fun rememberCountUpPercentage(
    targetPercent: Int,
    durationMillis: Int = 4200
): Int {
    val clampedTarget = targetPercent.coerceIn(0, 100)
    val anim = remember { Animatable(0f) }

    LaunchedEffect(clampedTarget) {
        anim.stop()
        anim.animateTo(
            targetValue = clampedTarget.toFloat(),
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = LinearOutSlowInEasing
            )
        )
    }
    return anim.value.toInt()
}

@Composable
fun Score(modifier: Modifier, percentage: Int) {
    NeoBrutalistCircle(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "%$percentage",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            BouncingHeartSmooth(
                color = Color.White
            )
        }
    }
}
