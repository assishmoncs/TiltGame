package com.hsissa.tiltgame

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gameView: GameView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameView = GameView(this)
        setContentView(gameView)

        mediaPlayer = MediaPlayer.create(this, R.raw.bg_music)
        mediaPlayer?.isLooping = true

        gameView.onGameOver = { score ->
            runOnUiThread { showGameOver(score) }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                window.insetsController?.hide(
                    android.view.WindowInsets.Type.statusBars()
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    }

    override fun onResume() {
        super.onResume()

        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        val vol = GamePrefs.getMusicVolume(this)
        mediaPlayer?.setVolume(vol, vol)
        mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.releaseSounds()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        gameView.updateTilt(event.values[0], event.values[1])
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun showGameOver(score: Int) {
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
        finish()
    }
}
