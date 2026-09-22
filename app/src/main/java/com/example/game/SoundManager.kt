package com.example.game

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, zero-latency Sound Effect Management System.
 * Generates crisp procedural audio syntheses for game actions:
 * - Rolling dice (cup shake & rattling dice clicks)
 * - Token moves & step clicks
 * - Token capture (dramatic knockout strike & zap)
 * - Victory fanfare (triumphant royal arpeggio)
 * - Six rolled & safe star chimes
 * - Snake bite & ladder climbs
 *
 * Includes persistent settings toggle via Android SharedPreferences.
 */
class SoundManager private constructor(private val context: Context) {

    private val prefs = context.getSharedPreferences("ludo_king_sound_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATION_ENABLED, true))
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val sampleRate = 22050

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    /**
     * Global toggle for mute/unmute sound effects.
     * Returns true if sound is now enabled (unmuted), false if muted.
     */
    fun toggleSound(): Boolean {
        val newEnabled = !_isSoundEnabled.value
        setSoundEnabled(newEnabled)
        if (newEnabled) {
            playButtonClick()
        }
        return newEnabled
    }

    /**
     * Toggle mute state. Returns true if now muted.
     */
    fun toggleMute(): Boolean = !toggleSound()

    fun setMuted(muted: Boolean) {
        setSoundEnabled(!muted)
    }

    fun isMuted(): Boolean = !_isSoundEnabled.value

    fun setVibrationEnabled(enabled: Boolean) {
        _isVibrationEnabled.value = enabled
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }

    fun toggleVibration(): Boolean {
        val newEnabled = !_isVibrationEnabled.value
        setVibrationEnabled(newEnabled)
        if (newEnabled) {
            vibrate(30)
        }
        return newEnabled
    }

    /**
     * Sound 1: Dice Roll (রোলিং ডাইস খটখট শব্দ)
     * Cascading succession of percussive wooden clicks mimicking rolling dice.
     */
    fun playDiceRoll() {
        if (!_isSoundEnabled.value) return
        vibrate(35)
        scope.launch {
            val totalDurationMs = 380
            val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
            val buffer = ShortArray(totalSamples)

            // Series of 7 click pulses at accelerating and decelerating intervals
            val pulseTimesMs = intArrayOf(20, 70, 130, 190, 250, 310, 350)
            val frequencies = intArrayOf(520, 680, 480, 750, 610, 450, 580)

            for (i in pulseTimesMs.indices) {
                val startIdx = (sampleRate * (pulseTimesMs[i] / 1000.0)).toInt()
                val freq = frequencies[i]
                val clickSamples = (sampleRate * 0.025).toInt() // 25ms per click

                for (s in 0 until clickSamples) {
                    val idx = startIdx + s
                    if (idx < totalSamples) {
                        val t = s.toDouble() / sampleRate
                        val envelope = exp(-t * 120.0) // sharp decaying click
                        val wave = sin(2.0 * PI * freq * t) * envelope
                        buffer[idx] = (wave * 26000).toInt().toShort()
                    }
                }
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 2: Token Step / Move (গুটি চালার শব্দ)
     * Crisp, resonant wooden pawn clack.
     */
    fun playTokenStep() {
        if (!_isSoundEnabled.value) return
        vibrate(20)
        scope.launch {
            val durationMs = 85
            val samples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(samples)

            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                // Dual frequency pop (540Hz + 820Hz harmonic)
                val decay = exp(-t * 45.0)
                val wave = (sin(2.0 * PI * 540.0 * t) * 0.7 + sin(2.0 * PI * 820.0 * t) * 0.3) * decay
                buffer[i] = (wave * 28000).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 3: Token Capture / Knockout (গুটি খাওয়ার পাওয়ারফুল সাউন্ড)
     * Dramatic punch impact with descending pitch zap and reverberation.
     */
    fun playTokenCapture() {
        if (!_isSoundEnabled.value) return
        vibrate(70)
        scope.launch {
            val durationMs = 320
            val samples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(samples)

            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                // Descending pitch sweep from 880Hz down to 180Hz
                val instantaneousFreq = 880.0 - (700.0 * (t / (durationMs / 1000.0)))
                val decay = exp(-t * 12.0)
                val hit = if (t < 0.04) sin(2.0 * PI * 220.0 * t) * 0.8 else 0.0 // thud component
                val sweep = sin(2.0 * PI * instantaneousFreq * t) * 0.6
                val wave = (hit + sweep) * decay
                buffer[i] = (wave.coerceIn(-1.0, 1.0) * 31000).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 4: Victory Fanfare (খেলা জেতার রাজকীয় সংগীত - Win State)
     * Majestic arpeggio chord progression (C5 -> E5 -> G5 -> C6).
     */
    fun playVictory() {
        if (!_isSoundEnabled.value) return
        vibrate(100)
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            val noteDurationMs = 180
            val totalDurationMs = noteDurationMs * notes.size + 300
            val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
            val buffer = ShortArray(totalSamples)

            for ((noteIdx, freq) in notes.withIndex()) {
                val startIdx = (sampleRate * (noteIdx * noteDurationMs / 1000.0)).toInt()
                val noteSamples = (sampleRate * ((if (noteIdx == notes.lastIndex) 480 else noteDurationMs) / 1000.0)).toInt()

                for (s in 0 until noteSamples) {
                    val idx = startIdx + s
                    if (idx < totalSamples) {
                        val t = s.toDouble() / sampleRate
                        val decay = exp(-t * (if (noteIdx == notes.lastIndex) 3.5 else 6.0))
                        val wave = (sin(2.0 * PI * freq * t) * 0.7 + sin(2.0 * PI * freq * 2.0 * t) * 0.3) * decay
                        val existing = buffer[idx] / 32767.0
                        val mixed = (existing + wave).coerceIn(-1.0, 1.0)
                        buffer[idx] = (mixed * 28000).toInt().toShort()
                    }
                }
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 4B: Loss / Defeat State (খেলা পরাজয় / লস হওয়ার দুঃখজনক সাউন্ড - Loss State)
     * Somber descending minor cadence (G4 -> Eb4 -> D4 -> C4) with deep resonance.
     */
    fun playLoss() {
        if (!_isSoundEnabled.value) return
        vibrate(80)
        scope.launch {
            val notes = doubleArrayOf(392.00, 311.13, 293.66, 261.63) // G4, Eb4, D4, C4 (Minor sadness)
            val noteDurationMs = 230
            val totalDurationMs = noteDurationMs * notes.size + 350
            val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
            val buffer = ShortArray(totalSamples)

            for ((noteIdx, freq) in notes.withIndex()) {
                val startIdx = (sampleRate * (noteIdx * noteDurationMs / 1000.0)).toInt()
                val noteSamples = (sampleRate * ((if (noteIdx == notes.lastIndex) 520 else noteDurationMs) / 1000.0)).toInt()

                for (s in 0 until noteSamples) {
                    val idx = startIdx + s
                    if (idx < totalSamples) {
                        val t = s.toDouble() / sampleRate
                        val decay = exp(-t * (if (noteIdx == notes.lastIndex) 2.8 else 5.2))
                        // Melancholy tone with slight expressive wobble
                        val vibrato = sin(2.0 * PI * 4.5 * t) * 3.5
                        val wave = (sin(2.0 * PI * (freq + vibrato) * t) * 0.85 + sin(2.0 * PI * (freq * 0.5) * t) * 0.15) * decay
                        val existing = buffer[idx] / 32767.0
                        val mixed = (existing + wave).coerceIn(-1.0, 1.0)
                        buffer[idx] = (mixed * 26000).toInt().toShort()
                    }
                }
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 5: Rolled Six (ছক্কার আনন্দধ্বনি)
     * Upbeat double-tone celebration chime (784Hz -> 1046Hz).
     */
    fun playSixRolled() {
        if (!_isSoundEnabled.value) return
        vibrate(40)
        scope.launch {
            val durationMs = 240
            val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(totalSamples)
            val halfSamples = totalSamples / 2

            // Note 1: 784Hz (G5)
            for (i in 0 until halfSamples) {
                val t = i.toDouble() / sampleRate
                val decay = exp(-t * 15.0)
                val wave = sin(2.0 * PI * 784.0 * t) * decay
                buffer[i] = (wave * 26000).toInt().toShort()
            }
            // Note 2: 1046.5Hz (C6)
            for (i in halfSamples until totalSamples) {
                val t = (i - halfSamples).toDouble() / sampleRate
                val decay = exp(-t * 12.0)
                val wave = sin(2.0 * PI * 1046.5 * t) * decay
                buffer[i] = (wave * 28000).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 6: Safe Star or Home Landed (নিরাপদ ঘরে প্রবেশের শব্দ)
     * Sparkling harmonic chime.
     */
    fun playSafeLanding() {
        if (!_isSoundEnabled.value) return
        vibrate(25)
        scope.launch {
            val durationMs = 220
            val samples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(samples)

            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val decay = exp(-t * 10.0)
                val wave = (sin(2.0 * PI * 1174.66 * t) * 0.6 + sin(2.0 * PI * 1760.0 * t) * 0.4) * decay
                buffer[i] = (wave * 24000).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 7: Snake Bite (সাপের ছোবল - সাপের মুখে পড়ে নিচে নামা)
     */
    fun playSnakeBite() {
        if (!_isSoundEnabled.value) return
        vibrate(60)
        scope.launch {
            val durationMs = 300
            val samples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(samples)

            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                // Pitch plunges down
                val freq = (600.0 - 450.0 * (t / 0.3)).coerceAtLeast(100.0)
                val decay = exp(-t * 6.0)
                val wave = sin(2.0 * PI * freq * t) * decay
                buffer[i] = (wave * 25000).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 8: Ladder Climb (মই বেয়ে উপরে ওঠা)
     */
    fun playLadderClimb() {
        if (!_isSoundEnabled.value) return
        vibrate(30)
        scope.launch {
            val notes = doubleArrayOf(440.0, 554.37, 659.25, 880.0) // A4, C#5, E5, A5
            val stepSamples = (sampleRate * 0.06).toInt()
            val totalSamples = stepSamples * notes.size
            val buffer = ShortArray(totalSamples)

            for ((idx, freq) in notes.withIndex()) {
                val start = idx * stepSamples
                for (s in 0 until stepSamples) {
                    val t = s.toDouble() / sampleRate
                    val decay = exp(-t * 16.0)
                    buffer[start + s] = (sin(2.0 * PI * freq * t) * decay * 26000).toInt().toShort()
                }
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 9: UI Button Click (বাটন ক্লিকের হালকা শব্দ)
     */
    fun playButtonClick() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            val samples = (sampleRate * 0.03).toInt()
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val decay = exp(-t * 70.0)
                buffer[i] = (sin(2.0 * PI * 920.0 * t) * decay * 20000).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Sound 10: Emoji Pop / Reaction Burst (ইমোজি পপ রিঅ্যাকশন শব্দ)
     */
    fun playEmojiPop() {
        if (!_isSoundEnabled.value) return
        vibrate(20)
        scope.launch {
            val durationMs = 80
            val samples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val freq = 520.0 + (t / (durationMs / 1000.0)) * 380.0
                val envelope = exp(-t * 40.0)
                buffer[i] = (sin(2.0 * PI * freq * t) * envelope * 22000).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    private fun vibrate(durationMs: Long) {
        if (!_isVibrationEnabled.value) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    private fun playPcmBuffer(buffer: ShortArray) {
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
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            // Release AudioTrack after playback finishes
            scope.launch {
                val durationMs = (buffer.size * 1000L) / sampleRate + 50L
                kotlinx.coroutines.delay(durationMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    companion object {
        private const val KEY_SOUND_ENABLED = "key_sound_effects_enabled"
        private const val KEY_VIBRATION_ENABLED = "key_vibration_enabled"

        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
