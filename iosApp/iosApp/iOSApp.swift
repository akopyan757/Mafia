import SwiftUI

@main
struct iOSApp: App {
    init() {
        Helper.doInitKoin()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}