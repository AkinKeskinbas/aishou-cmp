package com.keak.aishou.screens.profile

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.brain
import aishou.composeapp.generated.resources.crown_premium
import aishou.composeapp.generated.resources.double_star
import aishou.composeapp.generated.resources.star
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.navigation.Router
import com.keak.aishou.purchase.PremiumChecker
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    router: Router,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF49DC9C))
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
                UserInfoCard(
                    name = userProfile!!.displayName ?: "Anonymous User",
                    mbti = userProfile!!.mbtiType,
                    zodiac = userProfile!!.zodiacSign
                )
            }

            // Stats Card
            item {
                YourStatsCard(
                    finishedTest = userProfile!!.totalQuizzes.toString(),
                    launchedTime = viewModel.appLaunchedTime
                )
            }

            // Premium Card
            if (PremiumChecker.isPremium.not()) {
                item {
                    PremiumCard() {
                        router.goToPaywall()
                    }
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
private fun UserInfoCard(
    name: String?,
    mbti: String?,
    zodiac: String?
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        backgroundColor = Color.Black,
        shadowColor = Color.Red,
    ) {
        val isPremium = PremiumChecker.isPremium
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PROFILE",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Yellow
                )
                if (isPremium) {
                    Box(
                        modifier = Modifier.background(Color.Yellow)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(resource = Res.drawable.crown_premium),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "PRO",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.width(90.dp),
                    backgroundColor = Color.Yellow
                ) {
                    Text(
                        text = name.orEmpty().first().toString().uppercase(),
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = name.orEmpty(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Yellow
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = mbti.orEmpty(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Yellow
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = zodiac.orEmpty(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Yellow
                    )
                }

            }

        }
    }
}

@Composable
private fun YourStatsCard(
    finishedTest: String,
    launchedTime: String
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        backgroundColor = Color(0xFF66BB6A),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = "YOUR STATS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White
                ) {
                    Column {
                        Text(
                            text = finishedTest,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "TEST ACED!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                }
                Spacer(Modifier.width(16.dp))
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White
                ) {
                    Column {
                        Text(
                            text = launchedTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "DAY STREAK",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                }
            }

        }
    }
}

@Composable
private fun PremiumCard(
    onClick: () -> Unit
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        backgroundColor = Color(0xFFFFA726),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.double_star),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Unlock Your Destiny!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White
                ) {
                    Column {
                        Image(
                            painter = painterResource(Res.drawable.star),
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "UNLIMITED TESTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                }
                Spacer(Modifier.width(16.dp))
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White
                ) {
                    Column {
                        Image(
                            painter = painterResource(Res.drawable.brain),
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "DEEP INSIGHTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                }
            }
            Spacer(Modifier.height(16.dp))
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .clickable(role = Role.Button) {
                        onClick.invoke()
                    },
                backgroundColor = Color(0xFFFDD835)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unluck Your Third Eye!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }
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