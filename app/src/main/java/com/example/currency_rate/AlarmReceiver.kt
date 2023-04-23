package com.example.currency_rate

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Здесь можно вызывать нужную вам функцию
        // Например, если вы хотите отобразить уведомление, то можно вызвать функцию показа уведомления
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelHelper = NotificationChannelHelper()
        // Создаем уведомление
        val builder = NotificationCompat.Builder(context, notificationChannelHelper.channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Заголовок уведомления")
            .setContentText("Текст уведомления")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // Показываем уведомление
        notificationManager.notify(1, builder.build())
    }
}