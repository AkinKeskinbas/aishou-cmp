package com.keak.aishou.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Safe area utilities for handling insets across platforms
 */
object SafeAreaUtils {

    /**
     * Get safe drawing insets that avoid system UI like status bar, navigation bar, and notches
     */
    @Composable
    fun getSafeDrawingInsets(): WindowInsets = WindowInsets.safeDrawing

    /**
     * Get status bar insets only
     */
    @Composable
    fun getStatusBarInsets(): WindowInsets = WindowInsets.statusBars

    /**
     * Get navigation bar insets only
     */
    @Composable
    fun getNavigationBarInsets(): WindowInsets = WindowInsets.navigationBars

    /**
     * Get system bars insets (status + navigation)
     */
    @Composable
    fun getSystemBarsInsets(): WindowInsets = WindowInsets.systemBars

    /**
     * Get keyboard (IME) insets
     */
    @Composable
    fun getKeyboardInsets(): WindowInsets = WindowInsets.ime

    /**
     * Get safe drawing padding values
     */
    @Composable
    fun getSafeDrawingPadding(): PaddingValues = WindowInsets.safeDrawing.asPaddingValues()

    /**
     * Get status bar padding values
     */
    @Composable
    fun getStatusBarPadding(): PaddingValues = WindowInsets.statusBars.asPaddingValues()

    /**
     * Get navigation bar padding values
     */
    @Composable
    fun getNavigationBarPadding(): PaddingValues = WindowInsets.navigationBars.asPaddingValues()

    /**
     * Get combined safe area and keyboard insets
     */
    @Composable
    fun getSafeAreaWithKeyboard(): WindowInsets = WindowInsets.safeDrawing.add(WindowInsets.ime)
}

/**
 * Extension functions for easier usage
 */

/**
 * Apply safe drawing insets padding
 */
@Composable
fun Modifier.safeDrawingPadding(): Modifier =
    this.windowInsetsPadding(WindowInsets.safeDrawing)

/**
 * Apply status bar insets padding only
 */
@Composable
fun Modifier.statusBarsPadding(): Modifier =
    this.windowInsetsPadding(WindowInsets.statusBars)

/**
 * Apply navigation bar insets padding only
 */
@Composable
fun Modifier.navigationBarsPadding(): Modifier =
    this.windowInsetsPadding(WindowInsets.navigationBars)

/**
 * Apply system bars insets padding (status + navigation)
 */
@Composable
fun Modifier.systemBarsPadding(): Modifier =
    this.windowInsetsPadding(WindowInsets.systemBars)

/**
 * Apply keyboard insets padding
 */
@Composable
fun Modifier.imePadding(): Modifier =
    this.windowInsetsPadding(WindowInsets.ime)