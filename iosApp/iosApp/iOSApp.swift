import SwiftUI
import Foundation
import ComposeApp
import OneSignalFramework

// Global utility function for URL conversion
func convertToCustomSchemeIfNeeded(url: URL) -> URL {
    // If it's already custom scheme, return as-is
    if url.scheme == "aishou" {
        return url
    }

    // Convert HTTPS URLs to custom scheme
    if url.scheme == "https" && (url.host == "aishou.site" || url.host == "aishou.app") {
        let path = url.path.hasPrefix("/") ? String(url.path.dropFirst()) : url.path
        let query = url.query.map { "?\($0)" } ?? ""

        if let customURL = URL(string: "aishou://\(path)\(query)") {
            print("URL Converter: Converted \(url.absoluteString) to \(customURL.absoluteString)")
            return customURL
        }
    }

    // Return original if conversion fails
    return url
}

// Deep Link Handler - Shared between notification clicks and URL schemes
class DeepLinkHandler {
    static let shared = DeepLinkHandler()

    private init() {}
    private let processingQueue = DispatchQueue.main
    private var isProcessing = false
    private var queuedURL: URL?
    private var lastHandledURL: (value: String, date: Date)?

    func handleDeepLink(url: URL) {
        processingQueue.async { [weak self] in
            self?.enqueueHandling(url: url)
        }
    }

    private func enqueueHandling(url: URL) {
        if isProcessing {
            queuedURL = url
            print("DeepLinkHandler: Queued deeplink while another is processing: \(url.absoluteString)")
            return
        }
        process(url: url)
    }

    private func process(url: URL) {
        guard shouldHandle(url: url) else {
            print("DeepLinkHandler: Ignoring duplicate deeplink: \(url.absoluteString)")
            return
        }

        isProcessing = true
        defer {
            isProcessing = false
            if let next = queuedURL {
                queuedURL = nil
                enqueueHandling(url: next)
            }
        }

        print("DeepLinkHandler: Handling deeplink: \(url.absoluteString)")

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
        print("DeepLinkHandler: Full URL: \(url.absoluteString)")

        // Use the new unified approach - send URL to DeepLinkCoordinator
        dispatchToRouter {
            RouterBridgeIOS.shared.handleDeepLink(url: url.absoluteString)
        }
    }

    private func dispatchToRouter(_ action: @escaping () -> Void) {
        // Dispatch with a tiny delay to avoid re-entrancy during gesture handling
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05, execute: action)
    }

    private func shouldHandle(url: URL) -> Bool {
        let absolute = url.absoluteString
        if let last = lastHandledURL, last.value == absolute {
            let elapsed = Date().timeIntervalSince(last.date)
            if elapsed < 1.0 {
                return false
            }
        }
        lastHandledURL = (value: absolute, date: Date())
        return true
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

        // Convert HTTPS URLs to custom scheme for consistent handling
        let processedURL = convertToCustomSchemeIfNeeded(url: url)
        print("OneSignal iOS: Processed URL: \(processedURL.absoluteString)")

        // Call the shared deep link handler
        DeepLinkHandler.shared.handleDeepLink(url: processedURL)
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

                    // Convert HTTPS URLs to custom scheme for consistent handling
                    let processedURL = convertToCustomSchemeIfNeeded(url: url)
                    print("iOS App: Processed URL: \(processedURL.absoluteString)")

                    DeepLinkHandler.shared.handleDeepLink(url: processedURL)
                }
        }
    }

}
