import SwiftUI
import ComposeApp
import OneSignalFramework

// Deep Link Handler - Shared between notification clicks and URL schemes
class DeepLinkHandler {
    static let shared = DeepLinkHandler()

    private init() {}

    func handleDeepLink(url: URL) {
        print("DeepLinkHandler: Handling deep link: \(url.absoluteString)")

        // Handle different URL patterns
        let host = url.host?.lowercased()
        let path = url.path

        print("DeepLinkHandler: Host: \(host ?? "nil"), Path: \(path)")

        // Handle aishou.site and aishou.app domains
        if host == "aishou.site" || host == "aishou.app" {
            print("DeepLinkHandler: ✅ Aishou domain detected - opening in app")
            handleAishouPath(url: url, path: path)
        } else if url.scheme == "aishou" {
            print("DeepLinkHandler: ✅ Custom scheme detected - opening in app")
            handleAishouPath(url: url, path: path)
        } else {
            print("DeepLinkHandler: ⚠️ Unknown URL scheme/host - ignoring")
        }
    }

    private func handleAishouPath(url: URL, path: String) {
        print("DeepLinkHandler: Handling Aishou path: \(path)")

        // Handle friends request: /friends/request?senderId=xxx&senderName=John%20Doe
        if path.contains("/friends/request") || path.contains("friends/request") {
            print("DeepLinkHandler: Friends request detected")

            let senderId = extractURLParameter(from: url, parameter: "senderId")
            let senderName = extractURLParameter(from: url, parameter: "senderName")

            if let senderId = senderId, let senderName = senderName {
                print("DeepLinkHandler: ✅ Navigating to friend request screen")
                print("DeepLinkHandler: SenderId: \(senderId), SenderName: \(senderName)")
                RouterBridgeIOS.shared.goToFriendRequest(senderId: senderId, senderName: senderName)
            } else {
                print("DeepLinkHandler: ⚠️ Missing friend request parameters")
                RouterBridgeIOS.shared.goToNotifications() // Fallback to notifications
            }
        }
        // Handle test results: /test/something
        else if path.contains("/test/") {
            print("DeepLinkHandler: Test result detected")

            // Extract test ID or other parameters if needed
            let testId = extractPathParameter(from: path, after: "/test/")
            print("DeepLinkHandler: ✅ Navigating to test screen")
            print("DeepLinkHandler: TestId: \(testId ?? "unknown")")
            if let testId = testId {
                RouterBridgeIOS.shared.goToTestResult(testId: testId)
            } else {
                RouterBridgeIOS.shared.goToHome()
            }
        }
        // Handle invites: /invite/123?senderId=xxx&testId=yyy
        else if path.contains("/invite/") {
            print("DeepLinkHandler: Invite detected")

            let inviteId = extractPathParameter(from: path, after: "/invite/")
            let senderId = extractURLParameter(from: url, parameter: "senderId")
            let testId = extractURLParameter(from: url, parameter: "testId")
            let testTitle = extractURLParameter(from: url, parameter: "testTitle")

            if let inviteId = inviteId, let senderId = senderId, let testId = testId {
                print("DeepLinkHandler: ✅ Navigating to invite screen")
                print("DeepLinkHandler: InviteId: \(inviteId), SenderId: \(senderId), TestId: \(testId)")
                RouterBridgeIOS.shared.goToInvite(inviteId: inviteId, senderId: senderId, testId: testId, testTitle: testTitle ?? "Test")
            } else {
                print("DeepLinkHandler: ⚠️ Missing invite parameters")
                RouterBridgeIOS.shared.goToHome()
            }
        }
        else {
            print("DeepLinkHandler: Unknown path, navigating to home")
            RouterBridgeIOS.shared.goToHome()
        }
    }

    private func extractURLParameter(from url: URL, parameter: String) -> String? {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
              let queryItems = components.queryItems else {
            return nil
        }

        return queryItems.first { $0.name == parameter }?.value?.removingPercentEncoding
    }

    private func extractPathParameter(from path: String, after prefix: String) -> String? {
        guard let range = path.range(of: prefix) else { return nil }
        let afterPrefix = String(path[range.upperBound...])
        return afterPrefix.components(separatedBy: "?").first?.components(separatedBy: "/").first
    }
}

// OneSignal Notification Click Listener
class NotificationClickListener: NSObject, OSNotificationClickListener {
    func onClick(event: OSNotificationClickEvent) {
        print("OneSignal iOS: Notification clicked")
        print("OneSignal iOS: Notification data: \(event.notification.additionalData ?? [:])")

        // Handle the click - redirect to app instead of website
        if let url = event.notification.launchURL {
            print("OneSignal iOS: Original launch URL: \(url)")
            // Parse and handle the URL instead of opening it externally
            if let deepLinkURL = URL(string: url) {
                print("OneSignal iOS: Handling notification URL as deeplink")
                handleDeepLinkFromNotification(url: deepLinkURL)
            }
        }

        // Handle additional data for navigation
        if let additionalData = event.notification.additionalData {
            print("OneSignal iOS: Handling notification data in app: \(additionalData)")
            // TODO: Add custom navigation logic based on additionalData
        }
    }

    private func handleDeepLinkFromNotification(url: URL) {
        print("OneSignal iOS: Processing notification deeplink: \(url.absoluteString)")
        // Call the shared deep link handler
        DeepLinkHandler.shared.handleDeepLink(url: url)
    }
}

// OneSignal User State Observer
class UserStateObserver: NSObject, OSUserStateObserver {
    func onUserStateDidChange(state: OSUserChangedState) {
        print("OneSignal iOS: User state changed")

        if let onesignalId = state.current.onesignalId {
            print("OneSignal iOS: ✅ Current OneSignal ID: \(onesignalId)")
            // Update Kotlin bridge
            OneSignalBridgeIOS.shared.setOneSignalId(id: onesignalId)
        }

        // Get push subscription ID directly from OneSignal.User
        if let pushId = OneSignal.User.pushSubscription.id {
            print("OneSignal iOS: ✅ Current Push Subscription ID: \(pushId)")
            // Update Kotlin bridge
            OneSignalBridgeIOS.shared.setPushSubscriptionId(id: pushId)
        }

        if let externalId = state.current.externalId {
            print("OneSignal iOS: External ID: \(externalId)")
        } else {
            print("OneSignal iOS: No external ID set")
        }
    }
}

@main
struct iOSApp: App {
	init() {
		do {
			print("iOS App: Starting initialization...")

			// Initialize RevenueCat with safe string handling
			let revenueCatKey = PlatformKeys.shared.revenuecatApiKey
			print("iOS App: RevenueCat key: \(revenueCatKey)")
			RevenueCatInit.shared.configure(apiKey: revenueCatKey, appUserId: nil)

			// Get OneSignal App ID safely
			let oneSignalAppId = PlatformKeys.shared.oneSignalAppId
			print("iOS App: OneSignal App ID: \(oneSignalAppId)")

			// Enable verbose logging for debugging
			OneSignal.Debug.setLogLevel(.LL_VERBOSE)

			// Initialize OneSignal with App ID from PlatformKeys
			OneSignal.initialize(oneSignalAppId, withLaunchOptions: nil)

			// Request notification permission with safer handling and delay
			DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
				OneSignal.Notifications.requestPermission({ accepted in
					print("OneSignal iOS: Notification permission \(accepted ? "granted" : "denied")")

					// Log both OneSignal Player ID and Push Subscription ID with delay
					DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
						if let playerId = OneSignal.User.onesignalId {
							print("OneSignal iOS: ✅ Player ID (for push): \(playerId)")
							// Send to Kotlin bridge
							OneSignalBridgeIOS.shared.setOneSignalId(id: playerId)
						} else {
							print("OneSignal iOS: ⚠️ Player ID not yet available")
						}

						if let subscriptionId = OneSignal.User.pushSubscription.id {
							print("OneSignal iOS: Subscription ID: \(subscriptionId)")
							// Send to Kotlin bridge
							OneSignalBridgeIOS.shared.setPushSubscriptionId(id: subscriptionId)
						} else {
							print("OneSignal iOS: Subscription ID not yet available")
						}
					}
				}, fallbackToSettings: false)
			}

			// Set up user state observer
			OneSignal.User.addObserver(UserStateObserver())

			// Set up notification click handler
			OneSignal.Notifications.addClickListener(NotificationClickListener())

			// Log OneSignal ID format for debugging and update bridge
			DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
				if let playerId = OneSignal.User.onesignalId {
					print("OneSignal iOS: Final Player ID format check: \(playerId)")
					print("OneSignal iOS: Is UUID format: \(playerId.contains("-") && playerId.count > 20)")
					// Final update to bridge
					OneSignalBridgeIOS.shared.setOneSignalId(id: playerId)
				}

				if let subscriptionId = OneSignal.User.pushSubscription.id {
					print("OneSignal iOS: Final Subscription ID check: \(subscriptionId)")
					// Final update to bridge
					OneSignalBridgeIOS.shared.setPushSubscriptionId(id: subscriptionId)
				}
			}

			print("OneSignal iOS: ✅ Successfully initialized with real SDK!")
		} catch {
			print("iOS App: ❌ Error during initialization: \(error)")
		}
	}
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    print("iOS App: Received deep link: \(url)")
                    DeepLinkHandler.shared.handleDeepLink(url: url)
                }
        }
    }

}
