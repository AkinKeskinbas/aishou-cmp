package com.keak.aishou.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegister(
    @SerialName("revenueCatId")
    val revenueCatId: String,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("photoUrl")
    val photoUrl: String? = null,
    @SerialName("lang")
    val lang: String = "en",
    @SerialName("platform")
    val platform: String? = null,  // "ios" or "android"
    @SerialName("isAnonymous")
    val isAnonymous: Boolean = true,
    @SerialName("isPremium")
    val isPremium: Boolean = true
)