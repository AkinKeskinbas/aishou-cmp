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
}