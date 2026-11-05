# Cakes

An Android app that loads a list of cakes from a remote endpoint and displays them

## My approach

The main idea was not to overengineer and keep to the scope. There are a couple of key
assumptions/decisions that I have made:

1) Deduping logic uses the data class methods for comparing. UI uses only name as a key and assuming
   those are unique
2) One state object is suitable here because of pull to refresh at the very least instead of sealed
   states
3) For the sake of simplicity I am doing minimal logging for debugging purposes, but in production
   code I have UX and Logging/Error handling as priority
4) Repository doesn't inject a dispatcher because Retrofit uses its own thread pool so it's main
   thread safe. In most of the cases I would inject for testing purposes
5) Added UseCase to encapsulate the business logic of the sorting/deduping and make it testable in
   isolation.
6) Lightweight DI and fake tests to avoid bloating code with Hilt/Dagger setup, extra mock libs
7) UI is literally minimal, but the components should be correct and easily updated to match any
   design in a couple of minutes. I would use https://stitch.withgoogle.com/ to create a design and
   then adjust the UI/design system accordingly, but I would keep that as the next immediate step.
   The doc says that is important and I would say getting it to this stage is important, the rest is
   just a matter of opinion and the UX you want to present and there.

## AI Usage

I planned to write the code myself mostly, which I did all the way up to the last couple of commits,
where I sped up the development with a bit of automation. I created the framework for the unit test
fake repo and then asked claude to finish up the viewmodel test. I did a quick scan with Claude to
see if there were any issues I have forgotten or missed and fixed those up. I followed the doc as
the guide to write all the code and left the readme creation to Claude as well to summarise the
result and create references.

## Requirements

### Must have

| #  | Requirement                                                   | Status | Where                                                                 |
|----|---------------------------------------------------------------|:------:|-----------------------------------------------------------------------|
| 1  | Load and display a list of cakes when the app starts          |   ✅   | `CakeScreenViewModel` loads in `init`                                 |
| 1a | Remove duplicate entries                                      |   ✅   | `GetCakesUseCase`                                                     |
| 1b | Order entries by name                                         |   ✅   | `GetCakesUseCase`                                                     |
| 1c | Display image and title for each entry                        |   ✅   | `CakeListItem` with Coil `AsyncImage`                                 |
| 1d | Display a divider between each entry                          |   ✅   | `HorizontalDivider` in the list                                       |
| 2  | Show the cake description in a popup when an entry is clicked |   ✅   | Material 3 `ModalBottomSheet`                                         |
| 3  | Provide a refresh option that reloads the list                |   ✅   | Material 3 `PullToRefreshBox`                                         |
| 4  | Display an error message if the list cannot be loaded         |   ✅   | Full-screen error state, or a snackbar if a list is already on screen |

### Nice to have

| # | Requirement                                           | Status | Notes                                                              |
|---|-------------------------------------------------------|:------:|--------------------------------------------------------------------|
| 1 | Handle orientation changes without reloading the list |   ✅   | State lives in a `ViewModel`, which survives configuration changes |
| 2 | Offer a retry option when an error is shown           |   ✅   | Retry on both the error state and the refresh snackbar             |
| 3 | Animate in list items                                 |   🟡   | `animateItem()` animates list changes, not the first appearance    |

### Technical requirements

| # | Requirement                     | How                                                                           |
|---|---------------------------------|-------------------------------------------------------------------------------|
| 1 | Mobile technology of choice     | Android, Kotlin, Jetpack Compose                                              |
| 2 | Common third-party libraries    | Retrofit, OkHttp, kotlinx.serialization, Koin, Coil                           |
| 3 | Idiomatic architectural pattern | MVVM with a Clean Architecture style split into data, domain and presentation |
| 4 | TODOs for future additions      | Inline `TODO` comments, summarised below                                      |
| 5 | Git commits show the approach   | One commit per layer or feature, see `git log`                                |

---

## Architecture

```
network/       Retrofit service and DTOs for the raw cakes endpoint
data/          Repository implementation and DTO to domain mappers
domain/        Cake model, repository contract and GetCakesUseCase (dedupe + sort)
common/        Result type and safeRunCatching, shared across layers
presentation/  ViewModel, UI state and Compose screen
di/            Koin module wiring everything together
```

Data flows in one direction:

```
CakeService ──▶ CakeRepositoryImpl ──▶ GetCakesUseCase ──▶ CakeScreenViewModel ──▶ CakeListScreen
   (DTOs)          (Result<Cake>)       (distinct + sort)     (StateFlow<UiState>)     (Compose)
```

- **Business rules live in the use case.** Deduplication relies on `Cake` being a data class, and
  sorting is by title.
- **The repository never throws.** Network and parsing failures are mapped to `Result.Error`, so
  callers handle errors as values. Coroutine cancellation is still rethrown.
- **The ViewModel exposes a single `StateFlow<CakeListUiState>`.** The screen is a function of that
  state, which keeps the list on screen during a refresh.
- **Each screen state has a clear UI.** Loading, error, empty and content each have their own view.
  A failed refresh keeps the list and shows a snackbar, which the ViewModel marks as shown so it
  doesn't reappear after rotation.

### Data source

The app treats this raw file as its API endpoint:

```
https://raw.githubusercontent.com/Waracle/mobile-coding-test-api/refs/heads/main/cakes
```

---

## Tech stack

| Area                 | Library                                   |
|----------------------|-------------------------------------------|
| UI                   | Jetpack Compose, Material 3               |
| Dependency injection | Koin                                      |
| Networking           | Retrofit, OkHttp with logging interceptor |
| Serialization        | kotlinx.serialization                     |
| Image loading        | Coil                                      |
| Testing              | JUnit 4, kotlinx-coroutines-test          |

---

## Running the project

Open the project in Android Studio and run the `app` configuration, or build from the command line:

```bash
./gradlew :app:installDebug
```

Run the unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

---

## Unit tests

Tests are fast JVM unit tests. They use a hand-written `FakeCakeRepository` instead of a mocking
library, and a `MainDispatcherRule` to replace `Dispatchers.Main` so `viewModelScope` runs in tests.

| Required scenario                                                      | Covered by                                       |
|------------------------------------------------------------------------|--------------------------------------------------|
| Load succeeds: cakes shown with duplicates removed and sorting applied | `CakeScreenViewModelTest`, `GetCakesUseCaseTest` |
| Load fails: error is presented                                         | `CakeScreenViewModelTest`, `GetCakesUseCaseTest` |

Extra coverage includes a failed refresh with cakes on screen, an empty response, reload after an
error, cake selection for the bottom sheet, and the `safeRunCatching` helper.

---

## With more time

These match the `TODO` comments in the code.

- **Logging.** Add a logger abstraction in the repository so errors can be sent to Crashlytics,
  OpenTelemetry or similar.
- **Data validation.** Validate fields and image URLs in the mapper, dropping or repairing bad
  entries.
- **Loading and refresh UX.** Unify the initial load, refresh and error states, for example with
  shimmer placeholders.
- **Item animations.** Animate items on first appearance, not only on list changes.
- **Details sheet.** Give the description popup a proper design with the image and title.
