package com.example.currency_rate

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val CHANNEL_ID = "chanel_id"

    var result : String = " "
    var kgsRate : Double? = 0.0
    var dateNotif : String? = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val tvResult = findViewById<TextView>(R.id.tv_result)
        val btnFetchData = findViewById<Button>(R.id.btn_fetch_data)

        lifecycleScope.launch(Dispatchers.IO) {
            fetchDataFromNetwork(tvResult)
        }

        btnFetchData.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                fetchDataFromNetwork(tvResult)
                createNotificationChannel() // помести сюда создание нотификашки и заработало
            }
        }




//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
//            PendingIntent.getBroadcast(this, 0, intent, 0)
//        }
//        // Устанавливаем таймер на каждый час
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
////            System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
//            System.currentTimeMillis() + 60 * 1000,
//            AlarmManager.INTERVAL_HOUR,
//            alarmIntent
//        )

    }

     suspend fun fetchDataFromNetwork(tvResult: TextView)  {
        val currencyRates = runBlocking { fetchCurrencyRates() }
        tvResult.text = "Курс рубля к сому с"

        // Get date in format dd.MM.yyyy
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = dateFormatter.parse(currencyRates.date)

         dateNotif = date.toString()

        // Find currency with isoCode = RUB
        val rubCurrency = currencyRates.currencyList.find { it.isoCode == "RUB" }

        // Convert ruble rate to KGS rate
        kgsRate = rubCurrency?.value?.replace(',', '.')?.toDoubleOrNull() ?: 0.0

        result = "\n ${date?.let { dateFormatter.format(it) }} \n $kgsRate"
        runOnUiThread {
            tvResult.append(result)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Курс рубля к сому ")
                .setContentText("$result")
                // хз что за реализация, разницы не увидел
//                .setStyle(NotificationCompat.BigTextStyle()
//                    .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            notificationManager.notify(1, builder.build())
        }
    }

}


// comment