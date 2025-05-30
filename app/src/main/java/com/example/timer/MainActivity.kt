package com.example.timer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import com.example.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            val total = elapsedTime + (if (isRunning) now - startTime else 0)
            binding.timerText.text = formatTime(total)
            if (isRunning) handler.postDelayed(this, 10)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore state if available
        if (savedInstanceState != null) {
            isRunning = savedInstanceState.getBoolean("isRunning")
            startTime = savedInstanceState.getLong("startTime")
            elapsedTime = savedInstanceState.getLong("elapsedTime")
            // If timer was running, adjust startTime to current time
            if (isRunning) {
                startTime = System.currentTimeMillis() - (savedInstanceState.getLong("currentElapsed", 0L))
                handler.post(updateRunnable)
            } else {
                binding.timerText.text = formatTime(elapsedTime)
            }
        }

        binding.startButton.setOnClickListener {
            if (!isRunning) {
                startTime = System.currentTimeMillis()
                isRunning = true
                handler.post(updateRunnable)
            }
        }

        binding.pauseButton.setOnClickListener {
            if (isRunning) {
                elapsedTime += System.currentTimeMillis() - startTime
                isRunning = false
            }
        }

        binding.resetButton.setOnClickListener {
            isRunning = false
            elapsedTime = 0L
            binding.timerText.text = formatTime(0L)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isRunning", isRunning)
        outState.putLong("startTime", startTime)
        outState.putLong("elapsedTime", elapsedTime)
        // Save how much time has passed since start if running
        if (isRunning) {
            outState.putLong("currentElapsed", System.currentTimeMillis() - startTime)
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val ms = milliseconds % 1000 / 10
        val s = (milliseconds / 1000) % 60
        val m = (milliseconds / 60000) % 60
        val h = (milliseconds / 3600000)
        return String.format("%02d:%02d:%02d.%02d", h, m, s, ms)
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateRunnable)
        super.onDestroy()
    }
}