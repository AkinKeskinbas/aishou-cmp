package com.keak.aishou.network

import com.keak.aishou.response.BaseResponse
import com.keak.aishou.response.TokenResponse


interface AishouApiService {
    suspend fun getToken(): ApiResult<BaseResponse<TokenResponse>>

}