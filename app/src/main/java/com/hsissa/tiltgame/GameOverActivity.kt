package com.hsissa.tiltgame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val finalScore = intent.getIntExtra("score", 0)

        GamePrefs.saveHighScore(this, finalScore)
        val highScore = GamePrefs.getHighScore(this)

        findViewById<TextView>(R.id.finalScoreText).text =
            getString(R.string.score, finalScore)

        findViewById<TextView>(R.id.highScoreText).text =
            getString(R.string.high_score, highScore)

        findViewById<Button>(R.id.restartBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.mainMenuBtn).setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
    }
}
