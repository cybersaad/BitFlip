# 📱 BitFlip

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Offline](https://img.shields.io/badge/100%25-Offline-blue?style=for-the-badge)

BitFlip v2.0 is a modern, fast, and completely offline Android application designed to convert numbers between different bases with precision and clarity. Built with **Kotlin** and **Jetpack Compose**, this app provides not just the final result, but also the logical steps taken to reach it.

---

## ✨ Features

- **⚡ Real-time Conversion:** Instant results as you type. No "Convert" button required.
- **🔢 Fractional Number Support:** Full support for floating-point numbers (e.g., 12.625, A.8) across all bases.
- **📚 Integrated Step-by-Step Working:** Full mathematical working shown automatically beneath your results (Repeated Division for integers, Multiplication for fractions).
- **📋 One-Tap Copy:** Quickly copy results to your clipboard.
- **📖 Quick Reference:** Built-in lookup table for Binary/Decimal/Hex equivalents (0-15).
- **🌑 AMOLED Dark Theme:** Elegant, battery-friendly interface designed for modern screens.
- **🔒 Privacy First:** 100% offline. No permissions required. No tracking. No ads.

---

## 📥 Installation

### 🚀 Recommended (Direct Install)
The fastest way to get the app on your phone:

1. **Download the APK:** [Click here to download the latest `.apk`](https://github.com/cybersaad/BitFlip/releases/latest) (or go to the **Releases** section).
2. **Open the file:** Tap the downloaded `.apk` file on your Android device.
3. **Allow Installation:** If prompted, allow "Install from unknown sources" in your security settings.
4. **Done!** You're ready to start converting.

---

## 🛠️ Building from Source

If you want to contribute or build the app yourself:

### Prerequisites
- **Android Studio** Ladybug (2024.2.1) or newer.
- **JDK 17** or higher.
- **Android SDK 35** installed.

### Steps
1. **Clone the repo:**
   ```bash
   git clone https://github.com/cybersaad/BitFlip.git
   ```
2. **Open in Android Studio:** Choose `Open` and select the project folder.
3. **Sync Gradle:** Wait for the project to download all necessary dependencies.
4. **Run:** Connect your device/emulator and click the **Run** button.

---

## 🏗️ Project Architecture

The app follows a clean, modular structure for maintainability:

- **`ConversionEngine.kt`**: Pure Kotlin logic for base conversions (reusable outside Android).
- **`ui/screens/`**: UI components for Converter, Reference, and About screens.
- **`ui/theme/`**: Theme definitions using Material3 design system.
- **Compose Navigation**: Seamless transitions between different app sections.

---

## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Kotlin 2.0+ |
| **UI Framework** | Jetpack Compose (Material3) |
| **Architecture** | MVVM (State-driven UI) |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 35 (Android 15) |

---

## 🤝 Contribution

Contributions are welcome! If you find a bug or have a feature request, please open an **Issue** or submit a **Pull Request**.

---

## 📄 License

**© 2026 Saad Khan.** All rights reserved.
Developed with ❤️ by [Saad Khan](https://github.com/cybersaad).

