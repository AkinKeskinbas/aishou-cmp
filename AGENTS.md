# Repository Guidelines

## Project Structure & Module Organization
`composeApp` is the primary Kotlin Multiplatform module. Shared Compose UI and domain logic live in `composeApp/src/commonMain/kotlin`; keep feature folders lightweight and colocate previews with their composables. Android-specific implementations (including resources under `res/`) belong in `composeApp/src/androidMain`, while iOS shims and expect/actual bindings reside in `composeApp/src/iosMain`. The `iosApp/iosApp` directory is the Swift entry point—use it for platform setup, app delegates, and any SwiftUI glue code. Reserve `composeApp/src/nativeInterop/cinterop` for native bindings to avoid scattering platform calls elsewhere.

## Build, Test & Development Commands
- `./gradlew :composeApp:assembleDebug` — compile an Android debug APK.
- `./gradlew :composeApp:installDebug` — push the debug build to a connected device or emulator.
- `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64` — produce the Apple framework used by the simulator target; open `iosApp/iosApp.xcworkspace` in Xcode afterward.
- `./gradlew :composeApp:check` — run unit tests, lint, and verification tasks.
Always execute commands from the project root so Gradle picks up shared configuration in `settings.gradle.kts` and `gradle.properties`.

## Coding Style & Naming Conventions
Follow the official Kotlin style guide: 4-space indentation, trailing commas for multiline builders, and expressive `val` usage. Name composables and screen-level functions in `PascalCase`, view models with the `*ViewModel` suffix, and DataStore or Ktor clients in `camelCase`. Group feature code by screen or domain inside `commonMain` to keep multiplatform boundaries explicit. Run `./gradlew :composeApp:lint` before reviews if you touch Android-specific files; lint is configured to ignore noisy Compose warnings but still surfaces regressions.

## Testing Guidelines
Add shared unit tests under `composeApp/src/commonTest/kotlin` using the `kotlin.test` APIs already provided in Gradle dependencies. Mirror production package names so expect/actual pairs remain discoverable. Prefer behaviour-focused names such as `FriendInvitationViewModelTest`. Use emulator-friendly fakes for network and DataStore; instrumented tests are currently out of scope. Execute `./gradlew :composeApp:check` (or the narrower `:composeApp:testDebugUnitTest` once added) before pushing.

## Commit & Pull Request Guidelines
Commit messages today mix merge commits and descriptive sentences. Transition to a single-line imperative summary (≤72 chars) with optional scope, e.g. `Add invite sheet analytics hook`. Use dedicated branches like `feature/friendlist-invite` or `fix/ios-navigation`. PRs should include: problem statement, high-level solution, manual test notes (devices, simulators), and updated screenshots when altering UI flows. Link tracking issues in the description and request at least one peer review before merging.

## Platform Setup Tips
Develop with JDK 11+, an up-to-date Android SDK, and CocoaPods installed (`pod install` runs automatically through Gradle when needed). Keep Firebase or OneSignal configuration files out of version control unless explicitly shared; reference them via `local.properties` or CI secrets. When adding new native dependencies, document any brew/SDK prerequisites in the PR to keep onboarding smooth.
