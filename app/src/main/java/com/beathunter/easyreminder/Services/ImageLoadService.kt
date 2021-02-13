package com.beathunter.easyreminder.Services

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ImageLoadService : Service() {

    val BROADCAST_ACTION = "com.beathunter.easyreminder.action.settext"

    companion object {
        val MSG_INCOMING_URL = 1
        val MSG_OUTCOMING_LOCATION = 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val urlForCoroutines = intent?.getStringExtra("imageURL")

        CoroutineScope(Dispatchers.IO).launch {
            val image = loadImageForCoroutines(urlForCoroutines!!)
            val path = saveFile(image!!)
            val downloadedImageIntent = Intent()
            downloadedImageIntent.putExtra("image_path", path)
            downloadedImageIntent.action = BROADCAST_ACTION
            sendBroadcast(downloadedImageIntent)
        }

        return START_NOT_STICKY
    }

    fun loadImageForCoroutines(vararg urls : String) : Bitmap? {
        Log.d("Broadcastyyy", Thread.currentThread().name)
        val urldisplay = urls[0]
        var mIcon11 : Bitmap? = null
        try {
            val input = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(input)
        } catch (e : Exception) {
            Log.e("Error", e.message?:"null")
            e.printStackTrace()
        }
        Log.d("Broadcastyyy", "Image has been downloaded")
        return mIcon11
    }

    private fun saveFile(image : Bitmap) : String {
        val fileContainer = getOutputMediaFile()
        val fos = FileOutputStream(fileContainer)
        image.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()

        return fileContainer.absolutePath
    }

    //взял со StackOverFlow
    private fun getOutputMediaFile() : File {
        //получаем путь до файлового хранилища
        val storage = File(filesDir.absolutePath)
        //создаём директорию, если её не существует
        if (!storage.exists()) storage.mkdir()
        val fileName = "image.png"
        return File("${storage}/${fileName}")
    }


     inner class IncomingHandler() : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_INCOMING_URL -> {
                    Log.d("Broadcastyyy", "Message received. Received URL: ${msg.obj}")
                    val url = msg.obj.toString()
                    val responseMessenger = msg.replyTo
                    CoroutineScope(Dispatchers.IO).launch {
                        val image = loadImageForCoroutines(url)
                        val path = saveFile(image!!)
                        val message = Message.obtain(null, MSG_OUTCOMING_LOCATION, path)
                        responseMessenger.send(message)
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("Broadcastyyy", "Binding to service")
        return Messenger(IncomingHandler()).binder
    }

    override fun onRebind(intent: Intent?) {
        Log.d("Broadcastyyy", "Rebinding to service")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("Broadcastyyy", "Unbinding from service")
        return super.onUnbind(intent)
    }
}