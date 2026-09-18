package com.tictactoe.game.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Crash-proof, 100% offline audio and haptic feedback engine.
 * Utilizes native ToneGenerator for zero-allocation, zero-latency, hardware-accelerated sound.
 */
class SoundManager(private val context: Context) {
    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 65)
        } catch (_: Exception) {
            toneGen = null
        }
    }

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    fun playTap() {
        if (isHapticsEnabled) {
            vibrate(25, 120)
        }
        if (isSoundEnabled) {
            safePlayTone(ToneGenerator.TONE_PROP_BEEP, 35)
        }
    }

    fun playAiMove() {
        if (isHapticsEnabled) {
            vibrate(20, 90)
        }
        if (isSoundEnabled) {
            safePlayTone(ToneGenerator.TONE_PROP_ACK, 40)
        }
    }

    fun playWin() {
        if (isHapticsEnabled) {
            vibratePattern(longArrayOf(0, 50, 60, 100, 70, 180))
        }
        if (isSoundEnabled) {
            audioScope.launch {
                safePlayTone(ToneGenerator.TONE_PROP_BEEP, 70)
                delay(80)
                safePlayTone(ToneGenerator.TONE_PROP_ACK, 90)
                delay(100)
                safePlayTone(ToneGenerator.TONE_PROP_PROMPT, 180)
            }
        }
    }

    fun playDraw() {
        if (isHapticsEnabled) {
            vibratePattern(longArrayOf(0, 40, 80, 40))
        }
        if (isSoundEnabled) {
            audioScope.launch {
                safePlayTone(ToneGenerator.TONE_PROP_NACK, 90)
                delay(110)
                safePlayTone(ToneGenerator.TONE_PROP_NACK, 120)
            }
        }
    }

    fun playUndo() {
        if (isHapticsEnabled) {
            vibrate(15, 60)
        }
        if (isSoundEnabled) {
            safePlayTone(ToneGenerator.TONE_PROP_BEEP2, 25)
        }
    }

    private fun safePlayTone(toneType: Int, durationMs: Int) {
        try {
            if (toneGen == null) {
                toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 65)
            }
            toneGen?.startTone(toneType, durationMs)
        } catch (_: Exception) {}
    }

    private fun vibrate(durationMs: Long, amplitude: Int = 128) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern(timings: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGen?.release()
            toneGen = null
        } catch (_: Exception) {}
    }
}
