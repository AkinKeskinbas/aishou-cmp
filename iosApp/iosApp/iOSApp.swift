import SwiftUI
import ComposeApp
@main
struct iOSApp: App {
	init() {
		RevenueCatInit.shared.configure(apiKey: PlatformKeys.shared.revenuecatApiKey,appUserId: nil)
	}
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
