package com.example.currency_rate

import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [calculate_frag.newInstance] factory method to
 * create an instance of this fragment.
 */
class calculate_frag : Fragment() {

    private lateinit var edit_amount_tv: EditText
    private lateinit var calculation_end_tv: TextView
    private lateinit var calc_btn: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//      строчкой ниже "раздуваю" фрагмент и далее уже нахожу в нем нужные мне элементы
        val view = inflater.inflate(R.layout.fragment_calculate_frag, container, false)

        edit_amount_tv = view.findViewById(R.id.edit_amount_tv)
        calculation_end_tv = view.findViewById(R.id.calculation_end_tv)
        calc_btn = view.findViewById(R.id.calc_btn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calc_btn.setOnClickListener {
            finding_the_value_of_the_sum { result ->
                // Обновление calculation_end_tv.text в колбэке
                calculation_end_tv.text = "$result"
            }
        }
    }

    private fun finding_the_value_of_the_sum(callback: (String) -> Unit) {
        val amount_result = edit_amount_tv.text.toString()
        val dataFetcher = requireActivity() as? DataFetcher
        lifecycleScope.launch {
            try {
                val result = ((dataFetcher!!.fetchDataFromNetwork().toIntOrNull() ?: 0) * amount_result.toInt()).toString()
                callback(result) // Вызов колбэка с результатом
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Некорректный ввод", Toast.LENGTH_SHORT).show()
            }
        }
    }



//    private fun finding_the_value_of_the_sum(){
//        var mainActivity = MainActivity()
//        val amount_result = edit_amount_tv.text.toString()
//        try {
//            val result = (mainActivity.fetchDataFromNetwork()
//                .toInt() * amount_result.toInt()).toString()
//            calculation_end_tv.text = result
//        } catch (e: NumberFormatException) {
//            Toast.makeText(requireContext(), "Некорректный ввод", Toast.LENGTH_SHORT).show()
//        }
//    }

    companion object {
        @JvmStatic
        fun newInstance() = calculate_frag()
    }


//    private fun updateUI(amount_end_result: String) {
//        // Обновление UI в основном потоке
//        calculation_end_tv.text = "fafaf"
//    }


}

