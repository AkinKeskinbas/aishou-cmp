package com.keak.aishou.domain.usecase

import com.keak.aishou.data.api.Test
import com.keak.aishou.domain.repository.QuicTestScreenRepository
import com.keak.aishou.network.ApiResult
import com.keak.aishou.response.BaseResponse

class QuickTestHomeUseCase(
    private val quicTestScreenRepository: QuicTestScreenRepository
) {

    suspend fun getTests(): ApiResult<BaseResponse<List<Test>>> {
        return quicTestScreenRepository.getTests()
    }
}