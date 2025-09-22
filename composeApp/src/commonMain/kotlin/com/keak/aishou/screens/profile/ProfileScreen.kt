package com.keak.aishou.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.purchase.PremiumState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    router: Router,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val premiumState by viewModel.premiumState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = BackGroundBrush.homNeoBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Header
        item {
            ProfileHeader(
                onBackClick = { router.goBack() }
            )
        }

        if (isLoading) {
            item {
                ProfileLoadingCard()
            }
        } else if (error != null) {
            item {
                ProfileErrorCard(
                    error = error!!,
                    onRetry = { viewModel.loadProfile() }
                )
            }
        } else if (userProfile != null) {
            // Profile Info Card
            item {
                ProfileInfoCard(
                    name = userProfile!!.displayName ?: "User",
                    mbti = userProfile!!.mbtiType,
                    zodiac = userProfile!!.zodiacSign
                )
            }

            // Stats Card
            item {
                ProfileStatsCard(
                    totalTests = userProfile!!.totalQuizzes
                )
            }

            // Premium Card
            if (premiumState !is PremiumState.Premium) {
                item {
                    PremiumUpgradeCard(
                        onUpgradeClick = { router.goToPaywall() }
                    )
                }
            } else {
                item {
                    PremiumStatusCard()
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
            tint = Color.Black
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
    }
}

@Composable
private fun ProfileInfoCard(
    name: String,
    mbti: String?,
    zodiac: String?
) {
    NeoBrutalCard {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Icon & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF6366F1)
                )

                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            // MBTI & Zodiac
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (mbti != null) {
                    PersonalityBadge(
                        text = mbti,
                        color = Color(0xFFEF4444)
                    )
                }

                if (zodiac != null) {
                    PersonalityBadge(
                        text = zodiac,
                        color = Color(0xFF8B5CF6)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatsCard(
    totalTests: Int
) {
    NeoBrutalCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tests Completed",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = totalTests.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF6366F1)
            )
        }
    }
}

@Composable
private fun PremiumUpgradeCard(
    onUpgradeClick: () -> Unit
) {
    NeoBrutalCard(
        backgroundColor = Color(0xFFFEF3C7),
        borderColor = Color(0xFFF59E0B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFF59E0B)
                )

                Text(
                    text = "Get Premium",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            NeoBrutalButton(
                text = "Upgrade",
                onClick = onUpgradeClick,
                backgroundColor = Color(0xFFF59E0B),
                textColor = Color.White,
                compact = true
            )
        }
    }
}

@Composable
private fun PremiumStatusCard() {
    NeoBrutalCard(
        backgroundColor = Color(0xFFD1FAE5),
        borderColor = Color(0xFF10B981)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF10B981)
            )

            Text(
                text = "Premium Member",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun ProfileLoadingCard() {
    NeoBrutalCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF6366F1),
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading profile...",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ProfileErrorCard(
    error: String,
    onRetry: () -> Unit
) {
    NeoBrutalCard(
        backgroundColor = Color(0xFFFEE2E2),
        borderColor = Color(0xFFEF4444)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = error,
                fontSize = 14.sp,
                color = Color(0xFFDC2626),
                textAlign = TextAlign.Center
            )

            NeoBrutalButton(
                text = "Retry",
                onClick = onRetry,
                backgroundColor = Color(0xFFEF4444),
                textColor = Color.White
            )
        }
    }
}

@Composable
private fun PersonalityBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun NeoBrutalCard(
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Shadow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .offset(y = (-2).dp)
                .background(
                    color = borderColor,
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )
        )

        content()
    }
}

@Composable
private fun NeoBrutalButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color.Black,
    textColor: Color = Color.White,
    compact: Boolean = false
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(6.dp)
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(
                horizontal = if (compact) 16.dp else 24.dp,
                vertical = if (compact) 8.dp else 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = if (compact) 12.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}