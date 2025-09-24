package com.keak.aishou.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import aishou.composeapp.generated.resources.*

/**
 * Utility object for accessing string resources in a type-safe manner
 */
object StringResources {

    // Common UI Elements
    @Composable
    fun backArrow() = stringResource(Res.string.back_arrow)

    @Composable
    fun retryButton() = stringResource(Res.string.retry_button)

    @Composable
    fun dismissButton() = stringResource(Res.string.dismiss_button)

    @Composable
    fun backContentDescription() = stringResource(Res.string.back_content_description)

    @Composable
    fun userFallbackName(userId: String) = stringResource(Res.string.user_fallback_name, userId)

    // Notifications Screen
    @Composable
    fun notificationsTitle() = stringResource(Res.string.notifications_title)

    @Composable
    fun testInvitationsCount(count: Int) = stringResource(Res.string.test_invitations_count, count)

    @Composable
    fun noTestInvitationsTitle() = stringResource(Res.string.no_test_invitations_title)

    @Composable
    fun noTestInvitationsSubtitle() = stringResource(Res.string.no_test_invitations_subtitle)

    @Composable
    fun friendRequestsCount(count: Int) = stringResource(Res.string.friend_requests_count, count)

    @Composable
    fun noFriendRequestsTitle() = stringResource(Res.string.no_friend_requests_title)

    @Composable
    fun noFriendRequestsSubtitle() = stringResource(Res.string.no_friend_requests_subtitle)

    @Composable
    fun wantsToBeFriends() = stringResource(Res.string.wants_to_be_friends)

    @Composable
    fun loadingNotifications() = stringResource(Res.string.loading_notifications)

    @Composable
    fun testInviteFrom(senderName: String) = stringResource(Res.string.test_invite_from, senderName)

    @Composable
    fun testInviteMbti(mbti: String) = stringResource(Res.string.test_invite_mbti, mbti)

    // Invite Screen
    @Composable
    fun testInvitationTitle() = stringResource(Res.string.test_invitation_title)

    @Composable
    fun loadingInvitation() = stringResource(Res.string.loading_invitation)

    @Composable
    fun errorLoadingInvitation() = stringResource(Res.string.error_loading_invitation)

    @Composable
    fun takeTestCompareResults() = stringResource(Res.string.take_test_compare_results)

    @Composable
    fun acceptingInviteLoading() = stringResource(Res.string.accepting_invite_loading)

    @Composable
    fun rejectInvite() = stringResource(Res.string.reject_invite)

    @Composable
    fun invitationFromLabel() = stringResource(Res.string.invitation_from_label)

    @Composable
    fun testDetailsLabel() = stringResource(Res.string.test_details_label)

    @Composable
    fun premiumFeatureTitle() = stringResource(Res.string.premium_feature_title)

    @Composable
    fun premiumCompareDescription(senderName: String, testTitle: String) =
        stringResource(Res.string.premium_compare_description, senderName, testTitle)

    @Composable
    fun upgradeToPremiumLabel() = stringResource(Res.string.upgrade_to_premium_label)

    @Composable
    fun premiumFeatureTakeTests() = stringResource(Res.string.premium_feature_take_tests)

    @Composable
    fun premiumFeatureCompareResults() = stringResource(Res.string.premium_feature_compare_results)

    @Composable
    fun premiumFeatureUnlimitedInvites() = stringResource(Res.string.premium_feature_unlimited_invites)

    @Composable
    fun upgradeToPremiumButton() = stringResource(Res.string.upgrade_to_premium_button)

    // Friends Screen
    @Composable
    fun friendsTitle() = stringResource(Res.string.friends_title)

    @Composable
    fun friendNamePlaceholder() = stringResource(Res.string.friend_name_placeholder)

    @Composable
    fun friendsListCount(count: Int) = stringResource(Res.string.friends_list_count, count)

    @Composable
    fun noFriendsMessage() = stringResource(Res.string.no_friends_message)

    // Profile Screen
    @Composable
    fun profileTitle() = stringResource(Res.string.profile_title)

    @Composable
    fun profileHeader() = stringResource(Res.string.profile_header)

    @Composable
    fun proBadge() = stringResource(Res.string.pro_badge)

    @Composable
    fun yourStatsTitle() = stringResource(Res.string.your_stats_title)

    @Composable
    fun testAcedLabel() = stringResource(Res.string.test_aced_label)

    @Composable
    fun dayStreakLabel() = stringResource(Res.string.day_streak_label)

    @Composable
    fun unlockDestinyTitle() = stringResource(Res.string.unlock_destiny_title)

    @Composable
    fun unlimitedTestsLabel() = stringResource(Res.string.unlimited_tests_label)

    @Composable
    fun deepInsightsLabel() = stringResource(Res.string.deep_insights_label)

    @Composable
    fun unlockThirdEyeButton() = stringResource(Res.string.unlock_third_eye_button)

    @Composable
    fun testsCompletedLabel() = stringResource(Res.string.tests_completed_label)

    @Composable
    fun getPremiumLabel() = stringResource(Res.string.get_premium_label)

    @Composable
    fun upgradeButton() = stringResource(Res.string.upgrade_button)

    @Composable
    fun premiumMemberStatus() = stringResource(Res.string.premium_member_status)

    @Composable
    fun loadingProfile() = stringResource(Res.string.loading_profile)

    // MBTI Preference Screen
    @Composable
    fun mbtiYourPersonality() = stringResource(Res.string.mbti_your_personality)

    @Composable
    fun mbtiRecommended() = stringResource(Res.string.mbti_recommended)

    @Composable
    fun mbtiTakeFullTest() = stringResource(Res.string.mbti_take_full_test)

    @Composable
    fun mbtiTestDuration() = stringResource(Res.string.mbti_test_duration)

    @Composable
    fun mbtiComprehensiveAssessment() = stringResource(Res.string.mbti_comprehensive_assessment)

    @Composable
    fun mbtiKnowMyType() = stringResource(Res.string.mbti_know_my_type)

    @Composable
    fun mbtiSelectFromList() = stringResource(Res.string.mbti_select_from_list)

    @Composable
    fun mbtiSelectYourType() = stringResource(Res.string.mbti_select_your_type)

    @Composable
    fun mbtiStartFullTest() = stringResource(Res.string.mbti_start_full_test)

    @Composable
    fun mbtiContinue() = stringResource(Res.string.mbti_continue)

    // MBTI Types
    @Composable
    fun getMbtiTypeName(mbtiCode: String): String {
        return when (mbtiCode) {
            "INTJ" -> stringResource(Res.string.mbti_intj_name)
            "INTP" -> stringResource(Res.string.mbti_intp_name)
            "ENTJ" -> stringResource(Res.string.mbti_entj_name)
            "ENTP" -> stringResource(Res.string.mbti_entp_name)
            "INFJ" -> stringResource(Res.string.mbti_infj_name)
            "INFP" -> stringResource(Res.string.mbti_infp_name)
            "ENFJ" -> stringResource(Res.string.mbti_enfj_name)
            "ENFP" -> stringResource(Res.string.mbti_enfp_name)
            "ISTJ" -> stringResource(Res.string.mbti_istj_name)
            "ISFJ" -> stringResource(Res.string.mbti_isfj_name)
            "ESTJ" -> stringResource(Res.string.mbti_estj_name)
            "ESFJ" -> stringResource(Res.string.mbti_esfj_name)
            "ISTP" -> stringResource(Res.string.mbti_istp_name)
            "ISFP" -> stringResource(Res.string.mbti_isfp_name)
            "ESTP" -> stringResource(Res.string.mbti_estp_name)
            "ESFP" -> stringResource(Res.string.mbti_esfp_name)
            else -> mbtiCode
        }
    }

    @Composable
    fun getMbtiTypeDescription(mbtiCode: String): String {
        return when (mbtiCode) {
            "INTJ" -> stringResource(Res.string.mbti_intj_description)
            "INTP" -> stringResource(Res.string.mbti_intp_description)
            "ENTJ" -> stringResource(Res.string.mbti_entj_description)
            "ENTP" -> stringResource(Res.string.mbti_entp_description)
            "INFJ" -> stringResource(Res.string.mbti_infj_description)
            "INFP" -> stringResource(Res.string.mbti_infp_description)
            "ENFJ" -> stringResource(Res.string.mbti_enfj_description)
            "ENFP" -> stringResource(Res.string.mbti_enfp_description)
            "ISTJ" -> stringResource(Res.string.mbti_istj_description)
            "ISFJ" -> stringResource(Res.string.mbti_isfj_description)
            "ESTJ" -> stringResource(Res.string.mbti_estj_description)
            "ESFJ" -> stringResource(Res.string.mbti_esfj_description)
            "ISTP" -> stringResource(Res.string.mbti_istp_description)
            "ISFP" -> stringResource(Res.string.mbti_isfp_description)
            "ESTP" -> stringResource(Res.string.mbti_estp_description)
            "ESFP" -> stringResource(Res.string.mbti_esfp_description)
            else -> ""
        }
    }

    // Home Screen Additional
    @Composable
    fun me() = stringResource(Res.string.me)

    @Composable
    fun unknown() = stringResource(Res.string.unknown)

    @Composable
    fun welcomeToAishou() = stringResource(Res.string.welcome_to_aishou)

    @Composable
    fun welcomeBackWithName(name: String) = stringResource(Res.string.welcome_back_with_name, name)

    @Composable
    fun welcomeBack() = stringResource(Res.string.welcome_back)

    @Composable
    fun cosmicPersonalityDiscover() = stringResource(Res.string.cosmic_personality_discover)

    @Composable
    fun launchCountDaysActive(launchCount: String, daysActive: String) =
        stringResource(Res.string.launch_count_days_active, launchCount, daysActive)

    @Composable
    fun launchCountOnly(launchCount: String) = stringResource(Res.string.launch_count_only, launchCount)

    @Composable
    fun connectWithFriends() = stringResource(Res.string.connect_with_friends)

    @Composable
    fun notificationsContentDescription() = stringResource(Res.string.notifications_content_description)

    @Composable
    fun settingsContentDescription() = stringResource(Res.string.settings_content_description)

    @Composable
    fun userMatchHome() = stringResource(Res.string.user_match_home)

    // Zodiac Selection Screen
    @Composable
    fun whatsYourZodiacSign() = stringResource(Res.string.whats_your_zodiac_sign)

    @Composable
    fun selectZodiacToContinue() = stringResource(Res.string.select_zodiac_to_continue)

    // Zodiac Signs Dates
    @Composable
    fun getZodiacDates(zodiacName: String): String {
        return when (zodiacName.lowercase()) {
            "aries" -> stringResource(Res.string.zodiac_aries_dates)
            "taurus" -> stringResource(Res.string.zodiac_taurus_dates)
            "gemini" -> stringResource(Res.string.zodiac_gemini_dates)
            "cancer" -> stringResource(Res.string.zodiac_cancer_dates)
            "leo" -> stringResource(Res.string.zodiac_leo_dates)
            "virgo" -> stringResource(Res.string.zodiac_virgo_dates)
            "libra" -> stringResource(Res.string.zodiac_libra_dates)
            "scorpio" -> stringResource(Res.string.zodiac_scorpio_dates)
            "sagittarius" -> stringResource(Res.string.zodiac_sagittarius_dates)
            "capricorn" -> stringResource(Res.string.zodiac_capricorn_dates)
            "aquarius" -> stringResource(Res.string.zodiac_aquarius_dates)
            "pisces" -> stringResource(Res.string.zodiac_pisces_dates)
            else -> ""
        }
    }

    // Error Messages
    @Composable
    fun failedToLoadProfile() = stringResource(Res.string.failed_to_load_profile)

    @Composable
    fun exceptionLoadingProfile() = stringResource(Res.string.exception_loading_profile)

    @Composable
    fun unexpectedErrorLoadingProfile() = stringResource(Res.string.unexpected_error_loading_profile)

    @Composable
    fun failedToLoadFriendRequests() = stringResource(Res.string.failed_to_load_friend_requests)

    @Composable
    fun failedToLoadInvitationData() = stringResource(Res.string.failed_to_load_invitation_data)

    @Composable
    fun failedToLoadTestInformation() = stringResource(Res.string.failed_to_load_test_information)

    @Composable
    fun failedToLoadFriends() = stringResource(Res.string.failed_to_load_friends)

    @Composable
    fun failedToLoadTestResults() = stringResource(Res.string.failed_to_load_test_results)

    @Composable
    fun exceptionLoadingTestResults() = stringResource(Res.string.exception_loading_test_results)

    // Friends Screen
    @Composable
    fun friendNameLabel() = stringResource(Res.string.friend_name_label)

    @Composable
    fun dismiss() = stringResource(Res.string.dismiss)

    @Composable
    fun unknownErrorOccurred() = stringResource(Res.string.unknown_error_occurred)

    @Composable
    fun failedToSendFriendRequest() = stringResource(Res.string.failed_to_send_friend_request)

    @Composable
    fun failedToRemoveFriend() = stringResource(Res.string.failed_to_remove_friend)

    // Notifications Screen
    @Composable
    fun failedToAcceptFriendRequest() = stringResource(Res.string.failed_to_accept_friend_request)

    @Composable
    fun failedToRejectFriendRequest() = stringResource(Res.string.failed_to_reject_friend_request)

    // Match Screen
    @Composable
    fun errorPrefix(error: String) = stringResource(Res.string.error_prefix, error)

    @Composable
    fun testResults() = stringResource(Res.string.test_results)

    @Composable
    fun soloTestResults() = stringResource(Res.string.solo_test_results)

    @Composable
    fun matchAnalysis() = stringResource(Res.string.match_analysis)

    @Composable
    fun friendFallback() = stringResource(Res.string.friend_fallback)

    @Composable
    fun matchScore() = stringResource(Res.string.match_score)

    @Composable
    fun vs() = stringResource(Res.string.vs)

    @Composable
    fun yourScore() = stringResource(Res.string.your_score)

    @Composable
    fun shareWithFriends() = stringResource(Res.string.share_with_friends)

    @Composable
    fun sendToFriend() = stringResource(Res.string.send_to_friend)

    @Composable
    fun matchSummary() = stringResource(Res.string.match_summary)

    @Composable
    fun mbtiCompatibility() = stringResource(Res.string.mbti_compatibility)

    @Composable
    fun strengths() = stringResource(Res.string.strengths)

    @Composable
    fun challenges() = stringResource(Res.string.challenges)

    @Composable
    fun zodiacCompatibility() = stringResource(Res.string.zodiac_compatibility)

    @Composable
    fun detailedExplanations() = stringResource(Res.string.detailed_explanations)

    @Composable
    fun detailedAnalysis() = stringResource(Res.string.detailed_analysis)
}