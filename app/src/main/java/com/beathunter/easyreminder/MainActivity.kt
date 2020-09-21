package com.beathunter.easyreminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.adding_reminder.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mainScreen = R.layout.activity_main;
    private val addingReminderScreen = R.layout.adding_reminder;
    private val myRemindersScreen =  R.layout.my_reminders;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()?.hide()
        setContentView(mainScreen)
    }

    fun onAddRemButtonClick(v: View) = setContentView(addingReminderScreen)

    fun onMyRemsButtonClick(v: View) = setContentView(myRemindersScreen)

    var timeHour: Int = 0
    var timeMinute: Int = 0


    fun onSelectDateButtonClick(v: View) {
        val selectDateButton : Button = findViewById(R.id.set_date_button)
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);

        set_date_button.setOnClickListener {
            val datPickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth , mDay ->
                set_date_button.setText("" + mDay + "/" + mMonth + "/" + mYear);
            }, year, month, day)
            datPickerDialog.show()
        }
    }

    fun onSelectTimeButtonClick(v: View) {
        val selectTimeButton : Button = findViewById(R.id.set_time_button)
        val timePickerDialog = TimePickerDialog (
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

    fun onCreateButtonPressed(v : View) {

    }

    override fun onBackPressed() = setContentView(mainScreen)
}
