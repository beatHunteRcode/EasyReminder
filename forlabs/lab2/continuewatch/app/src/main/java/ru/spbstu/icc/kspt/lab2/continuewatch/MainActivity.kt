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
    var isRunning = false

    var backgroundThread = Thread {
        while (true) {
            Thread.sleep(1000)
            if (isRunning) {
                textSecondsElapsed.post {
                    textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                }
            }
        }
    }
    //как избавится от лишнего треда
    //что происходит, если мы все таки обратимся к secondsElapsed, минуя if
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        backgroundThread.start()
        Log.d(TAG, "Activity onCreate(): created")
        val view : View = View(this)
        view.post()
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
