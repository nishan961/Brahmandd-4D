# Brahmandd-4D

Brahmandd-4D is a production-grade Android live wallpaper application focused on high-performance 4D motion, gyroscope parallax, and modular wallpaper categories.

## Architecture overview
- Native Kotlin on Android SDK 34 with API 26+ compatibility
- Wallpaper engine built on `WallpaperService` with a locked 60 FPS render loop
- Sensor fusion pipeline using `TYPE_ROTATION_VECTOR` with accelerometer fallback
- SharedPreferences-backed configuration state manager for live category switching
- Glassmorphism dashboard and live preview activity built with Jetpack Compose

## Features
- Deep Space Nebula category
- Cybernetic Grid category
- AMOLED Quantum category
- Shiva • Kailash devotional scene with layered gyroscope parallax
- Choose a photo from the device to use as a tilt-responsive Shiva wallpaper
- Remote wallpapers loaded from the published catalog at startup, with cached image and depth maps for offline use
- Per-pixel depth-map gyro parallax for remote wallpapers; bright depth values shift more than dark values
- Direct apply pipeline through `WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER`
- In-app preview activity for gyroscope-driven testing
- Adaptive launcher icon assets for modern Android devices

## Build instructions
1. Install JDK 17 and Android SDK. The project expects:
   - `JAVA_HOME` pointing to a JDK 17 installation
   - `ANDROID_HOME=/opt/android-sdk`
2. Install required Android packages:
   - `platform-tools`
   - `platforms;android-34`
   - `build-tools;34.0.0`
3. Run:

```bash
source /usr/local/sdkman/bin/sdkman-init.sh
sdk use java 17.0.20-tem
export ANDROID_HOME=/opt/android-sdk
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
./gradlew assembleDebug --no-configuration-cache
```

## Project structure
- `app/src/main/java/com/brahmandd/fourd` – application code
- `app/src/main/res` – resources, theme, XML, and adaptive icon assets
- `app/src/main/AndroidManifest.xml` – manifest and wallpaper service registration

## Notes
The implementation is intentionally lightweight, uses minimal third-party dependencies, and keeps sensor listeners and render loops synchronized with wallpaper lifecycle events to reduce idle battery drain.
