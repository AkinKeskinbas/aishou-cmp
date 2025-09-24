package com.keak.aishou.screens.reauth

import aishou.composeapp.generated.resources.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReAuthScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSupport: () -> Unit,
    viewModel: ReAuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigationEvent) {
        when (uiState.navigationEvent) {
            ReAuthNavigationEvent.NavigateToHome -> {
                onNavigateToHome()
                viewModel.onNavigationHandled()
            }
            ReAuthNavigationEvent.NavigateToSupport -> {
                onNavigateToSupport()
                viewModel.onNavigationHandled()
            }
            null -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1117), // GitHub dark background
                        Color(0xFF161B22)  // Slightly lighter
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1C2128) // GitHub card background
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState.state) {
                    ReAuthState.Checking -> {
                        CheckingContent()
                    }
                    ReAuthState.Reregistering -> {
                        ReregisteringContent(uiState.progress)
                    }
                    ReAuthState.Error -> {
                        ErrorContent(
                            message = uiState.errorMessage,
                            canRetry = uiState.canRetry,
                            onRetry = { viewModel.retryAuth() },
                            onSupport = { viewModel.navigateToSupport() }
                        )
                    }
                    ReAuthState.Success -> {
                        SuccessContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔄",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(Res.string.reauth_checking_account),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF0F6FC), // GitHub text primary
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.reauth_please_wait),
            fontSize = 14.sp,
            color = Color(0xFF8B949E), // GitHub text secondary
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            color = Color(0xFF58A6FF), // GitHub accent blue
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ReregisteringContent(progress: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚡",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(Res.string.reauth_session_renewing),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF0F6FC),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.reauth_security_renewal),
            fontSize = 14.sp,
            color = Color(0xFF8B949E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            color = Color(0xFF58A6FF),
            trackColor = Color(0xFF21262D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 12.sp,
            color = Color(0xFF8B949E)
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    onSupport: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(Res.string.reauth_connection_problem),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF85149), // GitHub error red
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF8B949E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (canRetry) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58A6FF), // GitHub accent blue
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.reauth_try_again),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onSupport,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF8B949E)
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.reauth_contact_support),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SuccessContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✨",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(Res.string.reauth_success),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3FB950), // GitHub success green
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.reauth_account_renewed),
            fontSize = 14.sp,
            color = Color(0xFF8B949E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            color = Color(0xFF3FB950),
            modifier = Modifier.size(24.dp)
        )
    }
}