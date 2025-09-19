import SwiftUI
import ComposeApp
import OneSignalFramework

// OneSignal User State Observer
class UserStateObserver: NSObject, OSUserStateObserver {
    func onUserStateDidChange(state: OSUserChangedState) {
        print("OneSignal iOS: User state changed")

        if let onesignalId = state.current.onesignalId {
            print("OneSignal iOS: ✅ Current OneSignal ID: \(onesignalId)")
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
		RevenueCatInit.shared.configure(apiKey: PlatformKeys.shared.revenuecatApiKey,appUserId: nil)

		// Enable verbose logging for debugging
		OneSignal.Debug.setLogLevel(.LL_VERBOSE)

		// Initialize OneSignal with App ID from PlatformKeys
		OneSignal.initialize(PlatformKeys.shared.oneSignalAppId, withLaunchOptions: nil)

		// Request notification permission
		OneSignal.Notifications.requestPermission({ accepted in
			print("OneSignal iOS: Notification permission \(accepted ? "granted" : "denied")")

			// Log OneSignal User ID after permission
			if let userId = OneSignal.User.onesignalId {
				print("OneSignal iOS: User ID obtained: \(userId)")
			} else {
				print("OneSignal iOS: User ID not yet available")
			}
		}, fallbackToSettings: false)

		// Set up user state observer
		OneSignal.User.addObserver(UserStateObserver())

		print("OneSignal iOS: Successfully initialized with real SDK!")
	}
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
