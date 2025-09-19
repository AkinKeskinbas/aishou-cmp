package com.keak.aishou.screens.splashscreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.star
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.NeoBrutalistCircle
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    router: Router,
    viewModel: SplashViewModel
) {
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val screenWidth = screenSize().width
    val cardWidth = (screenWidth * 0.7f).dp

    // Animation for floating effect
    val infiniteTransition = rememberInfiniteTransition()
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Rotation animation for the star icon
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Restart
        )
    )

    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is SplashNavigationEvent.NavigateToOnboarding -> {
                router.goToOnBoarding()
                viewModel.onNavigationHandled()
            }
            is SplashNavigationEvent.NavigateToHome -> {
                router.goToHome()
                viewModel.onNavigationHandled()
            }
            null -> { /* Do nothing */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4CAF50), // Green
                        Color(0xFF2196F3), // Blue
                        Color(0xFF9C27B0)  // Purple
                    ),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main logo card
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier
                    .width(cardWidth)
                    .rotate(-2f)
                    .scale(1f + floatingOffset * 0.01f), // Subtle scaling with float
                backgroundColor = Color.White,
                borderColor = Color.Black,
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App icon
                    NeoBrutalistCircle(
                        modifier = Modifier.size(80.dp),
                        backgroundBrush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF2196F3)
                            )
                        ),
                        borderColor = Color.Black
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.star),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .rotate(rotation),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // App name
                    Text(
                        text = "Aishou",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tagline
                    Text(
                        text = "Find Your Real Tribe",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Loading indicator
            NeoBrutalistCardViewWithFlexSize(
                backgroundColor = Color.Black,
                borderColor = Color.White,
                cornerRadius = 16.dp
            ) {
                Text(
                    text = "Loading...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}