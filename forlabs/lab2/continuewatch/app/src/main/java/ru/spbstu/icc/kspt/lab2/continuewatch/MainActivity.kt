package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG : String = "lifecycle"
    var secondsElapsed: Int = 0
    var isRunning = true

    var backgroundThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                Thread.sleep(1000)
                textSecondsElapsed.post {
                    textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                    threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
                }
                if (!isRunning) Thread.currentThread().interrupt()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        backgroundThread.start()
        isRunning = true
        Log.d(TAG, "Activity onCreate(): created")
        val view : View = View(this)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Activity onStart(): started")
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
        Log.d(TAG, "Activity onResume(): resumed")
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
        Log.d(TAG, "Activity onPause(): paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Activity onStop(): stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity onDestroy(): destroyed")
        isRunning = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", secondsElapsed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secondsElapsed = savedInstanceState.getInt("seconds")
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed)
        }
    }
}
