package com.keak.aishou.network

import com.gyanoba.inspektor.Inspektor
import com.keak.aishou.data.DataStoreManager
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
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class AishouApiImpl(
    private val dataStoreManager: DataStoreManager
) : AishouApiService {

    private val client: HttpClient by lazy {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 60000  // 60 seconds for the entire request
                connectTimeoutMillis = 20000  // 20 seconds to establish connection
                socketTimeoutMillis = 60000   // 60 seconds for socket timeout
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println("KtorClient: $message")
                    }
                }
            }
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }

            install(ContentNegotiation) {
                json(json, contentType = ContentType.Any)
            }
            install(Inspektor)

            defaultRequest {
                url(ApiList.BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    private suspend fun getAuthHeader(): String? {
        val accessToken = dataStoreManager.accessToken.first()
        return if (accessToken != null) "Bearer $accessToken" else null
    }

    override suspend fun getToken(): ApiResult<BaseResponse<TokenResponse>> {
        return handleApi {
            client.get(ApiList.GET_TOKEN) {
                getAuthHeader()?.let { authHeader ->
                    header(HttpHeaders.Authorization, authHeader)
                }
            }
        }
    }

    override suspend fun registerUser(userRegister: UserRegister): ApiResult<BaseResponse<TokenResponse>> {
        println("AishouApiImpl: Making registration request to ${ApiList.BASE_URL}${ApiList.REGISTER_USER}")
        println("AishouApiImpl: Request body: $userRegister")

        return handleApi {
            client.post(ApiList.REGISTER_USER) {
                contentType(ContentType.Application.Json)
                setBody(userRegister)
                // Note: Registration typically doesn't need auth header, but we'll add it if available
                getAuthHeader()?.let { authHeader ->
                    header(HttpHeaders.Authorization, authHeader)
                }
            }
        }
    }

    override suspend fun updatePersonality(personalityUpdate: PersonalityUpdateRequest): ApiResult<BaseResponse<Unit>> {
        println("AishouApiImpl: Making personality update request to ${ApiList.BASE_URL}${ApiList.UPDATE_PERSONALITY}")
        println("AishouApiImpl: Request body: $personalityUpdate")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        return handleApi {
            client.put(ApiList.UPDATE_PERSONALITY) {
                contentType(ContentType.Application.Json)
                setBody(personalityUpdate)
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }
    }

    override suspend fun personalityQuickAssess(assessRequest: PersonalityAssessRequest): ApiResult<BaseResponse<PersonalityAssessResponse>> {
        println("AishouApiImpl: Making personality quick assess request to ${ApiList.BASE_URL}${ApiList.PERSONALITY_QUICK_ASSESS}")
        println("AishouApiImpl: Request body: $assessRequest")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        return handleApi {
            client.post(ApiList.PERSONALITY_QUICK_ASSESS) {
                contentType(ContentType.Application.Json)
                setBody(assessRequest)
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }
    }

    override suspend fun getPersonalityQuickQuiz(): ApiResult<BaseResponse<List<QuizQuestion>>> {
        println("AishouApiImpl: Making get personality quick quiz request to ${ApiList.BASE_URL}${ApiList.GET_PERSONALITY_QUICK_QUIZ}")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        return handleApi {
            client.get(ApiList.GET_PERSONALITY_QUICK_QUIZ) {
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }
    }

    override suspend fun getUserProfile(): ApiResult<BaseResponse<UserProfileResponse>> {
        println("AishouApiImpl: Making get user profile request to ${ApiList.BASE_URL}${ApiList.GET_PERSONALITY_PROFILE}")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        val result = handleApi<BaseResponse<UserProfileResponse>> {
            client.get(ApiList.GET_PERSONALITY_PROFILE) {
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }

        when (result) {
            is ApiResult.Success -> {
                println("AishouApiImpl: Profile response successful: ${result.data}")
                println("AishouApiImpl: Profile data: ${result.data.data}")
                println("AishouApiImpl: Solo quizzes count: ${result.data.data?.soloQuizzes?.size}")
                println("AishouApiImpl: Match quizzes count: ${result.data.data?.matchQuizzes?.size}")
                result.data.data?.soloQuizzes?.forEachIndexed { index, quiz ->
                    println("AishouApiImpl: Solo quiz $index: $quiz")
                }
            }
            is ApiResult.Error -> {
                println("AishouApiImpl: Profile request failed with error: ${result.message}")
            }
            is ApiResult.Exception -> {
                println("AishouApiImpl: Profile request failed with exception: ${result.exception.message}")
            }
        }

        return result
    }

    override suspend fun getTestResults(testId: String): ApiResult<BaseResponse<TestResultResponse>> {
        val url = ApiList.getTestResultsUrl(testId)
        println("AishouApiImpl: Making get test results request to ${ApiList.BASE_URL}$url")
        println("AishouApiImpl: Test ID: $testId")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        val result = handleApi<BaseResponse<TestResultResponse>> {
            client.get(url) {
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }

        when (result) {
            is ApiResult.Success -> {
                println("AishouApiImpl: Test results response successful: ${result.data}")
                println("AishouApiImpl: Result type: ${result.data.data?.resultType}")
            }
            is ApiResult.Error -> {
                println("AishouApiImpl: Test results request failed with error: ${result.message}")
            }
            is ApiResult.Exception -> {
                println("AishouApiImpl: Test results request failed with exception: ${result.exception.message}")
            }
        }

        return result
    }

    override suspend fun reprocessTestResults(testId: String): ApiResult<BaseResponse<TestResultResponse>> {
        val url = ApiList.getReprocessTestUrl(testId)
        println("AishouApiImpl: Making reprocess test results request to ${ApiList.BASE_URL}$url")
        println("AishouApiImpl: Test ID: $testId")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        val result = handleApi<BaseResponse<TestResultResponse>> {
            client.post(url) {
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
                contentType(ContentType.Application.Json)
            }
        }

        when (result) {
            is ApiResult.Success -> {
                println("AishouApiImpl: Reprocess test results response successful: ${result.data}")
                println("AishouApiImpl: Result type: ${result.data.data?.resultType}")
                println("AishouApiImpl: Has personalized insights: ${!result.data.data?.soloResult?.personalizedInsights.isNullOrBlank()}")
            }
            is ApiResult.Error -> {
                println("AishouApiImpl: Reprocess test results request failed with error: ${result.message}")
            }
            is ApiResult.Exception -> {
                println("AishouApiImpl: Reprocess test results request failed with exception: ${result.exception.message}")
            }
        }

        return result
    }

    override suspend fun getTests(): ApiResult<BaseResponse<List<Test>>> {
        println("AishouApiImpl: Making get tests request to ${ApiList.BASE_URL}${ApiList.GET_TESTS}")

        return handleApi {
            client.get(ApiList.GET_TESTS) {
                getAuthHeader()?.let { authHeader ->
                    header(HttpHeaders.Authorization, authHeader)
                }
            }
        }
    }

    override suspend fun getQuizQuestions(testId: String, version: Int): ApiResult<BaseResponse<List<QuizQuestion>>> {
        val url = ApiList.getQuizQuestionsUrl(testId, version)
        println("AishouApiImpl: Making get quiz questions request to ${ApiList.BASE_URL}$url")

        return handleApi {
            client.get(url) {
                getAuthHeader()?.let { authHeader ->
                    header(HttpHeaders.Authorization, authHeader)
                }
            }
        }
    }

    override suspend fun submitQuiz(testId: String, version: Int, submission: QuizSubmissionRequest): ApiResult<BaseResponse<Submission>> {
        val url = ApiList.getQuizSubmissionUrl(testId, version)
        val fullUrl = "${ApiList.BASE_URL}$url"
        println("AishouApiImpl: Making submit quiz request to $fullUrl")
        println("AishouApiImpl: Generated URL path: $url")
        println("AishouApiImpl: Request body: $submission")
        println("AishouApiImpl: testId=$testId, version=$version")

        // Serialize to JSON to see the actual JSON being sent
        val json = Json { ignoreUnknownKeys = true }
        val jsonString = json.encodeToString(QuizSubmissionRequest.serializer(), submission)
        println("AishouApiImpl: JSON being sent: $jsonString")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")
        println("AishouApiImpl: Auth header value: $authHeader")

        return handleApi {
            client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(submission)
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }
    }

    override suspend fun createInvite(inviteRequest: InviteCreateRequest): ApiResult<InviteResponse> {
        println("AishouApiImpl: Making create invite request to ${ApiList.BASE_URL}${ApiList.POST_CREATE_INVITE}")
        println("AishouApiImpl: Request body: $inviteRequest")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        return handleApi {
            client.post(ApiList.POST_CREATE_INVITE) {
                contentType(ContentType.Application.Json)
                setBody(inviteRequest)
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }
    }

    override suspend fun registerPush(pushReq: PushReq): ApiResult<BaseResponse<Unit>> {
        println("AishouApiImpl: Making push register request to ${ApiList.BASE_URL}${ApiList.POST_PUSH_REGISTER}")
        println("AishouApiImpl: Request body: $pushReq")

        val authHeader = getAuthHeader()
        println("AishouApiImpl: Auth header present: ${authHeader != null}")

        return handleApi {
            client.post(ApiList.POST_PUSH_REGISTER) {
                contentType(ContentType.Application.Json)
                setBody(pushReq)
                authHeader?.let {
                    header(HttpHeaders.Authorization, it)
                }
            }
        }
    }
}