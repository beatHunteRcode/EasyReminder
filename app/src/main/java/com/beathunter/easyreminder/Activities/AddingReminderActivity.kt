package com.beathunter.easyreminder.Activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.ViewModels.AddingReminderViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.File
import java.util.*


class AddingReminderActivity : AppCompatActivity() {

    lateinit var addingReminderViewModel : AddingReminderViewModel

    lateinit var selectTimeButton : Button
    lateinit var selectDateButton : Button
    lateinit var reminding : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adding_reminder)
//        getSupportActionBar()?.hide()
        supportActionBar?.title = "Adding reminder"

        addingReminderViewModel = ViewModelProvider(this).get(AddingReminderViewModel::class.java)
        selectTimeButton = findViewById(R.id.set_time_button)
        selectDateButton = findViewById(R.id.set_date_button)
        reminding = findViewById(R.id.reminding_text)


        if (selectDateButton.text == "set date")
            selectDateButton.setText(addingReminderViewModel.getDateButtonText())
        if (selectTimeButton.text == "set time")
            selectTimeButton.setText(addingReminderViewModel.getTimeButtonText())
        if (reminding.text == "")
            reminding.text = addingReminderViewModel.getRemindingText()
    }

    fun onSelectDateButtonClick(v: View) {
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);

        // The ViewModelStore provides a new ViewModel or one previously created.
        selectDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val date = "${mDay}.${mMonth + 1}.${mYear}"
                selectDateButton.setText(date.trim());
                addingReminderViewModel.setDateButtonText(date)
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    fun onSelectTimeButtonClick(v: View) {
        var timeHour = 0
        var timeMinute = 0
        val timePickerDialog = TimePickerDialog (
            this,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                timeHour = hour
                timeMinute = minute
                var strHour = hour.toString()
                var strMinute = minute.toString()
                var time: String
                if (hour < 10) strHour = "0$strHour"
                if (minute < 10) strMinute = "0$strMinute"
                if (hour <= 12) time = "$strHour:$strMinute AM"
                else {
                    timeHour -= 12
                    strHour = timeHour.toString()
                    time = "$strHour:$strMinute PM"
                }
                selectTimeButton.setText(time)
                addingReminderViewModel.setTimeButtonText(time.trim())
            }, 12, 0, false
        )
        timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.updateTime(timeHour, timeMinute)
        timePickerDialog.show()
    }

    fun onCreateButtonClick(v : View) {
        if (    !selectDateButton.text.contains(".") ||
                !selectTimeButton.text.contains(":") ||
                reminding.text == null
        ) Toast.makeText(this, "Please, set all the fields!", Toast.LENGTH_LONG).show()
        else {
            addingReminderViewModel.setRemindingText(findViewById<TextView>(R.id.reminding_text).text.toString())
            //добавляем напоминание в JSON-файл
            val mapper = ObjectMapper()
            val file = File(MainScreenActivity.FILE_PATH)
            val node = mapper.readValue(
                file, JsonNode::class.java
            )
            val remsArrayNode: ArrayNode = node.findValue("reminders") as ArrayNode
            val addedNode: ObjectNode = remsArrayNode.addObject()
            addedNode
                .put("text", addingReminderViewModel.getRemindingText().trim())
                .put("date", addingReminderViewModel.getDateButtonText().trim())
                .put("time", addingReminderViewModel.getTimeButtonText().trim())
            mapper.writeValue(file, node)

            val intent: Intent = Intent(this, MainScreenActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent = Intent(this, MainScreenActivity::class.java)
        startActivity(intent)
        finish()
    }
}