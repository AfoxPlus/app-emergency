# Copilot Instructions

## Build, test, and lint

Run Gradle through the checked-in wrapper from the repository root:

```bash
# Build the debug APK
./gradlew :app:assembleDebug

# Run all local JVM unit tests
./gradlew :app:testDebugUnitTest

# Run one unit test class
./gradlew :app:testDebugUnitTest --tests 'com.afoxplus.emergency.presentation.onboarding.OnboardingViewModelTest'

# Run one unit test method
./gradlew :app:testDebugUnitTest --tests 'com.afoxplus.emergency.ExampleUnitTest.addition_isCorrect'

# Run instrumentation and Compose UI tests on a connected emulator/device
./gradlew :app:connectedDebugAndroidTest

# Run Android lint
./gradlew :app:lintDebug
```

The app targets Java 11 and uses Kotlin 2.3, Compose, Hilt/KSP, and Navigation 3. Dependency and plugin versions are centralized in `gradle/libs.versions.toml`.

## Architecture

This is a single-module Android app (`:app`) in package `com.afoxplus.emergency`.

- `EmergencyApplication` initializes Hilt. `MainActivity` is the sole activity and Hilt entry point; it reads the persisted onboarding flag to create the initial Navigation 3 back stack.
- Navigation uses serializable object keys (`Onboarding`, `Home`) and a Compose `SnapshotStateList<Any>`. `AppNavigation` maps every key to a `NavEntry`; onboarding completion clears the stack and replaces it with `Home`.
- Features are organized under `presentation/<feature>`. The onboarding feature separates its `@HiltViewModel` and immutable `OnboardingUiState` from its stateful `OnboardingRoute` and stateless `OnboardingScreen`, both defined in `OnboardingScreen.kt`. Routes collect state with `collectAsStateWithLifecycle()` and pass callbacks down to stateless UI.
- Persistence is expressed as an interface (`OnboardingPreferences`), implemented with `SharedPreferences`, bound in `di/PreferencesModule`, and replaced with `FakeOnboardingPreferences` in JVM tests. Keep Android-dependent persistence out of ViewModels.

## Project conventions

- Use Hilt constructor injection for ViewModels and provide Android framework-backed implementations from `di` modules. `MainActivity` uses field injection only because it is an Android framework class.
- Model screen state as immutable data exposed through a read-only `StateFlow`; update the private flow with `update { ... }`. Event functions validate inputs rather than allowing invalid UI state.
- Reuse `AppemergencyTheme`, `EmergencyColors`, `EmergencyTypography`, `AppSpacing`, `AppShapes`, and the `Emergency*` composables in `ui/theme` instead of introducing screen-local visual tokens or restyling Material components.
- Existing Compose UI tests target stable semantic `testTag` values (for example, `onboarding_primary_action`), while ViewModel tests use the in-memory preferences fake. Preserve or add tags when interactive UI needs test coverage.
- Onboarding currently has two content sources: `onboardingPages` supplies ViewModel state and unit-test/preview data, while `rememberOnboardingPages()` builds equivalent localized data from `res/values/strings.xml`. Keep them synchronized when changing onboarding copy, or explicitly wire the localized source into the route/state flow.
- Repository-specific Android guidance is available as skills under `.github/skills/<category>/<skill-name>/SKILL.md`, grouped by category:
  - `architecture/`: android-architecture, android-data-layer, android-viewmodel
  - `build_and_tooling/`: android-gradle-logic
  - `concurrency_and_networking/`: android-coroutines, android-retrofit, kotlin-concurrency-expert
  - `performance/`: compose-performance-audit, gradle-build-performance
  - `testing_and_automation/`: android-emulator-skill, android-testing
  - `ui/`: android-accessibility, coil-compose, compose-navigation, compose-ui
