Docker Android emulator (for Expo)

This guide shows how to run an Android emulator inside Docker and connect your local Expo project to it.

Prerequisites

- Docker Desktop (Windows) with WSL2 backend recommended.
- `adb` available on host (you can install Platform Tools or use Android Studio later). If not installed, Docker image exposes port 5555 for ADB over TCP.

Quick steps

1. Start the emulator container

```powershell
cd EventExplorerApp
docker-compose up -d
```

2. Wait for the emulator to boot. You can watch the noVNC UI at http://localhost:6080/ (noVNC login: `root`/`root` for budtmo images).

3. Connect host ADB to the emulator

```powershell
adb connect localhost:5555
adb devices
```

You should see a device listed as `localhost:5555`.

4. Start Expo on the host and run on the connected device

```powershell
cd EventExplorerApp
npx expo start
# press 'a' to open on Android; or run 'npx expo run:android' to build/run
```

Notes & caveats

- Running an emulator inside Docker on Windows may be less reliable than using Android Studio directly because of virtualization and GPU acceleration issues.
- The budtmo/docker-android images run QEMU-based emulators and expose ADB and noVNC. They work well for CI and testing, but may be slower than native emulators.
- If you prefer, I can instead create a container that only provides the Android SDK and ADB (no GUI) and run the emulator on your host via Android Studio.

Want me to start the docker emulator now and try connecting `adb` from this machine? If yes I'll attempt to run `docker-compose up -d` and then `adb connect` automatically.