package com.keak.aishou.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object DeepLinkCoordinator {
    private val _deepLinks = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val deepLinks: SharedFlow<String> = _deepLinks

    suspend fun emit(url: String) {
        println("DeepLinkCoordinator: emit url: $url")
        _deepLinks.emit(url)
    }

    fun tryEmit(url: String): Boolean {
        val emitted = _deepLinks.tryEmit(url)
        println("DeepLinkCoordinator: tryEmit url: $url result=$emitted")
        return emitted
    }
}
