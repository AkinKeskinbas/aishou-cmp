package com.keak.aishou.notifications

interface OneSignalManager {
    /**
     * Initialize OneSignal with the app ID
     */
    fun initialize(appId: String)

    /**
     * Get the current OneSignal user ID
     */
    suspend fun getOneSignalId(): String?

    /**
     * Set user consent for notifications
     */
    fun setUserConsent(hasConsent: Boolean)

    /**
     * Request notification permission (for platforms that require it)
     */
    suspend fun requestNotificationPermission(): Boolean

    /**
     * Set external user ID (your app's user ID)
     */
    fun setExternalUserId(externalId: String)

    /**
     * Add tags to the user
     */
    fun addTags(tags: Map<String, String>)

    /**
     * Remove tags from the user
     */
    fun removeTags(tagKeys: List<String>)

    /**
     * Add listener for OneSignal user state changes
     */
    fun addUserStateChangeListener(listener: (String?) -> Unit)

    /**
     * Remove user state change listener
     */
    fun removeUserStateChangeListener()
}