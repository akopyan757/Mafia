# Mafia Game Host App (KMM)

## Description
The **Mafia Game Host App** is a Kotlin Multiplatform Mobile (KMM) project designed for managing Mafia game sessions. It helps the host track players, assign roles, and manage the various phases of the game. Currently, the app is designed to run on desktop platforms using shared KMM logic, with plans to extend support to Android and iOS in the future.

## Features
- Manage player roles and track game phases
- Assign random roles (Mafia, Civilians, Doctor, etc.)
- Track eliminated players and game progress
- Desktop support using KMM

## Technologies Used
- **Kotlin Multiplatform Mobile (KMM)**: Shared logic for potential mobile and desktop platforms
- **Coroutines**: For managing asynchronous tasks
- **Koin**: For dependency injection
- **Ktor**: For handling network requests
- **SQLDelight**: For local database management

## How to Build
1. Clone the repository:
    ```bash
    git clone https://github.com/akopyan757/Mafia.git
    ```
2. Open the project in IntelliJ IDEA or Android Studio with the KMM plugin installed.
3. Sync the project and run it on the desktop.

## Future Improvements
- Add mobile support for Android and iOS platforms
- Add more customizable game rules
- Support for hosting online multiplayer sessions
- Improve UI and animations for better user experience

## Contributing
Feel free to fork the repository and submit pull requests. Feedback and suggestions are welcome!
