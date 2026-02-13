package com.hsissa.tiltgame

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val vibrationSwitch = findViewById<SwitchCompat>(R.id.vibrationSwitch)
        val volumeSeekBar = findViewById<SeekBar>(R.id.volumeSeekBar)

        vibrationSwitch.isChecked = GamePrefs.isVibrationEnabled(this)

        volumeSeekBar.max = 100
        volumeSeekBar.progress = (GamePrefs.getMusicVolume(this) * 100).toInt()

        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            GamePrefs.setVibrationEnabled(this, isChecked)
        }

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    GamePrefs.setMusicVolume(this@SettingsActivity, progress / 100f)
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }
}