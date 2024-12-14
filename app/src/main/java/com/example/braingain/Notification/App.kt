package com.example.braingain.Notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App:Application() {
    final private val CHANNEL_ID1 = "CHANNEL_ID1"
    final private val CHANNEL_ID2 = "CHANNEL_ID2"

    override fun onCreate(){
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(CHANNEL_ID1, "Phone ka", NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "This is my High priority Notification"

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel1)
        }
    }
}
