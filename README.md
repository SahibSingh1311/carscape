# CarScape
A traffic-jam sliding puzzle game for Android built using Kotlin and Jetpack Compose. The app follows the MVVM architecture with a multi-module clean architecture setup, and uses Firebase and DataStore for data persistence.

## Download
[![Download APK](https://img.shields.io/badge/Download-APK-3DDC84?logo=android&logoColor=white&style=for-the-badge)](https://github.com/SahibSingh1311/CarScape/releases/latest)

Download the latest APK from the [Releases](https://github.com/SahibSingh1311/CarScape/releases/latest) page and install it on your Android device. You may need to allow installs from unknown sources.

## Features
- **MVVM + Clean Architecture:** Multi-module setup (domain, data, core, feature modules) that keeps concerns separated and the app easy to maintain.
- **Three Game Modes:** Daily, Timed and Casual, each with its own level pool and progress tracking.
- **Power-ups:** Hammer/Crusher (clears a vehicle), Time Freeze and Add Time.
- **Coins and Hearts:** Coins are spent in the Marketplace; hearts regenerate automatically every 15 minutes.
- **Daily Challenge:** Locked once per day with a live countdown to the next one.
- **Rewarded Ads:** AdMob rewarded ads shown through a floating bubble.
- **Remote Config Pricing:** Marketplace prices can be tuned without an app update.
- **Anonymous Sign-in:** Players can start right away, no account needed.
- **Screens:**
  - *Home Screen:*
    - Mode selection (Daily, Timed, Casual).
    - Shows coins and hearts.
    - Daily challenge countdown.
  - *Game Screen:*
    - Sliding puzzle board drawn with Compose Canvas.
    - Power-up buttons and timer.
    - Inventory of the power-ups you own.

## Screens
### Home Screen

<img width="300" alt="image" src="https://github.com/user-attachments/assets/caf73b5d-6c5b-4c79-956d-96da0e8a30c7" />


  - *Modes:* Pick between Daily, Timed and Casual.
  - *Wallet:* Displays current coins and hearts.
  - *Daily Challenge:* Locked after completion, with a live countdown until the next one unlocks.

### Game Screen

<img width="300" alt="image" src="https://github.com/user-attachments/assets/bc14c032-7743-48d7-8b55-fc156218b795" />


  - *Board:* Slide vehicles to clear the path for your car.
  - *Power-ups:*
    - Hammer: removes a vehicle from the board.
    - Time Freeze: pauses the timer.
    - Add Time: adds extra seconds.
  - *Inventory:* Shows the power-ups you currently own and how many of each you have left.
  - *Timed Mode:* Coins are rewarded based on the time remaining.

### Marketplace

<img width="300" alt="image" src="https://github.com/user-attachments/assets/4e5176dc-6ad1-41f4-a8eb-8cd406df8226" />

  - *Power-up Pricing:* Driven by Firebase Remote Config.
  - *Rewarded Ads:* Watch an ad to earn rewards.

### App UI

Gameplay:

[screen-recording-20261111-163711_iVUZtv22.webm](https://github.com/user-attachments/assets/52fbc8ff-de34-4e79-a657-7b294d15d5b9)



Using a power-up:


https://github.com/user-attachments/assets/d05cf6d0-ae3d-44a6-826b-b4dbf830784c



Daily challenge lock:



https://github.com/user-attachments/assets/65a794f9-4b8a-4b29-88d4-4a00a0f59dec



## Tech Stack
- Kotlin, Jetpack Compose
- MVVM, StateFlow, Hilt
- Jetpack DataStore
- Firebase: Firestore, Anonymous Auth, Remote Config
- Google AdMob
- Gradle version catalog
- JUnit, Mockito, Compose UI tests

## Project Structure
```
CarScape
├── app                  # Entry point, navigation, DI wiring
├── feature
│   ├── home             # Home screen
│   └── game             # Game board and power-ups
├── domain               # Models, repository interfaces, use cases
├── data                 # Repository implementations (Firestore, DataStore, Auth, Remote Config)
└── core
    ├── common           # Shared utilities
    └── designsystem     # Theme and reusable Compose components
```

## Testing
- Unit tests for the data layer repositories and ViewModels using Mockito.
- Compose instrumented tests for the Home screen.
- Shared fakes live in `testFixtures` source sets.

```
./gradlew test
./gradlew connectedAndroidTest
```

## Getting Started
### Prerequisites
  - Android Studio
  - Kotlin
  - A Firebase project with Firestore, Anonymous Auth and Remote Config enabled

### Installation
1. Clone the repository
```
git clone https://github.com/SahibSingh1311/CarScape
```
2. Open the project in Android Studio.
3. Add your own `google-services.json` to the `app/` folder.
4. Add your AdMob IDs (use Google's test IDs while developing).
5. Build and run the project on an emulator or a physical device.

## Roadmap
- Play Store release
- Leaderboard
- Offline levels for Casual mode

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request with your improvements.

## Acknowledgments
- Thanks to the Jetpack Compose and Android community for their tutorials and guidance.
- This app was built largely with the help of [Claude](https://claude.ai) by Anthropic, with additional help from [Grok](https://grok.com) by xAI.
- Sound effects and music from [Mixkit](https://mixkit.co/).
- Images and artwork generated with [Gemini](https://gemini.google.com/) by Google.
