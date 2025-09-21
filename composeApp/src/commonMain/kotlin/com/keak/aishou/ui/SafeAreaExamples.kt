package com.keak.aishou.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Example demonstrating different safe area usage patterns
 */
object SafeAreaExamples {

    /**
     * Example: Full screen with safe area padding applied to content
     */
    @Composable
    fun FullScreenWithSafeArea() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue) // This will extend behind system bars
                .safeDrawingPadding() // Content respects safe areas
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Content with safe area padding",
                    color = Color.White
                )
                Text(
                    text = "This content won't be hidden by notch or status bar",
                    color = Color.White
                )
            }
        }
    }

    /**
     * Example: Only top safe area (status bar) padding
     */
    @Composable
    fun TopSafeAreaOnly() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Only avoid status bar
        ) {
            Text(
                text = "Content avoiding status bar only",
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    /**
     * Example: Using Scaffold with safe areas
     */
    @Composable
    fun ScaffoldWithSafeArea() {
        Scaffold(
            modifier = Modifier.safeDrawingPadding(),
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Top Bar",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text("Scaffold content with safe areas")
            }
        }
    }

    /**
     * Example: Manual safe area handling for specific needs
     */
    @Composable
    fun ManualSafeAreaHandling() {
        val statusBarPadding = SafeAreaUtils.getStatusBarPadding()
        val navigationBarPadding = SafeAreaUtils.getNavigationBarPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(statusBarPadding) // Manual top padding
        ) {
            Text(
                text = "Manually handled safe areas",
                modifier = Modifier.padding(16.dp)
            )

            // Content that should go to bottom edge but avoid navigation bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            // Bottom content with navigation bar padding
            Text(
                text = "Bottom content",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(navigationBarPadding)
                    .padding(16.dp)
            )
        }
    }
}