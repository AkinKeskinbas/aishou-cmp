package com.keak.aishou.domain.repositoryimpl

import com.keak.aishou.data.api.Test
import com.keak.aishou.domain.mapper.QuickTestHomeMapper
import com.keak.aishou.domain.repository.QuicTestScreenRepository
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.response.BaseResponse

class QuickTestRepositoryImpl(
    private val quickTestHomeMapper: QuickTestHomeMapper,
    private val apiService: AishouApiService
) : QuicTestScreenRepository {

    override suspend fun getTests(): ApiResult<BaseResponse<List<Test>>> {
        return apiService.getTests()
    }
}