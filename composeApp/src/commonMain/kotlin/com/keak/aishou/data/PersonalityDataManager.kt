package com.keak.aishou.data

import com.keak.aishou.data.api.PersonalityUpdateRequest
import com.keak.aishou.data.api.Submission
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.screens.onboarding.MBTIType
import com.keak.aishou.screens.onboarding.ZodiacSign

class PersonalityDataManager(
    private val apiService: AishouApiService
) {
    private var collectedZodiac: String? = null
    private var collectedMBTI: String? = null

    fun setZodiacSign(zodiacSign: ZodiacSign) {
        collectedZodiac = zodiacSign.name
    }

    fun setMBTIType(mbtiType: MBTIType) {
        collectedMBTI = mbtiType.code
    }

    fun setMBTIFromTestResult(submission: Submission) {
        // MBTI sonucunu submission'dan çıkar
        // personalizedInsights'ta veya başka bir field'da MBTI sonucu olabilir
        submission.personalizedInsights?.let { insights ->
            // Örnek: "Your MBTI type is INTJ" gibi text'ten MBTI çıkar
            val mbtiPattern = "\\b([A-Z]{4})\\b".toRegex()
            val matchResult = mbtiPattern.find(insights)
            matchResult?.groupValues?.get(1)?.let { mbtiCode ->
                collectedMBTI = mbtiCode
            }
        }
    }

    suspend fun updatePersonality(): ApiResult<*> {
        val request = PersonalityUpdateRequest(
            mbtiType = collectedMBTI,
            zodiacSign = collectedZodiac
        )

        return apiService.updatePersonality(request)
    }

    fun hasCompleteData(): Boolean {
        return collectedZodiac != null && collectedMBTI != null
    }

    fun reset() {
        collectedZodiac = null
        collectedMBTI = null
    }
}