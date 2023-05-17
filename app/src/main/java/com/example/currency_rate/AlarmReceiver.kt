package com.example.currency_rate

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {



    override fun onReceive(context: Context, intent: Intent) {
        // Здесь можно вызывать нужную вам функцию
        // Например, если вы хотите отобразить уведомление, то можно вызвать функцию показа уведомления

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            showNotification(context)
        }

    }

    suspend fun showNotification(context: Context) {
        var mainActivity = MainActivity()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelHelper = NotificationChannelHelper()
        // Создаем уведомление
        val builder = NotificationCompat.Builder(context, notificationChannelHelper.channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Курс рубля к сому с")
            .setContentText(mainActivity.fetchDataFromNetwork())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // Показываем уведомление
        notificationManager.notify(1, builder.build())



    }

//    fun getStringResult(result: String)


}

// подтверждение того, что Миша внес изменение в проект и это сохранилось и отобразилось