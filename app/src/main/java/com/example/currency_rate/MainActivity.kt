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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

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
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
        // Устанавливаем таймер на каждый час
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
//            System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
            System.currentTimeMillis() + 60 * 1000,
            AlarmManager.INTERVAL_HOUR,
            alarmIntent
        )

    }

    private suspend fun fetchDataFromNetwork(tvResult: TextView) {
        val currencyRates = runBlocking { fetchCurrencyRates() }
        tvResult.text = "Курс рубля к сому с"

        // Get date in format dd.MM.yyyy
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = dateFormatter.parse(currencyRates.date)

        // Find currency with isoCode = RUB
        val rubCurrency = currencyRates.currencyList.find { it.isoCode == "RUB" }

        // Convert ruble rate to KGS rate
        val kgsRate = rubCurrency?.value?.replace(',', '.')?.toDoubleOrNull() ?: 0.0

        val result = "\n ${date?.let { dateFormatter.format(it) }} \n $kgsRate"
        runOnUiThread {
            tvResult.append(result)
        }
    }
}