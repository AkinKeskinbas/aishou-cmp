package com.keak.aishou.screens.onboarding

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.double_star
import aishou.composeapp.generated.resources.onboarding_third_lets_do_this
import aishou.composeapp.generated.resources.onboarding_third_matching_badge
import aishou.composeapp.generated.resources.onboarding_third_matching_description
import aishou.composeapp.generated.resources.onboarding_third_matching_title
import aishou.composeapp.generated.resources.onboarding_third_maybe_later
import aishou.composeapp.generated.resources.onboarding_third_mbti_badge
import aishou.composeapp.generated.resources.onboarding_third_mbti_description
import aishou.composeapp.generated.resources.onboarding_third_mbti_title
import aishou.composeapp.generated.resources.onboarding_third_question
import aishou.composeapp.generated.resources.onboarding_third_subtitle
import aishou.composeapp.generated.resources.onboarding_third_title
import aishou.composeapp.generated.resources.onboarding_third_zodiac_badge
import aishou.composeapp.generated.resources.onboarding_third_zodiac_description
import aishou.composeapp.generated.resources.onboarding_third_zodiac_title
import aishou.composeapp.generated.resources.personality_desc
import aishou.composeapp.generated.resources.profile_home
import aishou.composeapp.generated.resources.star
import aishou.composeapp.generated.resources.star_dec
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnBoardingScreenThird(
    router: Router
) {
    val screenWidth = screenSize().width
    val maxWidth = (screenWidth * 0.9f).dp
    var readyToChange by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFDD835), // Yellow-300
                        Color(0xFFFFB74D), // Orange-300
                        Color(0xFFF48FB1)  // Pink-300
                    ),
                    radius = 800f
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.onboarding_third_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFBC02D), // Yellow-500 (simulating gradient with single color)
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.onboarding_third_subtitle),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // How it works cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // MBTI Card
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.width(maxWidth).rotate(-1f),
                backgroundColor = Color.White,
                borderColor = Color.Black,
                cornerRadius = 24.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NeoBrutalistCircle(
                        modifier = Modifier.size(64.dp),
                        backgroundBrush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFFEB3B), Color(0xFFFFEB3B))
                        ),
                        borderColor = Color.Black
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.star),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.onboarding_third_mbti_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.onboarding_third_mbti_description),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        NeoBrutalistCardViewWithFlexSize(
                            backgroundColor = Color(0xFFFFF9C4), // Yellow-100
                            borderColor = Color.Black,
                            cornerRadius = 20.dp
                        ) {
                            Text(
                                text = stringResource(Res.string.onboarding_third_mbti_badge),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Zodiac Match Card
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.width(maxWidth).rotate(1f),
                backgroundColor = Color.White,
                borderColor = Color.Black,
                cornerRadius = 24.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NeoBrutalistCircle(
                        modifier = Modifier.size(64.dp),
                        backgroundBrush = Brush.linearGradient(
                            colors = listOf(Color(0xFFE91E63), Color(0xFFE91E63))
                        ),
                        borderColor = Color.Black
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.double_star),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.onboarding_third_zodiac_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.onboarding_third_zodiac_description),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        NeoBrutalistCardViewWithFlexSize(
                            backgroundColor = Color(0xFFFCE4EC), // Pink-100
                            borderColor = Color.Black,
                            cornerRadius = 20.dp
                        ) {
                            Text(
                                text = stringResource(Res.string.onboarding_third_zodiac_badge),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Friend Matching Card
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.width(maxWidth).rotate(-1f),
                backgroundColor = Color.White,
                borderColor = Color.Black,
                cornerRadius = 24.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NeoBrutalistCircle(
                        modifier = Modifier.size(64.dp),
                        backgroundBrush = Brush.linearGradient(
                            colors = listOf(Color(0xFF2196F3), Color(0xFF2196F3))
                        ),
                        borderColor = Color.Black
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.profile_home),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.onboarding_third_matching_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.onboarding_third_matching_description),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        NeoBrutalistCardViewWithFlexSize(
                            backgroundColor = Color(0xFFE3F2FD), // Blue-100
                            borderColor = Color.Black,
                            cornerRadius = 20.dp
                        ) {
                            Text(
                                text = stringResource(Res.string.onboarding_third_matching_badge),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Interactive Question
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.onboarding_third_question),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier
                        .width(maxWidth)
                        .clickable(role = Role.Button) {
                            readyToChange = true
                            router.goToOnBoardingFourth()
                        },
                    backgroundColor = if (readyToChange == true) Color(0xFF4CAF50) else Color.White,
                    borderColor = Color.Black,
                    cornerRadius = 16.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 24.sp,
                            color = if (readyToChange == true) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(Res.string.onboarding_third_lets_do_this),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (readyToChange == true) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}