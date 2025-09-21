package com.keak.aishou.screens.quicktestscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.domain.usecase.QuickTestHomeUseCase
import com.keak.aishou.network.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuickTestHomeScreenViewModel(
    private val quickTestHomeUseCase: QuickTestHomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestUiState())
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()

    init {
        loadTests()
    }

    fun onEvent(event: TestUiEvent) {
        when (event) {
            is TestUiEvent.LoadTests -> loadTests()
            is TestUiEvent.RetryLoad -> loadTests()
        }
    }

    private fun loadTests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = quickTestHomeUseCase.getTests()
                when (result) {
                    is ApiResult.Success -> {
                        val tests = result.data.data ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tests = tests,
                            error = null
                        )
                        println("QuickTestHomeScreenViewModel: Successfully loaded ${tests.size} tests")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Unknown error occurred"
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        println("QuickTestHomeScreenViewModel: Error loading tests: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception occurred"
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        println("QuickTestHomeScreenViewModel: Exception loading tests: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unexpected error occurred"
                )
                println("QuickTestHomeScreenViewModel: Unexpected error: ${e.message}")
            }
        }
    }
}