package com.keak.aishou.network

import com.keak.aishou.data.api.QuizQuestion
import com.keak.aishou.data.api.QuizSubmissionRequest
import com.keak.aishou.data.api.Submission
import com.keak.aishou.data.api.Test
import com.keak.aishou.data.api.UserRegister
import com.keak.aishou.data.api.PersonalityUpdateRequest
import com.keak.aishou.data.api.PersonalityAssessRequest
import com.keak.aishou.data.api.PersonalityAssessResponse
import com.keak.aishou.data.api.UserProfileResponse
import com.keak.aishou.data.api.TestResultResponse
import com.keak.aishou.data.api.InviteCreateRequest
import com.keak.aishou.data.api.InviteResponse
import com.keak.aishou.data.api.PushReq
import com.keak.aishou.data.api.CompatibilityRequest
import com.keak.aishou.data.api.CompatibilityResult
import com.keak.aishou.data.models.*
import com.keak.aishou.response.BaseResponse
import com.keak.aishou.response.TokenResponse


interface AishouApiService {
    suspend fun getToken(): ApiResult<BaseResponse<TokenResponse>>
    suspend fun registerUser(userRegister: UserRegister): ApiResult<BaseResponse<TokenResponse>>
    suspend fun updatePersonality(personalityUpdate: PersonalityUpdateRequest): ApiResult<BaseResponse<Unit>>
    suspend fun personalityQuickAssess(assessRequest: PersonalityAssessRequest): ApiResult<BaseResponse<PersonalityAssessResponse>>
    suspend fun getPersonalityQuickQuiz(): ApiResult<BaseResponse<List<QuizQuestion>>>
    suspend fun getUserProfile(): ApiResult<BaseResponse<UserProfileResponse>>
    suspend fun getTestResults(testId: String): ApiResult<BaseResponse<TestResultResponse>>
    suspend fun reprocessTestResults(testId: String): ApiResult<BaseResponse<TestResultResponse>>
    suspend fun getTests(): ApiResult<BaseResponse<List<Test>>>
    suspend fun getQuizQuestions(testId: String, version: Int): ApiResult<BaseResponse<List<QuizQuestion>>>
    suspend fun submitQuiz(testId: String, version: Int, submission: QuizSubmissionRequest): ApiResult<BaseResponse<Submission>>
    suspend fun createInvite(inviteRequest: InviteCreateRequest): ApiResult<InviteResponse>
    suspend fun registerPush(pushReq: PushReq): ApiResult<BaseResponse<Unit>>

    // Friends API endpoints
    suspend fun sendFriendRequest(request: SendFriendRequestReq): ApiResult<BaseResponse<FriendRequest>>
    suspend fun respondToFriendRequest(requestId: String, response: RespondFriendRequestReq): ApiResult<BaseResponse<FriendRequest>>
    suspend fun getReceivedRequests(unreadOnly: Boolean = false): ApiResult<BaseResponse<List<RequestWithSenderInfo>>>
    suspend fun getSentRequests(): ApiResult<BaseResponse<List<RequestWithReceiverInfo>>>
    suspend fun getFriendsList(): ApiResult<BaseResponse<List<FriendInfo>>>
    suspend fun markRequestAsRead(requestId: String): ApiResult<BaseResponse<Unit>>
    suspend fun markAllRequestsAsRead(): ApiResult<BaseResponse<Unit>>
    suspend fun getUnreadRequestsCount(): ApiResult<BaseResponse<UnreadCount>>
    suspend fun removeFriend(friendId: String): ApiResult<BaseResponse<Unit>>

    // Invite endpoints
    suspend fun acceptInvite(inviteId: String): ApiResult<BaseResponse<Unit>>

    // Compatibility endpoints
    suspend fun computeCompatibility(request: CompatibilityRequest): ApiResult<BaseResponse<CompatibilityResult>>
}