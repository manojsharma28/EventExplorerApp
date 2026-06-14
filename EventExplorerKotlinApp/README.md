# EventExplorerKotlinApp

A Kotlin Android mobile app that connects to MQTT and displays incoming event messages in a Compose-based UI.

## Overview

This app uses Jetpack Compose for the UI and the `hannesa2` MQTT Android client library to receive messages from a broker.

### Key Features
- **Real-time Event List:** Scrolling list of incoming MQTT messages.
- **JSON Parsing:** Automatically parses event details (Title, Severity, Source, Date, Description).
- **Severity Coding:** Events are color-coded based on severity (Critical, Warning, Info).
- **Selection & Deletion:** Select multiple events using checkboxes and delete them with a single tap.

## Prerequisites

- Android Studio installed
- Android SDK for API 35
- JDK 17 or newer
- Windows terminal or PowerShell for command-line build support

## Run in Android Studio

1. Open `d:\MyData\EventExplorerApp\EventExplorerKotlinApp` in Android Studio.
2. Allow Gradle to sync and install any missing SDK components.
3. Select a device or emulator.
4. Click `Run` or press `Shift+F10`.

## Run from the command line

Open PowerShell in the project root and run:

```powershell
cd d:\MyData\EventExplorerApp\EventExplorerKotlinApp
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

## MQTT configuration

- Broker: `tcp://broker.hivemq.com:1883` (Standard MQTT port 1883)
- Topic: `event/explorer`

### Supported Payload Format
The app is optimized for JSON payloads like:
```json
{
  "id": 114,
  "severity": "Critical",
  "title": "Disk Usage",
  "date": "2026-06-07",
  "source": "Server01",
  "description": "Usage exceeded 90%"
}
```

## Notes

- The app requires `INTERNET` permission to connect to MQTT.
- If the app does not receive messages, verify that the MQTT topic is active and reachable.
