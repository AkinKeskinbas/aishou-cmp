package com.keak.aishou.screens.quicktestscreen

import com.keak.aishou.data.api.Test

data class TestUiState(
    val isLoading: Boolean = false,
    val tests: List<Test> = emptyList(),
    val error: String? = null
)

sealed class TestUiEvent {
    object LoadTests : TestUiEvent()
    object RetryLoad : TestUiEvent()
}