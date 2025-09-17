package com.keak.aishou.screens.splashscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.keak.aishou.navigation.Router

@Composable
fun SplashScreen(router: Router) {

    Column {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "SplashScreen",
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    router.goToPaywall()
                })
        }
    }
}