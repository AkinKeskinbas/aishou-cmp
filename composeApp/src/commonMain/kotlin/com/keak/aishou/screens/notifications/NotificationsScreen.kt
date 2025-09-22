package com.keak.aishou.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.data.models.RequestWithSenderInfo
import com.keak.aishou.data.models.FriendTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsScreen(
    router: Router,
    highlightSenderId: String? = null,
    highlightSenderName: String? = null,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val friendRequests by viewModel.friendRequests.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val respondingToRequest by viewModel.respondingToRequest.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(brush = BackGroundBrush.homNeoBrush)
            .padding(16.dp)
    ) {
        // Custom Header
        NeobrutalistHeader(
            title = "🔔 Notifications",
            onBackClick = { router.goBack() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Friend Requests Section
        NeobrutalistSectionHeader(
            text = "Friend Requests (${friendRequests.size})"
        )

        // Error message
        error?.let { errorMessage ->
            NeobrutalistErrorCard(
                message = errorMessage,
                onDismiss = { viewModel.clearError() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Friend Requests List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                item {
                    NeobrutalistLoadingCard()
                }
            } else if (friendRequests.isEmpty()) {
                item {
                    NeobrutalistEmptyState(
                        emoji = "😴",
                        title = "All caught up!",
                        subtitle = "No new notifications"
                    )
                }
            } else {
                items(friendRequests) { requestWithSender ->
                    val isHighlighted = highlightSenderId == requestWithSender.sender.userId
                    NeobrutalistFriendRequestCard(
                        requestWithSender = requestWithSender,
                        isResponding = respondingToRequest == requestWithSender.request.getRequestId(),
                        isHighlighted = isHighlighted,
                        onAccept = {
                            val requestId = requestWithSender.request.getRequestId()
                            if (!requestId.isNullOrBlank()) {
                                viewModel.acceptFriendRequest(
                                    requestId = requestId,
                                    tag = FriendTag.FRIEND
                                )
                            } else {
                                println("NotificationsScreen: Cannot accept - request ID is null!")
                            }
                        },
                        onReject = {
                            val requestId = requestWithSender.request.getRequestId()
                            if (!requestId.isNullOrBlank()) {
                                viewModel.rejectFriendRequest(
                                    requestId = requestId
                                )
                            } else {
                                println("NotificationsScreen: Cannot reject - request ID is null!")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NeobrutalistHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color(0xFFFF6B6B),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(role = Role.Button) { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun NeobrutalistSectionHeader(text: String) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFFFEB3B),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
    }
}

@Composable
private fun NeobrutalistFriendRequestCard(
    requestWithSender: RequestWithSenderInfo,
    isResponding: Boolean,
    isHighlighted: Boolean = false,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(){
                onAccept.invoke()
            }
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(
                color = if (isHighlighted) Color(0xFFFFF9C4) else Color.White, // Light yellow for highlight
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = Color.Black
                        )
                        .background(
                            color = Color(0xFF9C27B0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (requestWithSender.sender.displayName ?: "U").first().toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = requestWithSender.sender.displayName ?: "User ${requestWithSender.sender.userId}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    val info = listOfNotNull(requestWithSender.sender.mbtiType, requestWithSender.sender.zodiacSign)
                        .joinToString(" • ")
                    if (info.isNotBlank()) {
                        Text(
                            text = info,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF666666)
                        )
                    }
                    Text(
                        text = "wants to be friends 👋",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF888888)
                    )
                    requestWithSender.request.message?.let { message ->
                        Text(
                            text = "\"$message\"",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9C27B0),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeobrutalistActionButton(
                    text = if (isResponding) "..." else "✓",
                    backgroundColor = Color(0xFF4CAF50),
                    shadowColor = Color(0xFF2E7D32),
                    enabled = !isResponding,
                    onClick = onAccept
                )

                NeobrutalistActionButton(
                    text = if (isResponding) "..." else "×",
                    backgroundColor = Color(0xFFFF5722),
                    shadowColor = Color(0xFFD84315),
                    enabled = !isResponding,
                    onClick = onReject
                )
            }
        }
    }
}

@Composable
private fun NeobrutalistActionButton(
    text: String,
    backgroundColor: Color,
    shadowColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .background(
                color = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                role = Role.Button,
                enabled = enabled
            ) {
                if (enabled) onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
private fun NeobrutalistEmptyState(
    emoji: String,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFF0F0F0),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = emoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun NeobrutalistErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                modifier = Modifier.weight(1f)
            )
            NeobrutalistActionButton(
                text = "×",
                backgroundColor = Color(0xFFFF5722),
                shadowColor = Color(0xFFD84315),
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun NeobrutalistLoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFF0F0F0),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color(0xFF9C27B0)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading notifications...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

