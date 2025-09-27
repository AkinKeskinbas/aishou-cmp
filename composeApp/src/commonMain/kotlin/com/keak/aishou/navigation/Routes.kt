package com.keak.aishou.navigation

sealed class Routes(val route: String = "") {
    data object Splash : Routes("Splash")
    data object ReAuth : Routes("ReAuth")
    data object OnBoarding : Routes("OnBoarding")
    data object OnBoardingSecond : Routes("OnBoardingSecond")
    data object OnBoardingThird : Routes("OnBoardingThird")
    data object OnBoardingFourth : Routes("OnBoardingFourth")
    data object ZodiacSelection : Routes("ZodiacSelection")
    data object MBTIPreference : Routes("MBTIPreference")
    data object Home : Routes("Home")
    data object AllResults : Routes("AllResults")
    data object QuicTests : Routes("QuicTests")
    data object PersonelResult : Routes("PersonelResult")
    data object TestResult : Routes("TestResults/{testID}") {
        fun passTestID(testID: String): String {
            return "TestResults/$testID"
        }
    }
    data object QuizScreen : Routes("QuizScreen/{quizID}?senderId={senderId}&inviteId={inviteId}") {
        fun passQuizID(quizID: String): String {
            return "QuizScreen/$quizID"
        }

        fun passQuizIDWithSender(quizID: String, senderId: String): String {
            return "QuizScreen/$quizID?senderId=$senderId"
        }

        fun passQuizIDWithInvite(quizID: String, senderId: String, inviteId: String): String {
            return "QuizScreen/$quizID?senderId=$senderId&inviteId=$inviteId"
        }
    }
    data object QuickQuizScreen : Routes("QuickQuizScreen")
    data object Friends : Routes("Friends")
    data object Notifications : Routes("Notifications")
    data object FriendRequest : Routes("Friends/Request?senderId={senderId}&senderName={senderName}") {
        fun passFriendRequestData(senderId: String, senderName: String): String {
            // URL encode sender name for safe passing
            val encodedName = senderName.replace(" ", "%20").replace("&", "%26")
            return "Friends/Request?senderId=$senderId&senderName=$encodedName"
        }
    }
    data object Invite : Routes("Invite/{inviteId}?senderId={senderId}&testId={testId}&testTitle={testTitle}&senderName={senderName}&senderMbti={senderMbti}") {
        fun passInviteData(inviteId: String, senderId: String, testId: String, testTitle: String, senderName: String = "Unknown User", senderMbti: String? = null): String {
            // URL encode parameters for safe passing
            val encodedTitle = testTitle.replace(" ", "%20").replace("&", "%26")
            val encodedSenderName = senderName.replace(" ", "%20").replace("&", "%26")
            val mbtiParam = senderMbti?.let { "&senderMbti=$it" } ?: "&senderMbti="
            return "Invite/$inviteId?senderId=$senderId&testId=$testId&testTitle=$encodedTitle&senderName=$encodedSenderName$mbtiParam"
        }
    }

    data object Paywall : Routes("Paywall?returnTo={returnTo}") {
        fun withReturnTo(returnTo: String): String {
            // URL encode return path for safe passing
            val encodedReturn = returnTo.replace("/", "%2F").replace("?", "%3F").replace("&", "%26")
            return "Paywall?returnTo=$encodedReturn"
        }
    }
    data object UserMatch : Routes("UserMatch/{testID}?friendId={friendId}") {
        fun passTestID(testID: String): String {
            return "UserMatch/$testID"
        }

        fun passTestIDWithFriend(testID: String, friendId: String): String {
            return "UserMatch/$testID?friendId=$friendId"
        }
    }
    data object Profile : Routes("Profile")
    data object ThankYou : Routes("ThankYou?isFromInvite={isFromInvite}") {
        fun passFromInvite(isFromInvite: Boolean): String {
            return "ThankYou?isFromInvite=$isFromInvite"
        }
    }
    data object MBTIResult : Routes("MBTIResult")
}