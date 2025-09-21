package com.keak.aishou.screens.onboarding

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.onboarding_fourth_accurate
import aishou.composeapp.generated.resources.onboarding_fourth_always
import aishou.composeapp.generated.resources.onboarding_fourth_find_friends
import aishou.composeapp.generated.resources.onboarding_fourth_footer
import aishou.composeapp.generated.resources.onboarding_fourth_free
import aishou.composeapp.generated.resources.onboarding_fourth_perfect_ready
import aishou.composeapp.generated.resources.onboarding_fourth_perfect_subtitle
import aishou.composeapp.generated.resources.onboarding_fourth_perfect_title
import aishou.composeapp.generated.resources.onboarding_fourth_quick_test
import aishou.composeapp.generated.resources.onboarding_fourth_take_quiz
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.NeoBrutalistCircle
import com.keak.aishou.navigation.Router
import com.keak.aishou.screenSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnBoardingScreenFourth(
    router: Router
) {
    val screenWidth = screenSize().width
    val maxWidth = (screenWidth * 0.9f).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFAB47BC), // Purple-400
                        Color(0xFFEC407A), // Pink-400
                        Color(0xFF42A5F5)  // Blue-400
                    ),
                    radius = 800f
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Success Message Card
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier.width(maxWidth).rotate(-1f),
            backgroundColor = Color.White,
            borderColor = Color.Black,
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Heart Icon Circle
                NeoBrutalistCircle(
                    modifier = Modifier.size(96.dp),
                    backgroundBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4CAF50), // Green-400
                            Color(0xFF2196F3)  // Blue-400
                        )
                    ),
                    borderColor = Color.Black
                ) {
                    Text(
                        text = "💝",
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = stringResource(Res.string.onboarding_fourth_perfect_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4CAF50), // Green-500 (simulating gradient)
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.onboarding_fourth_perfect_ready),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.onboarding_fourth_perfect_subtitle),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Stats Grid (2 items)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 3min stat
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.weight(1f).height(100.dp),
                        backgroundColor = Color(0xFFE8F5E8), // Green-100
                        borderColor = Color.Black,
                        cornerRadius = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "3min",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4CAF50) // Green-600
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.onboarding_fourth_quick_test),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // 97% stat
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.weight(1f).height(100.dp),
                        backgroundColor = Color(0xFFF3E5F5), // Purple-100
                        borderColor = Color.Black,
                        cornerRadius = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "97%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF9C27B0) // Purple-600
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.onboarding_fourth_accurate),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Free stat (centered below)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.weight(1f).height(100.dp),
                        backgroundColor = Color(0xFFE3F2FD), // Blue-100
                        borderColor = Color.Black,
                        cornerRadius = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.onboarding_fourth_free),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2196F3) // Blue-600
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.onboarding_fourth_always),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // CTA Button
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier
                .width(maxWidth)
                .clickable(role = Role.Button) {
                    router.goToZodiacSelection()
                },
            backgroundColor = Color(0xFF4CAF50), // Green gradient simulation
            borderColor = Color.Black,
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.onboarding_fourth_find_friends),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "→",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer text
        Text(
            text = stringResource(Res.string.onboarding_fourth_footer),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}