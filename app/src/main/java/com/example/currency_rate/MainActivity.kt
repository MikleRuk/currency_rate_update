package com.example.currency_rate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

//    val edit_amount_tv = findViewById<EditText>(R.id.edit_amount_tv)
//    val calculatoin_end_tv = findViewById<TextView>(R.id.calculation_end_tv)



    val fragmentManager = supportFragmentManager

    // ниже созданы 3 переменные и метод onAttachFragment для скрытия и отобраджения кнопок при появлении фрагмента

    var back: Button? = null
    var enter_amoun : Button? = null
    var myFragment = calculate_frag()

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is calculate_frag){
            myFragment = fragment
            back?.visibility = View.VISIBLE
            enter_amoun?.visibility = View.GONE

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val notificationChannelHelper = NotificationChannelHelper()
        notificationChannelHelper.createNotificationChannel(this)



        enter_amoun = findViewById(R.id.btn_calculate_frag)

        back = findViewById<Button>(R.id.back_from_calc_frag)
        back?.visibility = View.GONE


        val tvResult = findViewById<TextView>(R.id.tv_result)
        val btnFetchData = findViewById<Button>(R.id.btn_fetch_data)


        //думаю, тут надо будет отрефакторить и запихнуть в отдельный метод
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 33)
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
            appendResult(tvResult, fetchDataFromNetwork())
        }

        btnFetchData.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                appendResult(tvResult, fetchDataFromNetwork())
            }
        }

    }

    suspend fun fetchDataFromNetwork(): String {
        val currencyRates = runBlocking { fetchCurrencyRates() }

        // Get date in format dd.MM.yyyy
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = dateFormatter.parse(currencyRates.date)

        // Find currency with isoCode = RUB
        val rubCurrency = currencyRates.currencyList.find { it.isoCode == "RUB" }

        // Convert ruble rate to KGS rate
        val kgsRate = rubCurrency?.value?.replace(',', '.')?.toDoubleOrNull() ?: 0.0

        val result = "\n ${date?.let { dateFormatter.format(it) }} \n $kgsRate"

        return result
    }

    fun appendResult(textView: TextView, result: String) {
        textView.text = "Курс рубля к сому с"
        runOnUiThread {
            textView.append(result)
        }
    }

    fun goToFragment(view: View) {

        // по методу на кнопке  вызов фрагмента
        supportFragmentManager.beginTransaction().replace(R.id.fragment_holder, calculate_frag.newInstance()).commit()
//        find_amount(fetchDataFromNetwork())
    }


    // по нажатию на кнопку Назад закрываем фрагмент, скрываем кнопку Назад и возвращаем кнопку Ввести сумму

    fun close_frag(view: View) {
        val fragment = fragmentManager.findFragmentById(R.id.fragment_holder)
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit()
            back?.visibility = View.GONE
            enter_amoun?.visibility = View.VISIBLE
        }
    }

//    suspend fun calcilation_of_the_entered_amount(){
//        val amount_result = edit_amount_tv.text.toString()
//        val result = (fetchDataFromNetwork().toInt() * amount_result.toInt()).toString()
//        calculatoin_end_tv.setText(result)
//
//    }




}

