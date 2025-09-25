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
import com.keak.aishou.screens.friends.FriendsViewModel
import com.keak.aishou.screens.allresults.TestResultViewModel
import com.keak.aishou.data.models.FriendInfo
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendToFriendBottomSheet(
    inviteLink: String?,
    soloResult: SoloResult?,
    testId: String?,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val shareHelper = remember { ShareHelperFactory.create() }
    val imageShareHelper = remember { ImageShareHelperFactory.create() }
    var showCopiedMessage by remember { mutableStateOf(false) }
    var showInstagramShareCard by remember { mutableStateOf(false) }
    var showFriendsModal by remember { mutableStateOf(false) }

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
                        // Send to Friends
                        ShareOptionButton(
                            emoji = "👥",
                            label = "Friends",
                            color = Color(0xFF9C27B0),
                            onClick = {
                                showFriendsModal = true
                            }
                        )

                        // Instagram Story Share (with visual)
//                        ShareOptionButton(
//                            emoji = "📷",
//                            label = "Instagram",
//                            color = Color(0xFFE4405F),
//                            onClick = {
//                                if (soloResult != null && inviteLink != null) {
//                                    showInstagramShareCard = true
//                                }
//                            }
//                        )

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

    // Friends List Modal
    if (showFriendsModal && testId != null) {
        ModalBottomSheet(
            onDismissRequest = { showFriendsModal = false },
            containerColor = Color.White
        ) {
            FriendsListModal(
                testId = testId,
                onSendToFriend = { friend ->
                    // This will trigger createInvite API with friendId
                    showFriendsModal = false
                },
                onDismiss = { showFriendsModal = false }
            )
        }
    }
}

@Composable
private fun FriendsListModal(
    testId: String,
    onSendToFriend: (FriendInfo) -> Unit,
    onDismiss: () -> Unit,
    friendsViewModel: FriendsViewModel = koinViewModel(),
    testResultViewModel: TestResultViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val shareHelper = remember { ShareHelperFactory.create() }

    // Real friends data from API
    val friends by friendsViewModel.friendsList.collectAsStateWithLifecycle()
    val isLoading by friendsViewModel.isLoading.collectAsStateWithLifecycle()
    val error by friendsViewModel.error.collectAsStateWithLifecycle()

    // Invite creation state
    var isCreatingInvite by remember { mutableStateOf(false) }
    var createdInviteLink by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "👥 Send to Friends",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Error handling
        error?.let { errorMessage ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 48.sp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Error loading friends",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } ?: run {
            // Loading state
            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Loading friends...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else if (friends.isEmpty()) {
                // Empty state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "👥",
                        fontSize = 48.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No friends yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Add friends to share your results",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Friends list
                friends.forEach { friend ->
                    NeoBrutalistCardViewWithFlexSize(
                        backgroundColor = Color.White,
                        borderColor = Color(0xFFE9ECEF),
                        borderWidth = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button) {
                                if (!isCreatingInvite) {
                                    isCreatingInvite = true
                                    testResultViewModel.createInviteForFriend(
                                        testId = testId,
                                        friendId = friend.userId,
                                        onSuccess = { inviteLink ->
                                            scope.launch {
                                                createdInviteLink = inviteLink
                                                val shareText = "Check out my personality test results! Take the test here: $inviteLink"
                                                shareHelper.shareText(shareText, "Send to ${friend.displayName ?: friend.userId}")
                                                isCreatingInvite = false
                                                onSendToFriend(friend)
                                            }
                                        },
                                        onError = { error ->
                                            println("Failed to create invite for friend: $error")
                                            isCreatingInvite = false
                                            // Still call share with generic message if invite creation fails
                                            scope.launch {
                                                val shareText = "Check out this personality test!"
                                                shareHelper.shareText(shareText, "Send to ${friend.displayName ?: friend.userId}")
                                                onSendToFriend(friend)
                                            }
                                        }
                                    )
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NeoBrutalistCardViewWithFlexSize(
                                backgroundColor = Color(0xFF9C27B0),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text(
                                    text = (friend.displayName ?: "U").first().toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = friend.displayName ?: "User ${friend.userId}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                val info = listOfNotNull(friend.mbtiType, friend.zodiacSign)
                                    .joinToString(" • ")
                                if (info.isNotBlank()) {
                                    Text(
                                        text = info,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                friend.tag?.let { tag ->
                                    Text(
                                        text = tag.lowercase().replaceFirstChar { it.uppercase() },
                                        fontSize = 10.sp,
                                        color = Color(0xFF9C27B0),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (isCreatingInvite) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF9C27B0),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "📤",
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Cancel",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

// Using FriendInfo from data models instead of local Friend class

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