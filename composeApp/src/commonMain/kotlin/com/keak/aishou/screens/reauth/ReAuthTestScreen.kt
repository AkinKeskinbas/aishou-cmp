package com.keak.aishou.screens.reauth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.navigation.Router
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * 🧪 TEST SCREEN FOR RE-AUTH FLOW
 * REMOVE IN PRODUCTION!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReAuthTestScreen(
    router: Router
) {
    val userSessionManager: UserSessionManager = koinInject()
    val scope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("Ready for testing...") }

    LaunchedEffect(Unit) {
        statusText = ReAuthTestHelper.checkAuthStatus(userSessionManager)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1C2128)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🧪 ReAuth Test Console",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "REMOVE IN PRODUCTION!",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Display
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1C2128)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📊 Current Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = Color(0xFF8B949E),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Test Buttons
        TestButton(
            text = "🔄 Check Auth Status",
            description = "Get current authentication state"
        ) {
            scope.launch {
                statusText = "Checking status..."
                statusText = ReAuthTestHelper.checkAuthStatus(userSessionManager)
            }
        }

        TestButton(
            text = "❌ Simulate 401 (Unauthorized)",
            description = "Clear tokens & mark as unauthorized"
        ) {
            scope.launch {
                statusText = "Simulating 401..."
                ReAuthTestHelper.simulateUnauthorizedUser(userSessionManager)
                statusText = ReAuthTestHelper.checkAuthStatus(userSessionManager)
            }
        }

        TestButton(
            text = "🚫 Simulate Too Many Retries",
            description = "Trigger retry limit (3 attempts)"
        ) {
            scope.launch {
                statusText = "Setting up max retries..."
                ReAuthTestHelper.simulateTooManyRetries(userSessionManager)
                statusText = ReAuthTestHelper.checkAuthStatus(userSessionManager)
            }
        }

        TestButton(
            text = "⏰ Simulate Recent Retry",
            description = "Mark recent attempt (throttling)"
        ) {
            scope.launch {
                statusText = "Marking recent retry..."
                ReAuthTestHelper.simulateRecentRetry(userSessionManager)
                statusText = ReAuthTestHelper.checkAuthStatus(userSessionManager)
            }
        }

        TestButton(
            text = "🔄 Reset Auth State",
            description = "Clear all retry attempts"
        ) {
            scope.launch {
                statusText = "Resetting auth state..."
                ReAuthTestHelper.resetAuthState(userSessionManager)
                statusText = ReAuthTestHelper.checkAuthStatus(userSessionManager)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Buttons
        Button(
            onClick = { router.goToReAuth() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF58A6FF)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "🚀 Go to ReAuth Screen",
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { router.goToHome() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF8B949E)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("← Back to Home")
        }
    }
}

@Composable
private fun TestButton(
    text: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF21262D)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF8B949E)
            )
        }
    }
}