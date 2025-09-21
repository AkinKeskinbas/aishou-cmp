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

    val zodiacSigns = remember {
        listOf(
            ZodiacSign("Aries", "♈", "Mar 21 - Apr 19", "Fire"),
            ZodiacSign("Taurus", "♉", "Apr 20 - May 20", "Earth"),
            ZodiacSign("Gemini", "♊", "May 21 - Jun 20", "Air"),
            ZodiacSign("Cancer", "♋", "Jun 21 - Jul 22", "Water"),
            ZodiacSign("Leo", "♌", "Jul 23 - Aug 22", "Fire"),
            ZodiacSign("Virgo", "♍", "Aug 23 - Sep 22", "Earth"),
            ZodiacSign("Libra", "♎", "Sep 23 - Oct 22", "Air"),
            ZodiacSign("Scorpio", "♏", "Oct 23 - Nov 21", "Water"),
            ZodiacSign("Sagittarius", "♐", "Nov 22 - Dec 21", "Fire"),
            ZodiacSign("Capricorn", "♑", "Dec 22 - Jan 19", "Earth"),
            ZodiacSign("Aquarius", "♒", "Jan 20 - Feb 18", "Air"),
            ZodiacSign("Pisces", "♓", "Feb 19 - Mar 20", "Water")
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
            text = "What's Your Zodiac Sign?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please select your zodiac sign to continue",
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
                    text = "Continue",
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