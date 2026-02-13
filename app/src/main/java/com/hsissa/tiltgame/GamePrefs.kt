package com.hsissa.tiltgame

import android.content.Context
import androidx.core.content.edit

object GamePrefs {

    private const val PREF_NAME = "tilt_game_prefs"
    private const val KEY_VIBRATION = "vibration_enabled"
    private const val KEY_HIGH_SCORE = "high_score"
    private const val KEY_MUSIC_VOLUME = "music_volume"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isVibrationEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_VIBRATION, true)

    fun setVibrationEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit {
            putBoolean(KEY_VIBRATION, enabled)
        }
    }

    fun getHighScore(context: Context): Int =
        prefs(context).getInt(KEY_HIGH_SCORE, 0)

    fun saveHighScore(context: Context, score: Int) {
        val currentHigh = getHighScore(context)
        if (score > currentHigh) {
            prefs(context).edit {
                putInt(KEY_HIGH_SCORE, score)
            }
        }
    }

    fun getMusicVolume(context: Context): Float =
        prefs(context).getFloat(KEY_MUSIC_VOLUME, 0.6f)

    fun setMusicVolume(context: Context, volume: Float) {
        prefs(context).edit {
            putFloat(KEY_MUSIC_VOLUME, volume)
        }
    }
}
