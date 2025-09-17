package com.keak.aishou.domain.repositoryimpl

import com.keak.aishou.domain.mapper.QuickTestHomeMapper
import com.keak.aishou.domain.repository.QuicTestScreenRepository

class QuickTestRepositoryImpl(
    private val quickTestHomeMapper: QuickTestHomeMapper
) : QuicTestScreenRepository {
    override fun getHomeContent() {
        TODO("Not yet implemented")
    }
}