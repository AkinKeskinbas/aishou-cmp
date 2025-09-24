package com.keak.aishou.network

object ApiList {
    // Platform-specific base URLs - automatically selects correct URL for each platform
    val BASE_URL = PlatformConfig.baseUrl

    // Alternative URLs (comment out PlatformConfig.baseUrl above and uncomment one below if needed):
    // const val BASE_URL = "http://172.22.1.95:3060"  // Use actual IP for real device testing

    const val GET_CHAT_ANALYSIS = "/analyze"
    const val GET_TOKEN = "/auth/token"
    const val REGISTER_USER = "/v1/auth/register"
    const val UPDATE_PERSONALITY = "/v1/personality/update"
    const val PERSONALITY_QUICK_ASSESS = "/v1/personality/quick-assess"
    const val GET_PERSONALITY_PROFILE = "/v1/personality/profile"
    const val GET_PERSONALITY_QUICK_QUIZ = "/v1/personality/quick-quiz"
    const val GET_TESTS = "/v1/tests"
    const val POST_CREATE_INVITE = "/v1/invites"
    const val GET_RECEIVED_INVITES = "/v1/invites/received"
    const val POST_PUSH_REGISTER = "/v1/push/register"
    const val POST_COMPUTE_COMPATIBILITY = "/v1/compatibility/compute"

    // Friends API endpoints
    const val POST_FRIEND_REQUEST = "/v1/friends/request"
    const val GET_RECEIVED_REQUESTS = "/v1/friends/requests/received"
    const val GET_SENT_REQUESTS = "/v1/friends/requests/sent"
    const val GET_FRIENDS_LIST = "/v1/friends/my-friends"
    const val PUT_MARK_ALL_READ = "/v1/friends/requests/mark-all-read"
    const val GET_UNREAD_COUNT = "/v1/friends/requests/unread-count"

    // Quiz questions endpoint - use with string formatting: v1/tests/{testId}/versions/{version}/questions
    fun getQuizQuestionsUrl(testId: String, version: Int): String {
        return "/v1/tests/$testId/versions/$version/questions"
    }

    // Quiz submission endpoint - use with string formatting: /v1/submissions/{testId}/{version}
    fun getQuizSubmissionUrl(testId: String, version: Int, inviteId: String? = null): String {
        return if (inviteId != null) {
            "/v1/submissions/$testId/$version?inviteId=$inviteId"
        } else {
            "/v1/submissions/$testId/$version"
        }
    }

    // Test results endpoint - use with string formatting: /v1/tests/{testId}/results
    fun getTestResultsUrl(testId: String, friendId: String? = null): String {
        return if (friendId != null) {
            "/v1/tests/$testId/results?friendId=$friendId"
        } else {
            "/v1/tests/$testId/results"
        }
    }

    // Reprocess test results endpoint - use with string formatting: /v1/tests/{testId}/reprocess
    fun getReprocessTestUrl(testId: String): String {
        return "/v1/tests/$testId/reprocess"
    }

    // Friends dynamic endpoints
    fun getRespondFriendRequestUrl(requestId: String): String {
        return "/v1/friends/request/$requestId/respond"
    }

    fun getMarkRequestReadUrl(requestId: String): String {
        return "/v1/friends/request/$requestId/mark-read"
    }

    fun getRemoveFriendUrl(friendId: String): String {
        return "/v1/friends/$friendId"
    }

    // Invite accept endpoint - use with string formatting: /v1/{inviteId}/accept
    fun getAcceptInviteUrl(inviteId: String): String {
        return "/v1/invites/$inviteId/accept"
    }

    // Invite reject endpoint - use with string formatting: /v1/{inviteId}/reject
    fun getRejectInviteUrl(inviteId: String): String {
        return "/v1/invites/$inviteId/reject"
    }

}