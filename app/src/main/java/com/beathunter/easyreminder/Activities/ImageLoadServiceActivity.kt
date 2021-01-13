package com.beathunter.easyreminder.Activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.Services.ImageLoadService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class ImageLoadServiceActivity : AppCompatActivity() {

    private val urlForCoroutines = "https://sun9-25.userapi.com/OUVWMtar9YKLzhD3nz_OHD1Y7jJyLPqXO39noA/-ONaP92RrhM.jpg"
    companion object {
        val MSG_OUTCOMING_LOCATION = 2
        var pathText : String = "null"
    }

    var mService : Messenger? = null
    var isBound : Boolean = false

    val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("Broadcastyyy", "Connected to service")
            mService = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("Broadcastyyy", "Disconnected from service")
            isBound = false
        }

    }

    class OutcomingHandler() : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_OUTCOMING_LOCATION -> {
                    Log.d("Broadcastyyy", "Answer received. Received path: ${msg.obj}")
                    pathText = msg.obj.toString()
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Service Lab7"
        setContentView(R.layout.service_activity)

        val downloadBtn = findViewById<Button>(R.id.service_start_btn)
        downloadBtn.setOnClickListener {
            val serviceIntent = Intent(this, ImageLoadService::class.java)
            serviceIntent.putExtra("imageURL", urlForCoroutines);
            startService(serviceIntent)
        }

        val bindServiceBtn = findViewById<Button>(R.id.bind_service_button)
        bindServiceBtn.setOnClickListener {
            val serviceIntent = Intent(this, ImageLoadService::class.java)
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        }

        val sendURLButton = findViewById<Button>(R.id.send_url_button)
        sendURLButton.setOnClickListener {
            sendURL()
        }

        val showPathBtn = findViewById<Button>(R.id.show_path_button)
        showPathBtn.setOnClickListener {
            val pathTV = findViewById<TextView>(R.id.path_tv)
            pathTV.setText(pathText)
        }
    }

    private fun sendURL() {
        if (!isBound) return
        val responseMessenger = Messenger(OutcomingHandler())
        val message : Message = Message.obtain().apply {
            what = ImageLoadService.MSG_INCOMING_URL
            obj = urlForCoroutines
            replyTo = responseMessenger
        }
        mService?.send(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        isBound = false
    }
}

