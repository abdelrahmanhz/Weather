package com.example.weather.alerts.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.weather.R
import com.example.weather.databinding.FragmentAlertDialogBinding
import com.example.weather.model.Utils.convertDateToMillis
import com.example.weather.model.Utils.convertTimeToLong
import com.example.weather.model.WeatherAlert
import java.text.SimpleDateFormat
import java.util.*


class AlertDialog : Fragment() {

    lateinit var binding: FragmentAlertDialogBinding
    var formate = SimpleDateFormat("dd/M/yyyy", Locale.forLanguageTag("en"))
    var timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val now = Calendar.getInstance()
    val alert: WeatherAlert? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentAlertDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFrom.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                val date = formate.format(selectedDate.time)
                if (alert != null) {
                    alert.startDate = date.convertDateToMillis()
                }
            },
                now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()}



        binding.btnTo.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                val date = formate.format(selectedDate.time)
                if (alert != null) {
                    alert.endDate = date.convertDateToMillis()
                }
            },
                now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.btnTime.setOnClickListener {
                        val timePicker = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            val selectedTime = Calendar.getInstance()
                            selectedTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                            selectedTime.set(Calendar.MINUTE,minute)
                            var time:String = timeFormat.format(selectedTime.time)
                            if (alert != null) {
                                alert.time = time
                            }
                            if (alert != null) {
                                alert.timeInMills = time.convertTimeToLong()
                            }
                            // btn_show.text = timeFormat.format(selectedTime.time)
                        },
                            now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),false)
                        timePicker.show()}

                    binding.btnSave.setOnClickListener {  }
        }


}