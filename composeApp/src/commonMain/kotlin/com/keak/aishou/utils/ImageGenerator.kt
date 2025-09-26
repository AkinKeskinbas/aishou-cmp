package com.keak.aishou.utils

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.app_icon
import aishou.composeapp.generated.resources.cat
import aishou.composeapp.generated.resources.match_results
import aishou.composeapp.generated.resources.match_summary
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.data.api.CompatibilityResult
import com.keak.aishou.data.api.TestResultResponse
import com.keak.aishou.screens.quicktestscreen.BrutalHeader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShareableMatchResultCard(
    testResult: TestResultResponse,
    userDisplayName: String,
    compatibilityResult: CompatibilityResult
) {
    Box(
        modifier = Modifier
            .width(1080.dp)
            .height(1920.dp)
            .background(Color(0xFFC8FBAD)),
        contentAlignment = Alignment.Center
    ) {
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp), // Remove any padding
            backgroundColor = Color(0xFFC8FBAD)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Internal content padding
                verticalArrangement = Arrangement.SpaceBetween
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.weight(1f).rotate(-2f),
                    backgroundColor = Color(0xFFFDFEA5),
                ) {
                    Text(
                        text = compatibilityResult.myInfo?.mbtiType.orEmpty() + " - " + compatibilityResult.myInfo?.zodiacSign,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.width(8.dp))
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.weight(1f).rotate(2f),
                    backgroundColor = Color(0xFFFDFEA5),
                ) {
                    Text(
                        text = compatibilityResult.friendInfo?.mbtiType.orEmpty() + " - " + compatibilityResult.friendInfo?.zodiacSign,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(Res.string.match_results),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black
                )
            }
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                backgroundColor = Color(0xFFB3CDFF)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = compatibilityResult.score.toString() + "%",
                        fontWeight = FontWeight.Black,
                        fontSize = 46.sp
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BrutalHeader(
                    modifier = Modifier,
                    headerText = compatibilityResult.chemistry.orEmpty(),
                    textSize = 42
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f) // Take half of remaining space instead of all
                    .background(
                        Color(0xFFADFF86),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        4.dp,
                        Color.Black,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp), // Larger padding
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Larger spacing
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.cat),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = stringResource(Res.string.match_summary),
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = compatibilityResult.summary.orEmpty(),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        lineHeight = 50.sp // Much larger line height for 42sp text
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "Aishou App",
                    fontWeight = FontWeight.Black,
                    fontSize = 50.sp
                )
            }
        }
        }
    }
}

// Alternative approach: Generate bitmap programmatically
expect fun generateShareableBitmap(
    testResult: TestResultResponse,
    userDisplayName: String
): ImageBitmap?