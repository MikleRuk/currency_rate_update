package com.example.currency_rate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
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
        val notificationChannelHelper = NotificationChannelHelper()
        notificationChannelHelper.createNotificationChannel(this)


        val tvResult = findViewById<TextView>(R.id.tv_result)
        val btnFetchData = findViewById<Button>(R.id.btn_fetch_data)

        //думаю, тут надо будет отрефакторить и запихнуть в отдельный метод
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // Если текущее время уже больше 12 часов, то установим уведомление на следующий день
//        if (calendar.timeInMillis > System.currentTimeMillis()) { // а тут если меньше, отрабатывает, когда на эмуляторе 7 часов
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        lifecycleScope.launch(Dispatchers.IO) {
            fetchDataFromNetwork(tvResult)
        }

        btnFetchData.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                fetchDataFromNetwork(tvResult)
            }
        }

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