package com.keak.aishou.domain.repository

import com.keak.aishou.data.api.Test
import com.keak.aishou.network.ApiResult
import com.keak.aishou.response.BaseResponse

interface QuicTestScreenRepository {
    suspend fun getTests(): ApiResult<BaseResponse<List<Test>>>
}