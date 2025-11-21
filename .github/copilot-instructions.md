# Copilot Instructions for Screen Lock

## Project Overview
This is an Android application called "Screen Lock" that allows users to quickly lock their device screen using an Accessibility Service. The app is built with Kotlin and Jetpack Compose.

## Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3
- **Build System**: Gradle with Kotlin DSL (.kts files)
- **Minimum SDK**: API 30 (Android 11)
- **Target SDK**: API 36
- **Compile SDK**: API 36
- **Java Version**: 11

## Project Structure
- `/app/src/main/java/com/example/screenlock/` - Main source code
  - `MainActivity.kt` - Main UI activity with Compose UI for enabling accessibility service
  - `LockNowActivity.kt` - No-UI launcher activity that immediately locks the screen if service is enabled
  - `ScreenLockAccessibilityService.kt` - Accessibility service that performs the screen lock action
  - `/ui/theme/` - Compose theme files (Color.kt, Type.kt, Theme.kt)
- `/app/src/main/AndroidManifest.xml` - App manifest with activities and service declarations
- `/app/build.gradle.kts` - App-level build configuration
- `/build.gradle.kts` - Project-level build configuration
- `/settings.gradle.kts` - Gradle settings

## Key Architectural Patterns
1. **Accessibility Service Pattern**: Uses Android's AccessibilityService to perform the GLOBAL_ACTION_LOCK_SCREEN action
2. **Broadcast Receiver**: Internal broadcast communication between activities and the accessibility service
3. **Jetpack Compose**: Declarative UI with state management using `mutableStateOf` and lifecycle observers
4. **No-UI Launcher**: LockNowActivity serves as the main launcher with Theme.NoDisplay for immediate action

## Code Style & Conventions
- Follow Kotlin official code style (`kotlin.code.style=official` in gradle.properties)
- Use Jetpack Compose best practices
- Prefer Kotlin language features (data classes, sealed classes, extension functions)
- Use meaningful variable names and follow camelCase convention
- Add logging for important service lifecycle events using Android's Log class

## Build & Testing
- Build command: `./gradlew build`
- Run tests: `./gradlew test`
- Android instrumented tests: `./gradlew connectedAndroidTest`
- Test files located in:
  - `app/src/test/` - Unit tests
  - `app/src/androidTest/` - Instrumented tests

## Android-Specific Guidelines
1. **Permissions**: This app requires Accessibility Service permission (declared in manifest)
2. **API Level Considerations**: Handle API level differences (e.g., registerReceiver with RECEIVER_NOT_EXPORTED flag for API 33+)
3. **Lifecycle Management**: Properly register/unregister broadcast receivers in service lifecycle
4. **Activity Lifecycle**: Use lifecycle observers to update UI state when returning from settings
5. **ProGuard**: Release builds use code shrinking and minification (proguard-rules.pro)

## Common Tasks
- When adding new Compose components, ensure they follow Material3 design guidelines
- When modifying the accessibility service, test with the service enabled/disabled
- For UI changes, provide preview composables with `@Preview` annotation
- When handling broadcasts, consider API level differences for receiver registration
- Always use `finishAndRemoveTask()` when dismissing activities that shouldn't appear in recents

## Dependencies Management
Dependencies are managed through Gradle version catalogs. Common dependencies include:
- AndroidX Core KTX
- AndroidX Lifecycle Runtime KTX
- Jetpack Compose (BOM, UI, Material3, Tooling)
- Testing: JUnit, Espresso, Compose UI Test

## Security Considerations
- Accessibility services are sensitive permissions - document their usage clearly
- Don't store sensitive data in SharedPreferences without encryption
- Follow Android security best practices for broadcast receivers
- Use `RECEIVER_NOT_EXPORTED` flag for internal broadcasts on API 33+

## Testing Guidance
- Write unit tests for business logic
- Use Compose UI tests for UI components
- Test accessibility service functionality with instrumented tests
- Verify behavior across different Android API levels (minimum is API 30)

## Notes for Contributors
- This is a utility app focused on simplicity and reliability
- Maintain minimal UI - the primary interaction should be quick screen locking
- Consider battery impact when modifying the accessibility service
- Test on real devices, as accessibility services may behave differently on emulators
