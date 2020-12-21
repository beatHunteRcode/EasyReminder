package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val TAG : String = "lifecycle"
    var secondsElapsed: Int = 0

    var backgroundThread : Thread? = null
    var executorService : ExecutorService = Executors.newSingleThreadExecutor()
    var isRunning = false


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
        isRunning = true
        executorService.execute {
                if (!executorService.isShutdown) {
                    while (isRunning) {
                        Thread.sleep(1000)
                        textSecondsElapsed.post {
                            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                            threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
                        }
                    }
                }
        }
        

        
//        backgroundThread = Thread {
//            try {
//                while (!Thread.interrupted()) {
//                    Thread.sleep(5000)
//                    textSecondsElapsed.post {
//                        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
//                        threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
//                    }
//                }
//            }
//            catch (e: InterruptedException) {
//                //ignored
//            }
//        }
//        backgroundThread?.start()

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Activity onPause(): paused")
        isRunning = false
//        backgroundThread?.interrupt()

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Activity onStop(): stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity onDestroy(): destroyed")
//        executorService.shutdown()
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
