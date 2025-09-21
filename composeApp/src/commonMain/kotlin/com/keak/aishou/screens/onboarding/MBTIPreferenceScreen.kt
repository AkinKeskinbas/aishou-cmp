package com.keak.aishou.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

enum class MBTIPreferenceType {
    MANUAL_ENTRY,
    TAKE_TEST
}

data class MBTIType(
    val code: String,
    val name: String,
    val description: String
)

@Composable
fun MBTIPreferenceScreen(
    onPreferenceSelected: (MBTIPreferenceType, MBTIType?) -> Unit
) {
    var selectedPreference by remember { mutableStateOf<MBTIPreferenceType?>(null) }
    var selectedMBTI by remember { mutableStateOf<MBTIType?>(null) }

    val mbtiTypes = remember {
        listOf(
            MBTIType("INTJ", "Architect", "Imaginative and strategic thinkers"),
            MBTIType("INTP", "Thinker", "Innovative inventors with unquenchable thirst for knowledge"),
            MBTIType("ENTJ", "Commander", "Bold, imaginative and strong-willed leaders"),
            MBTIType("ENTP", "Debater", "Smart and curious thinkers who love intellectual challenges"),
            MBTIType("INFJ", "Advocate", "Creative and insightful, inspired and independent"),
            MBTIType("INFP", "Mediator", "Poetic, kind and altruistic people"),
            MBTIType("ENFJ", "Protagonist", "Charismatic and inspiring leaders"),
            MBTIType("ENFP", "Campaigner", "Enthusiastic, creative and sociable free spirits"),
            MBTIType("ISTJ", "Logistician", "Practical and fact-minded, reliable"),
            MBTIType("ISFJ", "Protector", "Warm-hearted and dedicated"),
            MBTIType("ESTJ", "Executive", "Excellent administrators, unsurpassed at managing"),
            MBTIType("ESFJ", "Consul", "Extraordinarily caring, social and popular"),
            MBTIType("ISTP", "Virtuoso", "Bold and practical experimenters"),
            MBTIType("ISFP", "Adventurer", "Flexible and charming artists"),
            MBTIType("ESTP", "Entrepreneur", "Smart, energetic and perceptive"),
            MBTIType("ESFP", "Entertainer", "Spontaneous, energetic and enthusiastic")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Your MBTI Personality",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please choose how to determine your MBTI type",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preference Selection
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Take Test Option
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                backgroundColor = if (selectedPreference == MBTIPreferenceType.TAKE_TEST)
                    Color(0xFF4FFFB3) else Color(0xFFFFE66D),
                borderColor = Color.Black,
                shadowColor = Color.Black,
                borderWidth = if (selectedPreference == MBTIPreferenceType.TAKE_TEST) 4.dp else 3.dp,
                shadowOffset = 6.dp,
                contentPadding = 16.dp,
                contentAlignment = Alignment.Center,
                showBadge = true,
                badgeText = "RECOMMENDED!",
                badgeBg = Color(0xFFF87171),
                badgeTextColor = Color.White
            ) {
                TextButton(
                    onClick = {
                        selectedPreference = MBTIPreferenceType.TAKE_TEST
                        selectedMBTI = null
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🧠",
                            fontSize = 32.sp
                        )

                        Text(
                            text = "Take a Full Test",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "70 questions • 15-20 minutes",
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Comprehensive personality assessment",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Manual Entry Option
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                backgroundColor = if (selectedPreference == MBTIPreferenceType.MANUAL_ENTRY)
                    Color(0xFF4FFFB3) else Color(0xFF74A0FF),
                borderColor = Color.Black,
                shadowColor = Color.Black,
                borderWidth = if (selectedPreference == MBTIPreferenceType.MANUAL_ENTRY) 4.dp else 3.dp,
                shadowOffset = 6.dp,
                contentPadding = 16.dp,
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = {
                        selectedPreference = MBTIPreferenceType.MANUAL_ENTRY
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✍️",
                            fontSize = 32.sp
                        )

                        Text(
                            text = "I Know My Type",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "Select from the list below",
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // MBTI Type Selection (only show if manual entry is selected)
        if (selectedPreference == MBTIPreferenceType.MANUAL_ENTRY) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Your MBTI Type",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(mbtiTypes) { mbti ->
                    MBTITypeCard(
                        mbtiType = mbti,
                        isSelected = selectedMBTI == mbti,
                        onClick = { selectedMBTI = mbti }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        val canContinue = selectedPreference != null &&
            (selectedPreference == MBTIPreferenceType.TAKE_TEST || selectedMBTI != null)

        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            backgroundColor = if (canContinue) Color(0xFF4FFFB3) else Color.Gray,
            borderColor = Color.Black,
            shadowColor = Color.Black,
            borderWidth = 3.dp,
            shadowOffset = 4.dp,
            contentPadding = 0.dp,
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = {
                    selectedPreference?.let { preference ->
                        onPreferenceSelected(preference, selectedMBTI)
                    }
                },
                enabled = canContinue,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = when (selectedPreference) {
                        MBTIPreferenceType.TAKE_TEST -> "Start Full Test"
                        MBTIPreferenceType.MANUAL_ENTRY -> "Continue"
                        null -> "Continue"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MBTITypeCard(
    mbtiType: MBTIType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        backgroundColor = if (isSelected) Color(0xFFFF66B2) else Color.White,
        borderColor = Color.Black,
        shadowColor = Color.Black,
        borderWidth = if (isSelected) 3.dp else 2.dp,
        shadowOffset = if (isSelected) 4.dp else 2.dp,
        contentPadding = 12.dp,
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mbtiType.code,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )

                Text(
                    text = mbtiType.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = mbtiType.description,
                    fontSize = 8.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}