package com.tictactoe.game.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager(private val context: Context) {
    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)

    fun playTap() {
        if (isHapticsEnabled) {
            vibrate(25, 120)
        }
        if (isSoundEnabled) {
            audioScope.launch {
                playTone(freq = 680.0, durationMs = 35, volume = 0.35f)
            }
        }
    }

    fun playAiMove() {
        if (isHapticsEnabled) {
            vibrate(20, 80)
        }
        if (isSoundEnabled) {
            audioScope.launch {
                playTone(freq = 520.0, durationMs = 45, volume = 0.3f)
            }
        }
    }

    fun playWin() {
        if (isHapticsEnabled) {
            vibratePattern(longArrayOf(0, 50, 60, 100, 70, 180))
        }
        if (isSoundEnabled) {
            audioScope.launch {
                // Uplifting victory arpeggio: C5 -> E5 -> G5 -> C6
                playTone(523.25, 90, 0.45f)
                playTone(659.25, 90, 0.50f)
                playTone(783.99, 110, 0.55f)
                playTone(1046.50, 240, 0.65f)
            }
        }
    }

    fun playDraw() {
        if (isHapticsEnabled) {
            vibratePattern(longArrayOf(0, 40, 80, 40))
        }
        if (isSoundEnabled) {
            audioScope.launch {
                playTone(440.0, 100, 0.35f)
                playTone(370.0, 160, 0.30f)
            }
        }
    }

    fun playUndo() {
        if (isHapticsEnabled) {
            vibrate(15, 60)
        }
        if (isSoundEnabled) {
            audioScope.launch {
                playTone(400.0, 25, 0.25f)
            }
        }
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

    private fun playTone(freq: Double, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val generatedSnd = ByteArray(2 * numSamples)

        // Generate Sine wave with attack/decay envelope to eliminate clicks
        val attackSamples = (numSamples * 0.1).toInt().coerceAtLeast(1)
        val decaySamples = (numSamples * 0.3).toInt().coerceAtLeast(1)
        val sustainSamples = numSamples - attackSamples - decaySamples

        for (i in 0 until numSamples) {
            val dVal = sin(2.0 * Math.PI * i / (sampleRate / freq))

            // Envelope calculation
            val envelope = when {
                i < attackSamples -> i.toFloat() / attackSamples
                i < attackSamples + sustainSamples -> 1.0f
                else -> {
                    val decayIndex = i - attackSamples - sustainSamples
                    (1.0f - (decayIndex.toFloat() / decaySamples)).coerceAtLeast(0f)
                }
            }

            val sample = (dVal * 32767 * volume * envelope).toInt().coerceIn(-32768, 32767).toShort()
            generatedSnd[2 * i] = (sample.toInt() and 0x00ff).toByte()
            generatedSnd[2 * i + 1] = ((sample.toInt() and 0xff00) ushr 8).toByte()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(generatedSnd.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {}
    }
}
