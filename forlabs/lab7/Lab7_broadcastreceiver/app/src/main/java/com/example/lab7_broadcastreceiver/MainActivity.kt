package com.example.lab7_broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    val BROADCAST_ACTION = "com.beathunter.easyreminder.action.settext"
    var ir = ImageReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val filter = IntentFilter(BROADCAST_ACTION)
        registerReceiver(ir, filter)

        Log.d("Broadcastyyy", intent?.getStringExtra("path")?:"null")
        val textView = findViewById<TextView>(R.id.textView_forpath)
        textView.setText(intent?.getStringExtra("path")?:"null")

    }


    class ImageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val path = intent?.getStringExtra("image_path")
            Log.d("Broadcastyyy", "Receiver has got a path")
            val activityIntent = Intent(context, MainActivity::class.java)
            activityIntent.putExtra("path", path)
            context?.startActivity(activityIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(ir)
    }
}