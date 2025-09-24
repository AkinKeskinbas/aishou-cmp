package com.keak.aishou.screens.paywall

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PaywallLegalContent(url: String, modifier: Modifier = Modifier)
