package com.beathunter.easyreminder.Services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder

import com.beathunter.easyreminder.Activities.MainScreenActivity
import com.beathunter.easyreminder.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ReminderService : Service() {

    val scope = CoroutineScope(Dispatchers.Default)

    private val CHANNEL_ID = "Reminder channel"
    private val NOTIFICATION_ID = 451

    override fun onCreate() {
        super.onCreate()
        val arrRems = MainScreenActivity.arrReminders
        scope.launch {
            while (true) {
                val currentMillis = System.currentTimeMillis()
                for (reminder in arrRems) {
                    if (currentMillis == reminder.millis) createNotification(reminder)
                }
            }
        }
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        val arrRems = MainActivity.arrReminders
//        scope.launch {
//            while (true) {
//                val currentMillis = System.currentTimeMillis()
//                for (reminder in arrRems) {
//                    if (currentMillis == reminder.millis) createNotification(reminder)
//                }
//            }
//        }
//
//        return START_STICKY
//    }

    private fun createNotification(reminder: Reminder) {
        val nm : NotificationManager = applicationContext.
                                        getSystemService(Context.NOTIFICATION_SERVICE) as
                                        NotificationManager

        var builder : Notification.Builder = Notification.Builder(this, CHANNEL_ID)
        builder
            .setContentText(reminder.text)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setLargeIcon(BitmapFactory.decodeResource(application.resources, R.drawable.ic_dialog_alert))
            .setTicker("REMINDER!")
            .setContentTitle("Reminder")
        createChannelIfNeeded(nm)
        nm.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createChannelIfNeeded(nm : NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(nc)
        }
    }
}