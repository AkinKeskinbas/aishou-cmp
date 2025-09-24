package com.keak.aishou.screens.quicktestscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.api.QuizQuestion
import com.keak.aishou.data.api.QuizSubmissionRequest
import com.keak.aishou.data.api.Submission
import com.keak.aishou.data.api.PersonalityAssessRequest
import com.keak.aishou.data.api.PersonalityAssessResponse
import com.keak.aishou.data.api.CompatibilityRequest
import com.keak.aishou.data.api.CompatibilityResult
import com.keak.aishou.data.PersonalityDataManager
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val error: String? = null,
    val currentQuestionIndex: Int = 0,
    val answers: Map<Int, String> = emptyMap(), // question index -> selected choice key
    val isFinished: Boolean = false,
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false,
    val submissionError: String? = null,
    val submissionResult: Submission? = null,
    val showIncompleteWarning: Boolean = false,
    val personalityResult: PersonalityAssessResponse? = null,
    val isMBTITest: Boolean = false,
    val compatibilityResult: CompatibilityResult? = null,
    val isFromInvite: Boolean = false, // Indicates if test was started from invite
    val inviteId: String? = null // Invite ID when started from invite
)

sealed class QuizUiEvent {
    data class LoadQuestions(val testId: String, val version: Int) : QuizUiEvent()
    object LoadQuickQuiz : QuizUiEvent()
    data class SelectAnswer(val questionIndex: Int, val choiceKey: String) : QuizUiEvent()
    object NextQuestion : QuizUiEvent()
    object PreviousQuestion : QuizUiEvent()
    data class SubmitQuiz(val testId: String, val version: Int) : QuizUiEvent()
    object SubmitQuickQuiz : QuizUiEvent()
    object RetryLoad : QuizUiEvent()
    object DismissIncompleteWarning : QuizUiEvent()
    object ResetSubmissionState : QuizUiEvent()
}

class QuizViewModel(
    private val apiService: AishouApiService,
    private val personalityDataManager: PersonalityDataManager
) : ViewModel() {

    private var currentSenderId: String? = null

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun setSenderInfo(senderId: String?) {
        currentSenderId = senderId
        _uiState.value = _uiState.value.copy(isFromInvite = senderId != null)
        println("QuizViewModel: Sender ID set to: $senderId")
    }

    fun onEvent(event: QuizUiEvent) {
        when (event) {
            is QuizUiEvent.LoadQuestions -> loadQuestions(event.testId, event.version)
            is QuizUiEvent.LoadQuickQuiz -> loadQuickQuiz()
            is QuizUiEvent.SelectAnswer -> selectAnswer(event.questionIndex, event.choiceKey)
            is QuizUiEvent.NextQuestion -> nextQuestion()
            is QuizUiEvent.PreviousQuestion -> previousQuestion()
            is QuizUiEvent.SubmitQuiz -> {
                if (canFinish()) {
                    submitQuiz(event.testId, event.version)
                } else {
                    // Show warning if quiz is incomplete
                    _uiState.value = _uiState.value.copy(showIncompleteWarning = true)
                }
            }
            is QuizUiEvent.SubmitQuickQuiz -> {
                if (canFinish()) {
                    submitQuickQuiz()
                } else {
                    // Show warning if quiz is incomplete
                    _uiState.value = _uiState.value.copy(showIncompleteWarning = true)
                }
            }
            is QuizUiEvent.RetryLoad -> {
                val currentState = _uiState.value
                if (currentState.questions.isNotEmpty()) {
                    val firstQuestion = currentState.questions.first()
                    loadQuestions(firstQuestion.testId, firstQuestion.version)
                }
            }
            is QuizUiEvent.DismissIncompleteWarning -> {
                _uiState.value = _uiState.value.copy(showIncompleteWarning = false)
            }
            is QuizUiEvent.ResetSubmissionState -> {
                _uiState.value = _uiState.value.copy(
                    submissionSuccess = false,
                    submissionError = null,
                    isSubmitting = false,
                    submissionResult = null,
                    personalityResult = null
                )
            }
        }
    }

    private fun loadQuestions(testId: String, version: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = apiService.getQuizQuestions(testId, version)
                when (result) {
                    is ApiResult.Success -> {
                        val questions = result.data.data ?: emptyList()
                        // Group questions by index and take unique ones
                        val uniqueQuestions = questions.groupBy { it.index }
                            .map { (_, questionsForIndex) -> questionsForIndex.first() }
                            .sortedBy { it.index }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            questions = uniqueQuestions,
                            error = null,
                            isMBTITest = testId == "personality-full-v1"
                        )
                        println("QuizViewModel: Successfully loaded ${uniqueQuestions.size} questions")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Unknown error occurred"
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        println("QuizViewModel: Error loading questions: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception occurred"
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        println("QuizViewModel: Exception loading questions: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unexpected error occurred"
                )
                println("QuizViewModel: Unexpected error: ${e.message}")
            }
        }
    }

    private fun loadQuickQuiz() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = apiService.getPersonalityQuickQuiz()
                when (result) {
                    is ApiResult.Success -> {
                        val questions = result.data.data ?: emptyList()
                        // Group questions by index and take unique ones
                        val uniqueQuestions = questions.groupBy { it.index }
                            .map { (_, questionsForIndex) -> questionsForIndex.first() }
                            .sortedBy { it.index }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            questions = uniqueQuestions,
                            error = null,
                            isMBTITest = true // Quick quiz is always MBTI test
                        )
                        println("QuizViewModel: Successfully loaded ${uniqueQuestions.size} quick quiz questions")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Unknown error occurred"
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        println("QuizViewModel: Error loading quick quiz: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception occurred"
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        println("QuizViewModel: Exception loading quick quiz: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unexpected error occurred"
                )
                println("QuizViewModel: Unexpected error loading quick quiz: ${e.message}")
            }
        }
    }

    private fun selectAnswer(questionIndex: Int, choiceKey: String) {
        val currentAnswers = _uiState.value.answers.toMutableMap()
        currentAnswers[questionIndex] = choiceKey

        _uiState.value = _uiState.value.copy(answers = currentAnswers)
        println("QuizViewModel: Selected answer $choiceKey for question $questionIndex")
    }

    private fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = currentState.currentQuestionIndex + 1
            )
        }
    }

    private fun previousQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex > 0) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = currentState.currentQuestionIndex - 1
            )
        }
    }

    private fun submitQuiz(testId: String, version: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, submissionError = null)

            try {
                val currentState = _uiState.value

                // Convert answers from Map<Int, String> to Map<String, String>
                // where key is question index as string and value is the selected choice key
                val answersForSubmission = currentState.answers.mapKeys { it.key.toString() }

                val submissionRequest = QuizSubmissionRequest.create(answersForSubmission)
                println("QuizViewModel: Submission request created: $submissionRequest")
                println("QuizViewModel: Answers map: $answersForSubmission")

                val result = apiService.submitQuiz(testId, version, submissionRequest, currentState.inviteId)
                when (result) {
                    is ApiResult.Success -> {
                        val submissionData = result.data.data
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionSuccess = true,
                            isFinished = true,
                            submissionResult = submissionData
                        )
                        println("QuizViewModel: Quiz submitted successfully. Submission ID: ${submissionData?.id}")

                        // If this is the MBTI personality test, use different endpoint
                        if (testId == "personality-full-v1") {
                            handleMBTITestSubmission(testId, version, answersForSubmission)
                        }

                        // If this test was started from an invite, compute compatibility
                        currentSenderId?.let { senderId ->
                            println("QuizViewModel: Computing compatibility with sender: $senderId")
                            computeCompatibility(testId, version, senderId)
                        }
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Submission failed"
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionError = errorMessage
                        )
                        println("QuizViewModel: Error submitting quiz: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception during submission"
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionError = errorMessage
                        )
                        println("QuizViewModel: Exception submitting quiz: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submissionError = e.message ?: "Unexpected error during submission"
                )
                println("QuizViewModel: Unexpected error: ${e.message}")
            }
        }
    }

    private fun submitQuickQuiz() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSubmitting = true, submissionError = null)

                val currentState = _uiState.value
                println("QuizViewModel: Submitting quick quiz with ${currentState.answers.size} answers")

                // Convert answers from Map<Int, String> to Map<String, String>
                val answersForSubmission = currentState.answers.mapKeys { it.key.toString() }

                // Use personality quick assess endpoint for quick quiz
                val assessRequest = PersonalityAssessRequest(
                    testId = "quick-quiz",
                    version = 1,
                    answers = answersForSubmission
                )
                println("QuizViewModel: Quick quiz assessment request created: $assessRequest")

                val result = apiService.personalityQuickAssess(assessRequest)
                when (result) {
                    is ApiResult.Success -> {
                        val personalityResult = result.data.data
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionSuccess = true,
                            isFinished = true,
                            personalityResult = personalityResult
                        )
                        println("QuizViewModel: Quick quiz submitted successfully. MBTI: ${personalityResult?.mbtiType}")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Quick quiz submission failed"
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionError = errorMessage
                        )
                        println("QuizViewModel: Error submitting quick quiz: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception during quick quiz submission"
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionError = errorMessage
                        )
                        println("QuizViewModel: Exception submitting quick quiz: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submissionError = e.message ?: "Unexpected error during quick quiz submission"
                )
                println("QuizViewModel: Unexpected error in quick quiz: ${e.message}")
            }
        }
    }

    fun getProgress(): Float {
        val currentState = _uiState.value
        if (currentState.questions.isEmpty()) return 0f
        return currentState.answers.size.toFloat() / currentState.questions.size.toFloat()
    }

    fun canFinish(): Boolean {
        val currentState = _uiState.value
        return currentState.answers.size == currentState.questions.size
    }

    private fun handleMBTITestSubmission(testId: String, version: Int, answers: Map<String, String>) {
        viewModelScope.launch {
            try {
                val assessRequest = PersonalityAssessRequest(
                    testId = testId,
                    version = version,
                    answers = answers
                )

                val result = apiService.personalityQuickAssess(assessRequest)
                when (result) {
                    is ApiResult.Success -> {
                        val personalityData = result.data.data
                        _uiState.value = _uiState.value.copy(
                            personalityResult = personalityData,
                            submissionSuccess = true,
                            isFinished = true
                        )
                        println("QuizViewModel: MBTI assessment completed: ${personalityData?.mbtiType}")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "MBTI assessment failed"
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionError = errorMessage
                        )
                        println("QuizViewModel: Error in MBTI assessment: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "MBTI assessment failed"
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            submissionError = errorMessage
                        )
                        println("QuizViewModel: Exception in MBTI assessment: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submissionError = e.message ?: "MBTI assessment failed"
                )
                println("QuizViewModel: Error handling MBTI test submission: ${e.message}")
            }
        }
    }

    private fun computeCompatibility(testId: String, version: Int, friendId: String) {
        viewModelScope.launch {
            try {
                println("QuizViewModel: Starting compatibility computation...")
                println("QuizViewModel: Request params - testId: '$testId', version: $version, friendId: '$friendId'")

                val compatibilityRequest = CompatibilityRequest(
                    testId = testId,
                    version = version,
                    friendId = friendId
                )
                println("QuizViewModel: Compatibility request created: $compatibilityRequest")

                val result = apiService.computeCompatibility(compatibilityRequest)
                when (result) {
                    is ApiResult.Success -> {
                        val compatibilityData = result.data.data
                        _uiState.value = _uiState.value.copy(
                            compatibilityResult = compatibilityData
                        )
                        println("QuizViewModel: Compatibility computed successfully: Score=${compatibilityData?.score}")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Compatibility calculation failed"
                        println("QuizViewModel: Error computing compatibility: $errorMessage")
                        // Don't update submissionError since the main submission was successful
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Compatibility calculation failed"
                        println("QuizViewModel: Exception computing compatibility: $errorMessage")
                        // Don't update submissionError since the main submission was successful
                    }
                }
            } catch (e: Exception) {
                println("QuizViewModel: Unexpected error computing compatibility: ${e.message}")
                // Don't update submissionError since the main submission was successful
            }
        }
    }

    // Method to set invite ID when quiz is started from invite
    fun setInviteId(inviteId: String) {
        _uiState.value = _uiState.value.copy(
            inviteId = inviteId,
            isFromInvite = true
        )
        println("QuizViewModel: Set inviteId: $inviteId")
    }
}