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

data class ZodiacSign(
    val name: String,
    val symbol: String,
    val dates: String,
    val element: String
)

@Composable
fun ZodiacSelectionScreen(
    onZodiacSelected: (ZodiacSign) -> Unit
) {
    var selectedZodiac by remember { mutableStateOf<ZodiacSign?>(null) }

    val zodiacSigns = listOf(
        ZodiacSign("Aries", "♈", StringResources.getZodiacDates("Aries"), "Fire"),
        ZodiacSign("Taurus", "♉", StringResources.getZodiacDates("Taurus"), "Earth"),
        ZodiacSign("Gemini", "♊", StringResources.getZodiacDates("Gemini"), "Air"),
        ZodiacSign("Cancer", "♋", StringResources.getZodiacDates("Cancer"), "Water"),
        ZodiacSign("Leo", "♌", StringResources.getZodiacDates("Leo"), "Fire"),
        ZodiacSign("Virgo", "♍", StringResources.getZodiacDates("Virgo"), "Earth"),
        ZodiacSign("Libra", "♎", StringResources.getZodiacDates("Libra"), "Air"),
        ZodiacSign("Scorpio", "♏", StringResources.getZodiacDates("Scorpio"), "Water"),
        ZodiacSign("Sagittarius", "♐", StringResources.getZodiacDates("Sagittarius"), "Fire"),
        ZodiacSign("Capricorn", "♑", StringResources.getZodiacDates("Capricorn"), "Earth"),
        ZodiacSign("Aquarius", "♒", StringResources.getZodiacDates("Aquarius"), "Air"),
        ZodiacSign("Pisces", "♓", StringResources.getZodiacDates("Pisces"), "Water")
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
            text = StringResources.whatsYourZodiacSign(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = StringResources.selectZodiacToContinue(),
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Zodiac Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(zodiacSigns) { zodiac ->
                ZodiacCard(
                    zodiac = zodiac,
                    isSelected = selectedZodiac == zodiac,
                    onClick = { selectedZodiac = zodiac }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            backgroundColor = if (selectedZodiac != null) Color(0xFF4FFFB3) else Color.Gray,
            borderColor = Color.Black,
            shadowColor = Color.Black,
            borderWidth = 3.dp,
            shadowOffset = 4.dp,
            contentPadding = 0.dp,
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = {
                    selectedZodiac?.let { onZodiacSelected(it) }
                },
                enabled = selectedZodiac != null,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = StringResources.mbtiContinue(),
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
private fun ZodiacCard(
    zodiac: ZodiacSign,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> Color(0xFFFF66B2)
        zodiac.element == "Fire" -> Color(0xFFFF6B6B)
        zodiac.element == "Earth" -> Color(0xFF4ECDC4)
        zodiac.element == "Air" -> Color(0xFFFFE66D)
        zodiac.element == "Water" -> Color(0xFF74A0FF)
        else -> Color.White
    }

    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        backgroundColor = backgroundColor,
        borderColor = Color.Black,
        shadowColor = Color.Black,
        borderWidth = if (isSelected) 4.dp else 2.dp,
        shadowOffset = if (isSelected) 6.dp else 3.dp,
        contentPadding = 8.dp,
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
                    text = zodiac.symbol,
                    fontSize = 24.sp,
                    color = Color.Black
                )

                Text(
                    text = zodiac.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = zodiac.dates,
                    fontSize = 8.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}