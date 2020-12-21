package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.whenResumed
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val TAG : String = "lifecycle"

    private var secondsElapsed = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity onCreate(): created")
        
        lifecycleScope.launchWhenResumed {
                while (true) {
                    delay(1000)
                    textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                    threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Activity onStart(): started")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Activity onResume(): resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Activity onPause(): paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Activity onStop(): stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity onDestroy(): destroyed")
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
