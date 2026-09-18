# 🎮 Tic-Tac-Toe Ultra (Android App & APK)

A futuristic, high-performance Tic-Tac-Toe mobile game engineered for Android with **Jetpack Compose** and **Material 3**. Features a sleek **Cyber-Neon Glassmorphic** aesthetic, **Minimax AI**, **Pass & Play multiplayer**, zero-latency synthesized audio, and **100% offline standalone capability**.

---

## 📱 Features

- ⚡ **Zero-Lag 60/120 FPS Performance**: Hardware-accelerated neon rendering with optimized canvas drawing, eliminating frame drops, jank, and battery drain.
- 🛡️ **100% Offline (Anytime & Anywhere)**: No internet connection or network permissions required. Complete local gameplay, stats, and match history storage.
- 🔊 **Crash-Proof Synthesized Audio**: Built-in native tone synthesizer delivering crisp audio feedback (tap clicks, AI turns, win fanfare) with zero memory leaks.
- 📳 **Haptic Response**: Tactile vibration patterns tailored for moves, turns, and victory celebrations.
- 🧠 **Smart Minimax AI Engine**:
  - 🟢 **Casual**: Relaxed, playful moves.
  - 🟡 **Tactical**: Smart moves with occasional tactical slips.
  - 🔴 **Unbeatable**: Flawless Minimax algorithm with Alpha-Beta pruning.
- 👥 **Pass & Play Mode**: 2-player local multiplayer on a single device.
- 🏆 **Scoreboard & Win Streaks**: Real-time score tracking with dynamic win streak multipliers (e.g. `🔥 3x STREAK`).
- 📜 **Match Archive & Stats**: Full in-app history logging game results, total moves, win rates, and timestamps saved locally.

---

## 📥 Direct APK Installation

The pre-built, optimized APK is located at the root of this repository:

👉 **[`TicTacToe.apk`](TicTacToe.apk)** *(~8.8 MB standalone installable package)*

### Installation Steps:
1. Download **`TicTacToe.apk`** directly to your Android phone or tablet.
2. Open the file in your Downloads/Files manager to install (enable *"Install unknown apps"* if prompted).
3. Or install via ADB:
   ```bash
   adb install -r TicTacToe.apk
   ```

---

## 🛠 Tech Stack & Architecture

- **Platform**: Android 7.0+ (API 24 to 34)
- **UI Framework**: Jetpack Compose, Material 3
- **Language & Coroutines**: Kotlin 2.0.0, Kotlinx Coroutines
- **Build System**: Gradle 8.14.3, Android Gradle Plugin 8.5.1
- **Audio & Haptics**: Android Native `ToneGenerator`, Android `Vibrator`
- **Persistence**: Internal Storage JSON (`context.filesDir`)

---

## 🏗 Building from Source

To build the APK locally from source:

```bash
cd android
./gradlew assembleDebug
```

The output APK will be generated at `android/app/build/outputs/apk/debug/app-debug.apk`.

---

## 📄 License
MIT License
