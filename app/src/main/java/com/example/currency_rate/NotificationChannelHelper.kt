package com.example.currency_rate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


class NotificationChannelHelper {
    var channelId = "channel_id"
    private set
    private val channelName = "my_first_chanel"
    private val channelDescription = "my_chanel_description"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = channelName
            val description = channelDescription
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}