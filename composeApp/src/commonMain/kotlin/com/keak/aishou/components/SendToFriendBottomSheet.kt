package com.keak.aishou.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.utils.ShareHelperFactory
import com.keak.aishou.utils.ImageShareHelperFactory
import com.keak.aishou.data.api.SoloResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendToFriendBottomSheet(
    inviteLink: String?,
    soloResult: SoloResult?,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val shareHelper = remember { ShareHelperFactory.create() }
    val imageShareHelper = remember { ImageShareHelperFactory.create() }
    var showCopiedMessage by remember { mutableStateOf(false) }
    var showInstagramShareCard by remember { mutableStateOf(false) }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray, RoundedCornerShape(2.dp))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "🎉 SHARE YOUR RESULTS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Invite your friends to take the same test!",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Invite Link Display
                if (inviteLink != null) {
                    NeoBrutalistCardViewWithFlexSize(
                        backgroundColor = Color(0xFFF8F9FA),
                        borderColor = Color(0xFFE9ECEF),
                        borderWidth = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "📎 Invite Link",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = inviteLink,
                                fontSize = 12.sp,
                                color = Color.Black,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Copy Link Button
                    NeoBrutalistCardViewWithFlexSize(
                        backgroundColor = null,
                        backgroundBrush = if (showCopiedMessage) {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF4CAF50), Color(0xFF45A049))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button) {
                                scope.launch {
                                    shareHelper.copyToClipboard(inviteLink, "Invite Link")
                                    showCopiedMessage = true
                                    kotlinx.coroutines.delay(2000)
                                    showCopiedMessage = false
                                }
                            },
                        shadowColor = Color(0xFF1565C0),
                        shadowOffset = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (showCopiedMessage) "✅" else "📋",
                                fontSize = 20.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (showCopiedMessage) "COPIED!" else "COPY LINK",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Share Options
                    Text(
                        text = "Or share via:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Instagram Story Share (with visual)
                        ShareOptionButton(
                            emoji = "📷",
                            label = "Instagram",
                            color = Color(0xFFE4405F),
                            onClick = {
                                if (soloResult != null && inviteLink != null) {
                                    showInstagramShareCard = true
                                }
                            }
                        )

                        // LINE Share
                        ShareOptionButton(
                            emoji = "💬",
                            label = "LINE",
                            color = Color(0xFF00B900),
                            onClick = {
                                scope.launch {
                                    val shareText = "Check out my personality test results! Take the test here: $inviteLink"
                                    shareHelper.shareToLine(shareText)
                                }
                            }
                        )

                        // More Options
                        ShareOptionButton(
                            emoji = "📤",
                            label = "More",
                            color = Color(0xFF6C757D),
                            onClick = {
                                scope.launch {
                                    val shareText = "Check out my personality test results! Take the test here: $inviteLink"
                                    shareHelper.shareText(shareText, "Share Invite")
                                }
                            }
                        )
                    }
                } else {
                    // Loading state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2196F3),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Creating invite link...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Close Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Instagram Share Card Dialog
    if (showInstagramShareCard && soloResult != null && inviteLink != null) {
        ModalBottomSheet(
            onDismissRequest = { showInstagramShareCard = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📸 SHARE TO INSTAGRAM",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )

                Spacer(Modifier.height(16.dp))

                // Instagram Story Card Preview
                InstagramStoryShareCard(
                    soloResult = soloResult,
                    modifier = Modifier.scale(0.6f)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Share as Story Button
                    NeoBrutalistCardViewWithFlexSize(
                        backgroundColor = Color(0xFFE4405F),
                        shadowColor = Color(0xFFE4405F).copy(alpha = 0.3f),
                        shadowOffset = 4.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(role = Role.Button) {
                                scope.launch {
                                    // TODO: Implement actual image capture and share
                                    // For now, fallback to text share
                                    val shareText = "🎯 I scored ${soloResult.totalScore} points on my personality test! Take the test here: $inviteLink"
                                    shareHelper.shareToInstagram(shareText)
                                    showInstagramShareCard = false
                                    onDismiss()
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📱",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Share Story",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    // Share as Post Button
                    NeoBrutalistCardViewWithFlexSize(
                        backgroundColor = Color(0xFF6C757D),
                        shadowColor = Color(0xFF6C757D).copy(alpha = 0.3f),
                        shadowOffset = 4.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(role = Role.Button) {
                                scope.launch {
                                    // TODO: Implement actual image capture and share
                                    // For now, fallback to text share
                                    val shareText = "🎯 I scored ${soloResult.totalScore} points on my personality test! Take the test here: $inviteLink"
                                    shareHelper.shareText(shareText, "Share Result")
                                    showInstagramShareCard = false
                                    onDismiss()
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📤",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Share Post",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = { showInstagramShareCard = false }
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ShareOptionButton(
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(role = Role.Button) { onClick() }
    ) {
        NeoBrutalistCardViewWithFlexSize(
            backgroundColor = color,
            shadowColor = color.copy(alpha = 0.3f),
            shadowOffset = 4.dp,
            borderWidth = 2.dp,
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}