package com.beathunter.easyreminder.Activities

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.beathunter.easyreminder.Reminder
import com.beathunter.easyreminder.ViewModels.AddingReminderViewModel
import com.beathunter.easyreminder.ViewModels.EditingReminderViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import name.ank.lab4.BibDatabase
import java.io.File
import java.util.*

class EditingReminderActivity : AppCompatActivity() {

    lateinit var selectTimeButton : Button
    lateinit var selectDateButton : Button
    lateinit var reminding : TextView

    lateinit var oldText : String
    lateinit var oldDate : String
    lateinit var oldTime : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.beathunter.easyreminder.R.layout.editing_reminder)
//        getSupportActionBar()?.hide()
        supportActionBar?.title = "Editing reminder"

        selectTimeButton = findViewById(com.beathunter.easyreminder.R.id.set_time_button)
        selectDateButton = findViewById(com.beathunter.easyreminder.R.id.set_date_button)
        reminding = findViewById(com.beathunter.easyreminder.R.id.edit_reminding_text)

        selectDateButton.text = EditingReminderViewModel.getDateButtonText()
        selectTimeButton.text = EditingReminderViewModel.getTimeButtonText()
        reminding.text = EditingReminderViewModel.getRemindingText()

        oldDate = EditingReminderViewModel.getDateButtonText()
        oldTime = EditingReminderViewModel.getTimeButtonText()
        oldText = EditingReminderViewModel.getRemindingText()
    }

    fun onSelectDateButtonClick(v: View) {
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);

        // The ViewModelStore provides a new ViewModel or one previously created.
        selectDateButton.setOnClickListener {
            val datPickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val date = "${mDay}.${mMonth + 1}.${mYear}"
                selectDateButton.setText(date);
                EditingReminderViewModel.setDateButtonText(date)
            }, year, month, day)
            datPickerDialog.show()
        }
    }

    fun onSelectTimeButtonClick(v: View) {
        var timeHour = 0
        var timeMinute = 0
        val timePickerDialog = TimePickerDialog (
            this,
            R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                EditingReminderViewModel.setTimeButtonText(time)
            }, 12, 0, false
        )
        timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.updateTime(timeHour, timeMinute)
        timePickerDialog.show()
    }

    /**
     * Редактирует уже имеющееся напоминание
     *
     * Алгоритм работы:
     * 1. Парсится JSON-файл и составялется массив из всех напоминаний
     * 2. Из массива удаляется напоминание со старой информацией (до редактирования)
     * 3. В массив добавляется напоминенаие с новой (отредактированной) информацией
     * 4. Весь массив заносится в JSON-файл, который затем будет считываться и отображаться в MyRemindersActivity.kt
     * */
    fun onSaveButtonClick(v : View) {
        EditingReminderViewModel.setRemindingText(findViewById<TextView>(com.beathunter.easyreminder.R.id.edit_reminding_text).text.toString())
        //добавляем напоминание в JSON-файл
        val mapper = ObjectMapper()
        val file = File(MainActivity.FILE_PATH)
        val node = mapper.readValue(
            file, JsonNode::class.java
        )

        val datesList = node.findValue("reminders").findValues("date")
        val timesList = node.findValue("reminders").findValues("time")
        val textsList = node.findValue("reminders").findValues("text")

        //поиск индекса напоминания в JSON-файле для дальнейшего удаления
        var i = 0
        while (i < textsList.size) {
            if (textsList[i].asText() == oldText &&
                datesList[i].asText() == oldDate &&
                timesList[i].asText() == oldTime) break
            else i++
        }

        val remsArrayNode: ArrayNode = node.findValue("reminders") as ArrayNode
        remsArrayNode.remove(i)
        val addedNode: ObjectNode = remsArrayNode.addObject()
        addedNode
            .put("text", EditingReminderViewModel.getRemindingText())
            .put("date", EditingReminderViewModel.getDateButtonText())
            .put("time", EditingReminderViewModel.getTimeButtonText())
        mapper.writeValue(file, node)

        val intent: Intent = Intent(this, MyRemindersActivity::class.java)
        startActivity(intent)
        finish()
    }
}