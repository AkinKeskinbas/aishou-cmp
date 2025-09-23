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
import androidx.lifecycle.lifecycleScope
import com.keak.aishou.navigation.DeepLinkCoordinator
import com.keak.aishou.utils.ImageShareHelperFactory
import com.keak.aishou.utils.ShareHelperFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ShareHelperFactory.initialize(this)
        ImageShareHelperFactory.initialize(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            App()
        }

        handleDeepLink(intent)?.toString()?.let { url ->
            println("MainActivity-->DeepLinkCoordinator: incoming deeplink $url")
            if (!DeepLinkCoordinator.tryEmit(url)) {
                lifecycleScope.launch { DeepLinkCoordinator.emit(url) }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)?.toString()?.let { url ->
            println("MainActivity: New deeplink intent: $url")
            if (!DeepLinkCoordinator.tryEmit(url)) {
                lifecycleScope.launch { DeepLinkCoordinator.emit(url) }
            }
        }
    }

    private fun handleDeepLink(intent: Intent): Uri? = when (intent.action) {
        Intent.ACTION_VIEW -> intent.data?.also { println("MainActivity: Received deeplink: $it") }
        else -> null
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
