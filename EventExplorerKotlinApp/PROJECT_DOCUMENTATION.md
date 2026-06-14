# EventExplorerKotlinApp Documentation

## Purpose

EventExplorerKotlinApp is a Kotlin-based Android application that connects to an MQTT broker and displays real-time event messages in a list UI powered by Jetpack Compose. It focuses on structured data visualization and simple event management.

## Key features

- **MQTT Integration:** Uses `com.github.hannesa2:paho.mqtt.android:4.4.1` for background-safe MQTT connectivity.
- **Structured Data Support:** Parses JSON payloads to extract metadata like severity and source.
- **Dynamic UI:**
    - Color-coded cards based on severity (Critical: Red, Warning: Gold, Info: Blue).
    - Responsive layout showing Title, Metadata (Source/Date), and Description.
- **Event Management:** Checkbox-based selection for bulk deletion of received events.
- **Resilient Connectivity:** Automatic reconnection and status tracking.

## Technology stack

- Kotlin
- Android SDK 35
- Jetpack Compose (Material 3)
- Hannesa2 MQTT client (modernized Paho fork)
- Gradle Kotlin DSL

## How it works

1. `MainActivity` hosts `EventExplorerScreen()`, which manages a `mutableStateListOf` of events.
2. The `MqttAndroidClient` connects to `broker.hivemq.com` on port 1883.
3. Upon receiving a message on `event/explorer`:
    - The payload is parsed as a `JSONObject`.
    - An `EventItem` is created and prepended to the list.
4. `EventCard` composables display each item with:
    - A `Checkbox` for selection.
    - `severity`-based container and text coloring.
5. A `TopAppBar` displays a `Delete` icon whenever `selectedEvents` is not empty.

## Project files

- `README.md` — User-facing overview and run instructions.
- `PROJECT_DOCUMENTATION.md` — Technical details and architecture.
- `app/build.gradle.kts` — Dependency management (notably `compileSdk = 35`).
- `app/src/main/java/com/example/eventexplorer/MainActivity.kt` — Core logic including `EventItem` model, MQTT callbacks, and UI components.

## Troubleshooting

- **Build Errors:** Ensure `compileSdk` is set to 35. Version 4.5 of the MQTT library requires API 36; we use 4.4.1 for compatibility.
- **No Data:** Check network access and use an MQTT tool (like MQTT Explorer) to verify messages are being published to `event/explorer`.

## Recent Improvements

- Added JSON parsing for structured event messages.
- Implemented severity-based color coding.
- Added multi-select deletion capability.
- Updated to Android API 35 compatibility.
