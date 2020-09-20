package com.beathunter.easyreminder

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mainScreen = R.layout.activity_main;
    private val addingReminderScreen = R.layout.adding_reminder;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()?.hide()
        setContentView(mainScreen)
    }

    fun onAddRemButtonClick(v: View) = setContentView(addingReminderScreen)

    fun onMyRemsButtonClick(v: View) {

    }

    var timeHour: Int = 0
    var timeMinute: Int = 0

    fun onSelectTimeButtonClick(v: View) {
        var selectTimeButton : Button = findViewById(R.id.set_time_button)
        var timePickerDialog = TimePickerDialog (
            this,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    timeHour = hourOfDay
                    timeMinute = minute
                    //переводим выбранную дату и время в строку
                    val time: String =  "$timeHour:$timeMinute"
                    selectTimeButton.setText(time)
                }
            }, 12, 0, false
        )
        timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.updateTime(timeHour, timeMinute)
        timePickerDialog.show()
    }

    override fun onBackPressed() = setContentView(mainScreen)
}
