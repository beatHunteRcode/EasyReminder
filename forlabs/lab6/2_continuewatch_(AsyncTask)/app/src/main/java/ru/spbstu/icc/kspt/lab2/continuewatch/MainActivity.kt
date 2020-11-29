package ru.spbstu.icc.kspt.lab2.continuewatch

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG : String = "lifecycle"
    var secondsElapsed: Int = 0

    class MyTask(val func : () -> Boolean) : AsyncTask<Unit, Unit, Unit>() {

        override fun doInBackground(vararg p0: Unit?) {
            while (!isCancelled) {
                Thread.sleep(1000)
                publishProgress()
            }
        }

        override fun onProgressUpdate(vararg values: Unit?) {
            super.onProgressUpdate(*values)
            func()
        }
    }

    var myTask : MyTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Activity onCreate(): created")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Activity onStart(): started")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Activity onResume(): resumed")
        myTask = MyTask {
            textSecondsElapsed.post {
                textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
            }
        }
        myTask?.execute()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Activity onPause(): paused")
        myTask?.cancel(false)
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
