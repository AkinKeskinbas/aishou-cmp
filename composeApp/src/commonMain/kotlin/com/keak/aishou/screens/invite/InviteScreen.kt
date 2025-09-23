package com.keak.aishou.screens.invite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keak.aishou.misc.AppStatu
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InviteScreen(
    inviteId: String,
    senderId: String,
    testId: String,
    testTitle: String,
    router: Router,
    viewModel: InviteViewModel = koinViewModel()
) {
    val senderInfo by viewModel.senderInfo.collectAsStateWithLifecycle()
    val testInfo by viewModel.testInfo.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val userPremiumStatus by viewModel.userPremiumStatus.collectAsStateWithLifecycle()
    val isAcceptingInvite by viewModel.isAcceptingInvite.collectAsStateWithLifecycle()
    val inviteAccepted by viewModel.inviteAccepted.collectAsStateWithLifecycle()

    // Load data when screen starts
    LaunchedEffect(senderId, testId) {
        viewModel.loadInviteData(senderId, testId)
    }

    // Navigate to test when invite is accepted
    LaunchedEffect(inviteAccepted) {
        if (inviteAccepted) {
            router.goToQuizScreenWithSender(testId, senderId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(Color(0xFF49DC9C))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        NeobrutalistHeader(
            title = "🎉 Test Invitation",
            onBackClick = { router.goBack() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            // Loading state
            LoadingCard()
        } else if (error != null) {
            // Error state
            ErrorCard(
                message = error!!,
                onRetry = { viewModel.loadInviteData(senderId, testId) }
            )
        } else {
            // Content
            InviteContent(
                inviteId = inviteId,
                senderInfo = senderInfo,
                testTitle = testTitle,
                testInfo = testInfo,
                userPremiumStatus = userPremiumStatus,
                isAcceptingInvite = isAcceptingInvite,
                onTakeTest = {
                    //TODO:Delete AppStatus
                    if (userPremiumStatus == true || AppStatu.appStatus == "test") {
                        // User is premium, accept invite and start test
                        viewModel.acceptInviteAndStartTest(inviteId, testId)
                    } else {
                        // User is not premium, go to paywall
                        router.goToPaywall()
                    }
                },
                onUpgradeToPremium = { router.goToPaywall() }
            )
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
private fun LoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color.White,
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
                text = "Loading invitation...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
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
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error Loading Invitation",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Retry Button
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = Color(0xFF2196F3),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(role = Role.Button) { onRetry() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Retry",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun InviteContent(
    inviteId: String,
    senderInfo: SenderInfo?,
    testTitle: String,
    testInfo: TestInfo?,
    userPremiumStatus: Boolean?,
    isAcceptingInvite: Boolean,
    onTakeTest: () -> Unit,
    onUpgradeToPremium: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sender Info Card
        senderInfo?.let { sender ->
            SenderInfoCard(sender)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Test Info Card
        TestInfoCard(
            testTitle = testTitle,
            testInfo = testInfo
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        if (userPremiumStatus == true || AppStatu.appStatus == "test") {
            // Premium user - can take test
            ActionButton(
                text = if (isAcceptingInvite) "Accepting Invite..." else "Take Test & Compare Results",
                backgroundColor = Color(0xFF4CAF50),
                emoji = if (isAcceptingInvite) "⏳" else "🚀",
                onClick = onTakeTest,
                enabled = !isAcceptingInvite
            )
        } else {
            // Non-premium user - needs upgrade
            NonPremiumCard(
                senderName = senderInfo?.displayName ?: "Someone",
                testTitle = testTitle,
                onUpgrade = onUpgradeToPremium
            )
        }
    }
}

@Composable
private fun SenderInfoCard(sender: SenderInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black
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
                    text = (sender.displayName ?: "U").first().toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Invitation from:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
                Text(
                    text = sender.displayName ?: "User ${sender.userId}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                val info = listOfNotNull(sender.mbtiType, sender.zodiacSign)
                    .joinToString(" • ")
                if (info.isNotBlank()) {
                    Text(
                        text = info,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                }
            }
        }
    }
}

@Composable
private fun TestInfoCard(
    testTitle: String,
    testInfo: TestInfo?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "📋 Test Details",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = testTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            testInfo?.description?.let { description ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun NonPremiumCard(
    senderName: String,
    testTitle: String,
    onUpgrade: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black
            )
            .background(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⭐",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Premium Feature",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$senderName wants to compare results with you on \"$testTitle\".",
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Upgrade to Premium to:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text("✓ Take tests from friends", fontSize = 12.sp, color = Color(0xFF666666))
                Text("✓ Compare your results", fontSize = 12.sp, color = Color(0xFF666666))
                Text("✓ Send unlimited invites", fontSize = 12.sp, color = Color(0xFF666666))
            }

            Spacer(modifier = Modifier.height(20.dp))

            ActionButton(
                text = "Upgrade to Premium",
                backgroundColor = Color(0xFFFF9800),
                emoji = "⭐",
                onClick = onUpgrade
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    backgroundColor: Color,
    emoji: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = backgroundColor.copy(alpha = 0.3f)
            )
            .background(
                color = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                role = Role.Button,
                enabled = enabled
            ) { if (enabled) onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

// Data classes
data class SenderInfo(
    val userId: String,
    val displayName: String?,
    val mbtiType: String?,
    val zodiacSign: String?
)

data class TestInfo(
    val id: String,
    val title: String,
    val description: String?,
    val isPremium: Boolean
)