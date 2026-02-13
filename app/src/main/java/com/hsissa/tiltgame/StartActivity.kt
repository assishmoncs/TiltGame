package com.hsissa.tiltgame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        findViewById<Button>(R.id.playBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.settingsBtn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.helpBtn).setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        findViewById<Button>(R.id.aboutBtn).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}
