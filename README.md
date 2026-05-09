# Number Base Converter — Android App

A fully offline Android app built with **Kotlin + Jetpack Compose**.
Converts between Decimal, Binary, Octal, and Hexadecimal with step-by-step working.

---

## Features
- **Converter screen** — real-time conversion as you type, copy-to-clipboard
- **Steps screen** — full step-by-step working (repeated division + remainder trace)
- **Reference screen** — 0–15 quick lookup table + conversion method formulas
- **About screen** — developer and app information
- **100% offline** — no internet permission at all
- **Dark AMOLED theme** — easy on the eyes

---

## Requirements
- **Android Studio** Hedgehog (2023.1.1) or newer — download free from https://developer.android.com/studio
- **JDK 17** (bundled with Android Studio)
- **Android device or emulator** running Android 7.0+ (API 24+)

---

## How to Build & Run

### Option A — Run on Android Emulator (easiest)
1. Open **Android Studio**
2. Choose **"Open"** and select this `NumberConverter` folder
3. Wait for Gradle to sync (first time takes 2–5 minutes, downloads dependencies)
4. Click **▶ Run** (green play button) or press `Shift+F10`
5. Android Studio will create an emulator automatically if you don't have one

### Option B — Run on your real Android phone
1. On your phone: go to **Settings → About Phone** → tap **Build Number** 7 times → enables Developer Options
2. Go to **Settings → Developer Options** → enable **USB Debugging**
3. Connect phone via USB cable
4. In Android Studio: select your phone from the device dropdown → click **▶ Run**

### Option C — Build an APK to share/install manually
1. In Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`
3. Copy APK to your phone and install (you may need to allow "Install from unknown sources" in Settings)

---

## Project Structure
```
NumberConverter/
├── app/src/main/java/com/numberconverter/app/
│   ├── MainActivity.kt          ← App entry point + bottom navigation
│   ├── ConversionEngine.kt      ← All conversion logic (pure Kotlin)
│   └── ui/
│       ├── theme/
│       │   └── Theme.kt         ← Colors, dark theme
│       └── screens/
│           ├── ConverterScreen.kt   ← Main converter tab
│           ├── StepsScreen.kt       ← Step-by-step tab
│           ├── ReferenceScreen.kt   ← Reference table tab
│           └── AboutScreen.kt       ← Developer & app info
├── app/build.gradle.kts         ← App dependencies
├── build.gradle.kts             ← Root build file
├── settings.gradle.kts          ← Project settings
└── gradle/libs.versions.toml   ← Dependency versions
```

---

## Tech Stack
| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material3) |
| Navigation | Navigation Compose |
| Min Android | 7.0 (API 24) |
| Target Android | 15 (API 35) |
| Internet | ❌ None required |

---

## Troubleshooting

**Gradle sync fails?**
→ Make sure you have a working internet connection for the first sync (downloads libraries once)
→ File → Invalidate Caches → Restart

**"SDK not found" error?**
→ Android Studio → Settings → Android SDK → Install SDK 35

**Device not detected?**
→ Try a different USB cable, or use the emulator instead

---

## License & Copyright

**© 2026 saad khan. All rights reserved.**
Developed by **saad khan**.
