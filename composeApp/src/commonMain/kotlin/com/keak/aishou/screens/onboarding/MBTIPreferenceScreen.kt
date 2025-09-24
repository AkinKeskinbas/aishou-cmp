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
import com.keak.aishou.utils.StringResources

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

    val mbtiTypes = listOf(
        MBTIType("INTJ", StringResources.getMbtiTypeName("INTJ"), StringResources.getMbtiTypeDescription("INTJ")),
        MBTIType("INTP", StringResources.getMbtiTypeName("INTP"), StringResources.getMbtiTypeDescription("INTP")),
        MBTIType("ENTJ", StringResources.getMbtiTypeName("ENTJ"), StringResources.getMbtiTypeDescription("ENTJ")),
        MBTIType("ENTP", StringResources.getMbtiTypeName("ENTP"), StringResources.getMbtiTypeDescription("ENTP")),
        MBTIType("INFJ", StringResources.getMbtiTypeName("INFJ"), StringResources.getMbtiTypeDescription("INFJ")),
        MBTIType("INFP", StringResources.getMbtiTypeName("INFP"), StringResources.getMbtiTypeDescription("INFP")),
        MBTIType("ENFJ", StringResources.getMbtiTypeName("ENFJ"), StringResources.getMbtiTypeDescription("ENFJ")),
        MBTIType("ENFP", StringResources.getMbtiTypeName("ENFP"), StringResources.getMbtiTypeDescription("ENFP")),
        MBTIType("ISTJ", StringResources.getMbtiTypeName("ISTJ"), StringResources.getMbtiTypeDescription("ISTJ")),
        MBTIType("ISFJ", StringResources.getMbtiTypeName("ISFJ"), StringResources.getMbtiTypeDescription("ISFJ")),
        MBTIType("ESTJ", StringResources.getMbtiTypeName("ESTJ"), StringResources.getMbtiTypeDescription("ESTJ")),
        MBTIType("ESFJ", StringResources.getMbtiTypeName("ESFJ"), StringResources.getMbtiTypeDescription("ESFJ")),
        MBTIType("ISTP", StringResources.getMbtiTypeName("ISTP"), StringResources.getMbtiTypeDescription("ISTP")),
        MBTIType("ISFP", StringResources.getMbtiTypeName("ISFP"), StringResources.getMbtiTypeDescription("ISFP")),
        MBTIType("ESTP", StringResources.getMbtiTypeName("ESTP"), StringResources.getMbtiTypeDescription("ESTP")),
        MBTIType("ESFP", StringResources.getMbtiTypeName("ESFP"), StringResources.getMbtiTypeDescription("ESFP"))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = StringResources.mbtiYourPersonality(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

//        Text(
//            text = "Please choose how to determine your MBTI type",
//            fontSize = 16.sp,
//            color = Color.Gray,
//            textAlign = TextAlign.Center
//        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preference Selection
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Take Test Option
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                backgroundColor = if (selectedPreference == MBTIPreferenceType.TAKE_TEST)
                    Color(0xFF4FFFB3) else Color(0xFFFFE66D),
                borderColor = Color.Black,
                shadowColor = Color.Black,
                borderWidth = if (selectedPreference == MBTIPreferenceType.TAKE_TEST) 4.dp else 3.dp,
                shadowOffset = 6.dp,
                contentPadding = 8.dp,
                contentAlignment = Alignment.Center,
                showBadge = true,
                badgeText = StringResources.mbtiRecommended(),
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
                            fontSize = 20.sp
                        )

                        Text(
                            text = StringResources.mbtiTakeFullTest(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = StringResources.mbtiTestDuration(),
                            fontSize = 11.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = StringResources.mbtiComprehensiveAssessment(),
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
                    .height(100.dp),
                backgroundColor = if (selectedPreference == MBTIPreferenceType.MANUAL_ENTRY)
                    Color(0xFF4FFFB3) else Color(0xFF74A0FF),
                borderColor = Color.Black,
                shadowColor = Color.Black,
                borderWidth = if (selectedPreference == MBTIPreferenceType.MANUAL_ENTRY) 4.dp else 3.dp,
                shadowOffset = 6.dp,
                contentPadding = 8.dp,
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
                            fontSize = 20.sp
                        )

                        Text(
                            text = StringResources.mbtiKnowMyType(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = StringResources.mbtiSelectFromList(),
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
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = StringResources.mbtiSelectYourType(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                        MBTIPreferenceType.TAKE_TEST -> StringResources.mbtiStartFullTest()
                        MBTIPreferenceType.MANUAL_ENTRY -> StringResources.mbtiContinue()
                        null -> StringResources.mbtiContinue()
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )

                Text(
                    text = mbtiType.name,
                    fontSize = 11.sp,
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