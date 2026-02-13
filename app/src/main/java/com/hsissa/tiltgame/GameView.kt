package com.hsissa.tiltgame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import kotlin.math.hypot
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    private val bgPaint = Paint()
    private val playerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val countdownPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var soundPool: SoundPool
    private var greenSound = 0
    private var redSound = 0
    private var failSound = 0

    private val playerRadius = 20f
    private val greenRadius = 18f
    private val redRadius = 26f

    private var px = 0f
    private var py = 0f
    private var vx = 0f
    private var vy = 0f

    private var greenX = 0f
    private var greenY = 0f

    private var redX = 0f
    private var redY = 0f
    private var redVisible = false
    private var redSpawnTime = 0L
    private var lastRedTriggerScore = 0

    private var score = 0
    private var gameOver = false

    private var countdown = 3
    private var gameStarted = false

    var onGameOver: ((Int) -> Unit)? = null

    init {
        bgPaint.color = 0xFF0E1116.toInt()
        playerPaint.color = 0xFF00E5FF.toInt()
        greenPaint.color = 0xFF00FF9C.toInt()
        redPaint.color = 0xFFFF5252.toInt()

        textPaint.color = 0xFFFFFFFF.toInt()
        textPaint.textSize = 48f

        countdownPaint.color = 0xFFFFFFFF.toInt()
        countdownPaint.textSize = 160f
        countdownPaint.textAlign = Paint.Align.CENTER

        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttrs)
            .build()

        greenSound = soundPool.load(context, R.raw.green_pickup, 1)
        redSound = soundPool.load(context, R.raw.red_bonus, 1)
        failSound = soundPool.load(context, R.raw.fail, 1)

        startCountdown()
    }

    private fun startCountdown() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                countdown--
                invalidate()
                if (countdown > 0) handler.postDelayed(this, 1000)
                else gameStarted = true
            }
        }, 1000)
    }

    fun updateTilt(ax: Float, ay: Float) {
        if (!gameStarted || gameOver) return
        vx = -ax * 3
        vy = ay * 3
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        px = w / 2f
        py = h / 2f
        spawnGreen()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        if (gameStarted && !gameOver) {
            px += vx
            py += vy

            if (px <= playerRadius || px >= width - playerRadius ||
                py <= playerRadius || py >= height - playerRadius) {

                soundPool.play(failSound, 1f, 1f, 1, 0, 1f)
                gameOver = true
                onGameOver?.invoke(score)
                return
            }

            checkCollision()
        }

        canvas.drawCircle(px, py, playerRadius, playerPaint)
        canvas.drawCircle(greenX, greenY, greenRadius, greenPaint)

        if (redVisible) {
            if (System.currentTimeMillis() - redSpawnTime > 3000) {
                redVisible = false
            } else {
                canvas.drawCircle(redX, redY, redRadius, redPaint)
            }
        }

        canvas.drawText("SCORE  $score", 40f, 60f, textPaint)

        if (!gameStarted && countdown > 0) {
            canvas.drawText(
                countdown.toString(),
                width / 2f,
                height / 2f + 60,
                countdownPaint
            )
        }
    }

    private fun checkCollision() {
        if (distance(px, py, greenX, greenY) < playerRadius + greenRadius) {
            score += 1
            soundPool.play(greenSound, 0.7f, 0.7f, 1, 0, 1f)
            spawnGreen()

            if (score % 5 == 0 && score != lastRedTriggerScore) {
                spawnRed()
                lastRedTriggerScore = score
            }
        }

        if (redVisible && distance(px, py, redX, redY) < playerRadius + redRadius) {
            score += 5
            soundPool.play(redSound, 1f, 1f, 1, 0, 1f)
            vibrateOnRedPickup()
            redVisible = false
        }
    }

    private fun spawnGreen() {
        greenX = Random.nextFloat() * (width - 100) + 50
        greenY = Random.nextFloat() * (height - 150) + 100
    }

    private fun spawnRed() {
        redX = Random.nextFloat() * (width - 100) + 50
        redY = Random.nextFloat() * (height - 150) + 100
        redSpawnTime = System.currentTimeMillis()
        redVisible = true
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return hypot(x1 - x2, y1 - y2)
    }

    private fun getVibrator(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun vibrateOnRedPickup() {
        if (!GamePrefs.isVibrationEnabled(context)) return
        val vibrator = getVibrator()
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                android.os.VibrationEffect.createOneShot(
                    40,
                    android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(40)
        }
    }

    fun releaseSounds() {
        soundPool.release()
    }
}
