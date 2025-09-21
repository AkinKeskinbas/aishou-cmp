package com.keak.aishou.response

import com.keak.aishou.data.api.Test
import kotlinx.serialization.Serializable

@Serializable
data class TestsData(
    val data: List<Test>
)