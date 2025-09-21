package com.keak.aishou.network

object ApiList {
   // const val BASE_URL = "https://rizzu-backend.onrender.com"
    // const val BASE_URL = "http://10.0.2.2:3060"  // Use 10.0.2.2 for Android emulator to reach host localhost
    const val BASE_URL = "http://172.22.1.95:3060"  // Use actual IP for real device testing

    const val GET_CHAT_ANALYSIS = "/analyze"
    const val GET_TOKEN = "/auth/token"
    const val REGISTER_USER = "/v1/auth/register"
    const val UPDATE_PERSONALITY = "/v1/personality/update"
    const val PERSONALITY_QUICK_ASSESS = "/v1/personality/quick-assess"
    const val GET_PERSONALITY_PROFILE = "/v1/personality/profile"
    const val GET_PERSONALITY_QUICK_QUIZ = "/v1/personality/quick-quiz"
    const val GET_TESTS = "/v1/tests"

    // Quiz questions endpoint - use with string formatting: v1/tests/{testId}/versions/{version}/questions
    fun getQuizQuestionsUrl(testId: String, version: Int): String {
        return "/v1/tests/$testId/versions/$version/questions"
    }

    // Quiz submission endpoint - use with string formatting: /v1/submissions/{testId}/{version}
    fun getQuizSubmissionUrl(testId: String, version: Int): String {
        return "/v1/submissions/$testId/$version"
    }

    // Test results endpoint - use with string formatting: /v1/tests/{testId}/results
    fun getTestResultsUrl(testId: String): String {
        return "/v1/tests/$testId/results"
    }

    // Reprocess test results endpoint - use with string formatting: /v1/tests/{testId}/reprocess
    fun getReprocessTestUrl(testId: String): String {
        return "/v1/tests/$testId/reprocess"
    }

}