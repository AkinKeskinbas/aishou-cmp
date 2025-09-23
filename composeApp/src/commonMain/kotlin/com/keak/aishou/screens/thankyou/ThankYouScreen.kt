package com.keak.aishou.screens.thankyou

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.thank_you_title
import aishou.composeapp.generated.resources.thank_you_message_normal
import aishou.composeapp.generated.resources.thank_you_message_invite
import aishou.composeapp.generated.resources.thank_you_go_home
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import org.jetbrains.compose.resources.stringResource

@Composable
fun ThankYouScreen(
    router: Router,
    isFromInvite: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCEE978))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main Thank You Card
        NeoBrutalistCardViewWithFlexSize(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Success Icon
                Text(
                    text = "🎉",
                    fontSize = 64.sp
                )

                // Title
                Text(
                    text = stringResource(Res.string.thank_you_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = if (isFromInvite) {
                        stringResource(Res.string.thank_you_message_invite)
                    } else {
                        stringResource(Res.string.thank_you_message_normal)
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Go Home Button
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clickable(role = Role.Button) {
                            router.goToHome()
                        },
                    backgroundColor = Color(0xFF10B981)
                ) {
                    Text(
                        text = stringResource(Res.string.thank_you_go_home),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun NeoBrutalistCard(
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 4.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Shadow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .offset(y = (-3).dp)
                .background(
                    color = borderColor,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
        )

        content()
    }
}

@Composable
private fun NeoBrutalistButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color.Black,
    textColor: Color = Color.White
) {
    Box(
        modifier = Modifier
            .clickable(role = Role.Button) { onClick() }
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = textColor
        )
    }
}