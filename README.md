# 🎮 Tic-Tac-Toe Ultra (Android APK & Python XML Engine)

A futuristic, high-aesthetic Tic-Tac-Toe game featuring a **Cyber-Neon Glassmorphic** user interface, **Minimax AI** opponent, **Pass & Play multiplayer**, synthesized audio, tactile haptics, and match logging.

Available as both a **standalone Android APK** and a **Python desktop application**.

---

## 📱 Android Application (Tic-Tac-Toe Ultra)

### ✨ Features
- **Cyber-Neon Glassmorphic UI**: Deep obsidian dark mode (`#070A12`) with ambient pulsating nebulas and frosted glass surfaces.
- **Player X (Cyan Surge)**: Electric Cyan (`#00F2FE` / `#4FACFE`) with radiant glow bloom.
- **Player O (Coral Flare)**: Neon Crimson (`#FF2A6D` / `#FF7E5F`) with pulsing radiant rings.
- **Dynamic Laser Win Beam**: An animated neon beam drawing across winning cells with gradient luminescence.
- **Smart Minimax AI Engine**:
  - 🟢 **Casual**: Relaxed, playful moves.
  - 🟡 **Tactical**: Smart moves with occasional tactical slips.
  - 🔴 **Unbeatable**: Flawless Minimax algorithm with Alpha-Beta pruning.
- **Local Multiplayer**: 2-Player Pass & Play mode on a single device.
- **Scoreboard & Win Streaks**: Live score tracking with active win streak counters (e.g. `🔥 3x STREAK`).
- **Procedural Sound & Haptics**: Built-in tone synthesizer powered by Android's native `AudioTrack` (clicks, turn chimes, victory fanfare) and tactile vibration patterns.
- **Match Archive & Stats**: In-app dialog logging game results, total moves, win rates, and timestamps.

### 📥 Installing the APK
The pre-built APK is located at the root of this repository:
👉 **[`TicTacToe.apk`](TicTacToe.apk)** *(8.8 MB standalone installable package)*

1. Download **`TicTacToe.apk`** to your Android device.
2. Tap the file in your Downloads/Files manager to install (enable "Install unknown apps" if prompted).
3. Alternatively, install via ADB:
   ```bash
   adb install -r TicTacToe.apk
   ```

---

## 🐍 Python Desktop Version

The project also includes the original Python Tkinter desktop implementation powered by XML persistence.

### Features:
- Tkinter GUI with custom color themes.
- XML-based configuration (`config.xml`).
- Match history recording to XML (`match_history.xml`).
- Game state export and restoration (`savegame.xml`).

### Running the Python Game:
```bash
python main.py
```

---

## 🛠 Tech Stack

- **Mobile Platform**: Android 7.0+ (API 24 - 34)
- **Framework**: Jetpack Compose, Material 3, Kotlin Coroutines
- **Build System**: Gradle 8.14.3, Android Gradle Plugin 8.5.1, Kotlin 2.0.0
- **Audio/Sensors**: Android `AudioTrack` PCM tone generator, Android `Vibrator`
- **Desktop**: Python 3, Tkinter, `xml.etree.ElementTree`

---

## 📄 License
MIT License
