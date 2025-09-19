package com.keak.aishou.screens.onboarding

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.onboarding_are_you_bored
import aishou.composeapp.generated.resources.onboarding_real_talk
import aishou.composeapp.generated.resources.onboarding_tired_of_fake_friend
import aishou.composeapp.generated.resources.onboarding_yes
import aishou.composeapp.generated.resources.onboarding_you_know_the_feelings
import aishou.composeapp.generated.resources.sad
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import com.keak.aishou.components.BrokenTVText
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.NeoBrutalistCircle
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(
    router: Router
) {
    val screenWidth = screenSize().width
    val desiredWidth = screenWidth / 1.15
    Column(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D1421), // Deep dark blue-black
                    Color(0xFF1A1A2E), // Dark purple-black
                    Color(0xFF16213E), // Midnight blue
                    Color(0xFF0F0F23), // Almost black with purple tint
                    Color(0xFF000000)  // Pure black at bottom
                )
            )
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier.width(desiredWidth.dp).padding(top = 32.dp).rotate(-2f),
            backgroundColor = Color(0xFFF5F5F5), // Slightly off-white, more somber
            borderColor = Color(0xFF424242), // Dark gray border
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeoBrutalistCircle(
                    modifier = Modifier.size(80.dp),
                    backgroundBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF424242), // Dark gray
                            Color(0xFF616161), // Medium gray
                        )
                    )
                ) {
                    Image(
                        painter = painterResource(Res.drawable.sad),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF212121)) // Very dark gray, almost black
                    )
                }
                Spacer(Modifier.height(16.dp))
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.padding(horizontal = 8.dp), // Remove fixed width, add padding
                    backgroundColor = Color(0xFF8B0000), // Dark red, more ominous
                ) {
                    Text(
                        text = stringResource(Res.string.onboarding_real_talk),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        maxLines = 1, // Force single line
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.onboarding_tired_of_fake_friend),
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,

                    )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.onboarding_you_know_the_feelings),
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(0.dp))

            }

        }
        Spacer(Modifier.weight(1f))
        BrokenTVText(
            text = stringResource(Res.string.onboarding_are_you_bored),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.weight(1f))
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier.width(desiredWidth.dp).clickable(role = Role.Button) {
                router.goToOnBoardingSecond()
            },
            backgroundColor = Color(0xFF1C1C1C), // Dark gray/black
            borderColor = Color(0xFF424242), // Medium dark gray border
            cornerRadius = 16.dp
        ) {
            Text(
                text = stringResource(Res.string.onboarding_yes),
                color = Color.White, // White text on dark background
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
        Spacer(Modifier.height(22.dp))
    }
}

