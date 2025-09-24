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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var uiState by remember { mutableStateOf(TestUiState()) }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { newState ->
            uiState = newState
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
               Color(0xFF707CF3)
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
            items(uiState.tests) { test ->
                BrutalCard(
                    title = test.title,
                    subtitle = test.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    gradient = generateGradientFromColor(test.display.color),
                    shadowColor = parseColorWithAlpha(test.display.color),
                    isPremium = test.isPremium,
                    cardImage = mapCategoryToIcon(test.category),
                    onClick = {
                        // Check if test is premium and user is not premium
                        if (test.isPremium && !PremiumChecker.isPremium) {
                            router.goToPaywall()
                        } else {
                            router.goToQuizScreen(quizID = test.testId)
                        }
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

/**
 * Generate gradient colors from primary color
 */
private fun generateGradientFromColor(hexColor: String): List<Color> {
    val primaryColor = parseHexColor(hexColor)

    // Create variations of the primary color for gradient
    val lighterColor = primaryColor.copy(
        red = minOf(1f, primaryColor.red + 0.2f),
        green = minOf(1f, primaryColor.green + 0.2f),
        blue = minOf(1f, primaryColor.blue + 0.2f)
    )

    val darkerColor = primaryColor.copy(
        red = maxOf(0f, primaryColor.red - 0.2f),
        green = maxOf(0f, primaryColor.green - 0.2f),
        blue = maxOf(0f, primaryColor.blue - 0.2f)
    )

    return listOf(lighterColor, primaryColor, darkerColor)
}

/**
 * Parse color with alpha for shadow
 */
private fun parseColorWithAlpha(hexColor: String): Color {
    return parseHexColor(hexColor).copy(alpha = 0.6f)
}

/**
 * Parse hex color string to Compose Color
 */
private fun parseHexColor(hexColor: String): Color {
    val cleanHex = hexColor.removePrefix("#")
    return when (cleanHex.length) {
        6 -> {
            // RGB format
            val rgb = cleanHex.toULong(16)
            Color((0xFF000000UL or rgb).toLong())
        }
        8 -> {
            // ARGB format
            val argb = cleanHex.toULong(16)
            Color(argb.toLong())
        }
        else -> {
            // Invalid format, return default
            Color(0xFF6B73FF)
        }
    }
}

/**
 * Map test category to appropriate icon resource
 */
private fun mapCategoryToIcon(category: String): org.jetbrains.compose.resources.DrawableResource {
    return when (category.lowercase()) {
        "personality" -> Res.drawable.brain
        "cognition" -> Res.drawable.brain
        "social" -> Res.drawable.double_star
        "resilience" -> Res.drawable.film
        "creativity" -> Res.drawable.double_star
        "entertainment" -> Res.drawable.film
        "literature" -> Res.drawable.brain
        "arts" -> Res.drawable.double_star
        "gaming" -> Res.drawable.film
        else -> Res.drawable.brain // Default fallback
    }
}