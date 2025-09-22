package com.keak.aishou

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.keak.aishou.utils.ShareHelperFactory
import com.keak.aishou.utils.ImageShareHelperFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize ShareHelper for Android
        ShareHelperFactory.initialize(this)
        ImageShareHelperFactory.initialize(this)

        // Make status bar and navigation bar transparent/immersive
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            App(deepLinkUrl = handleDeepLink(intent)?.toString())
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Handle deeplink when app is already running
        val uri = handleDeepLink(intent)
        uri?.let {
            // TODO: Handle deeplink navigation when app is already running
            println("MainActivity: New deeplink intent: $it")
        }
    }

    private fun handleDeepLink(intent: Intent): Uri? {
        return when (intent.action) {
            Intent.ACTION_VIEW -> {
                val uri = intent.data
                println("MainActivity: Received deeplink: $uri")
                uri
            }
            else -> null
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}