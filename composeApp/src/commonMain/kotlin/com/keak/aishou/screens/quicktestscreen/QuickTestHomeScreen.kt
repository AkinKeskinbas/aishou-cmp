package com.keak.aishou.screens.quicktestscreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.brain
import aishou.composeapp.generated.resources.crown
import aishou.composeapp.generated.resources.double_star
import aishou.composeapp.generated.resources.film
import aishou.composeapp.generated.resources.personality_type_home
import aishou.composeapp.generated.resources.quick_tests_brutal
import aishou.composeapp.generated.resources.quick_tests_discover_your_true_self
import aishou.composeapp.generated.resources.quick_tests_get_premium_for_all_tests
import aishou.composeapp.generated.resources.quick_tests_personality_test
import aishou.composeapp.generated.resources.quick_tests_screen_title
import aishou.composeapp.generated.resources.star
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.ScreenHeader
import com.keak.aishou.navigation.Router
import com.keak.aishou.purchase.PremiumChecker
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuickTestHomeScreen(
    router: Router,
    viewModel: QuickTestHomeScreenViewModel
) {

    val cards = listOf(
        TestCardModel(
            title = "MIND + STARS",
            subtitle = "MBTI & Zodiac Fusion!",
            from = Color(0xFFbef264), // lime-300
            via = Color(0xFFfb923c), // orange-400
            to = Color(0xFFef4444), // red-500
            shadowColor = Color(0xFF22d3ee),
            cardImage = Res.drawable.brain
        ),
        TestCardModel(
            title = "RAPID TYPE",
            subtitle = "Quick Vibe Check",
            from = Color(0xFF60a5fa),
            via = Color(0xFFc084fc),
            to = Color(0xFFf472b6),
            shadowColor = Color(0xFF22c55e),
            cardImage = Res.drawable.film
        ),
        TestCardModel(
            title = "MIND + STARS",
            subtitle = "MBTI & Zodiac Fusion!",
            from = Color(0xFFbef264), // lime-300
            via = Color(0xFFfb923c), // orange-400
            to = Color(0xFFef4444), // red-500
            shadowColor = Color(0xFF22d3ee),
            cardImage = Res.drawable.double_star
        ),
        TestCardModel(
            title = "RAPID TYPE",
            subtitle = "Quick Vibe Check",
            from = Color(0xFF60a5fa),
            via = Color(0xFFc084fc),
            to = Color(0xFFf472b6),
            shadowColor = Color(0xFF22c55e),
            cardImage = Res.drawable.brain
        )

    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFbef264), // from-lime-300
                        Color(0xFFfb923c), // via-orange-400
                        Color(0xFFef4444)  // to-red-500
                    )
                )
            )
            .padding(16.dp)
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            stickyHeader {
                ScreenHeader(
                    modifier = Modifier.fillMaxWidth(),
                    screenName = stringResource(Res.string.quick_tests_screen_title),
                    backAction = {
                        router.goBack()
                    }
                )
            }
            item {
                MainHeader()
                Spacer(Modifier.height(8.dp))
            }
            item {
                Box(Modifier.fillMaxWidth()) {
                    SecondHeader(modifier = Modifier.align(Alignment.Center))
                }
                Spacer(Modifier.height(8.dp))

            }
            item {
                InfoHeader()
                Spacer(Modifier.height(8.dp))
            }
            item {
                PremiumSubHeader()
                Spacer(Modifier.height(8.dp))
            }
            items(cards) { card ->
                BrutalCard(
                    title = card.title,
                    subtitle = card.subtitle,
                    gradient = listOf(card.from, card.via, card.to),
                    shadowColor = card.shadowColor,
                    isPremium = PremiumChecker.isPremium,
                    cardImage = card.cardImage,
                    onClick = {
                        router.goToQuizScreen(quizID = card.title)
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PremiumSubHeader() {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = Color.Black,
        borderColor = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowColor = Color(0xFFFFEE58)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.crown),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.quick_tests_get_premium_for_all_tests),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                modifier = Modifier,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(Res.drawable.crown),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }

    }
}

@Composable
private fun InfoHeader() {
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = Color(0xFFDCE775),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shadowColor = Color(0xFF9C27B0)
    ) {
        Text(
            text = stringResource(Res.string.quick_tests_discover_your_true_self),
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SecondHeader(modifier: Modifier) {
    BrutalHeader(
        modifier = modifier,
        headerText = stringResource(Res.string.quick_tests_personality_test),
        textSize = 32
    )
}

@Composable
private fun MainHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.star),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.width(8.dp))
        BrutalHeader(
            modifier = Modifier,
            headerText = stringResource(Res.string.quick_tests_brutal),
            textSize = 54
        )
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(Res.drawable.star),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    }
}