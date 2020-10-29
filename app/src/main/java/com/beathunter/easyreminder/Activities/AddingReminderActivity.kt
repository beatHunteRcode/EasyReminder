package com.beathunter.easyreminder.Activities

import android.app.Activity
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
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.ViewModels.AddingReminderViewModel
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.IOError
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.min


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
        if (reminding.text.equals("write your reminding here"))
            reminding.text = addingReminderViewModel.getRemindingText()
    }

    fun onSelectDateButtonClick(v: View) {
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);

        // The ViewModelStore provides a new ViewModel or one previously created.
        selectDateButton.setOnClickListener {
            val datPickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val date = "" + mDay + "." + mMonth + "." + mYear
                selectDateButton.setText(date);
                addingReminderViewModel.setDateButtonText(date)
            }, year, month, day)
            datPickerDialog.show()
        }
    }

    fun onSelectTimeButtonClick(v: View) {
        var timeHour = 0
        var timeMinute = 0
        val timePickerDialog = TimePickerDialog (
            this,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
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
                    addingReminderViewModel.setTimeButtonText(time)
                }
            }, 12, 0, false
        )
        timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.updateTime(timeHour, timeMinute)
        timePickerDialog.show()
    }

    fun onCreateButtonClick(v : View) {
        val intent: Intent = Intent()
        intent.putExtra("dateText", addingReminderViewModel.getDateButtonText())
        intent.putExtra("timeText", addingReminderViewModel.getTimeButtonText())
        addingReminderViewModel.setRemindingText(findViewById<TextView>(R.id.reminding_text).text.toString())
        intent.putExtra("remText", addingReminderViewModel.getRemindingText())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onCreateButtonClick_old(v : View) {
//        val nodeList = parseXML("activity_main.xml")

        //выходим из сцены создания напоминания и переключаемся на главное меню
//        setContentView(mainScreen)
        //создаем виджет напоминания в главном меню в HorizontalScrollView
//        NearestRemField.createIn(R.id.hLinLayout)
//        createHelpReminder(R.id.hLinLayout)
        //переключаемся на сцену со списком всех напоминаний, чтобы добавить напоминание и туда
//        setContentView(myRemindersScreen)
        //добавляем напоминание в общий список
//        createHelpReminder(R.id.myrems_linlayout, nodeList)

        //возвращаемся на главное меню
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun printNodes(nodeList: NodeList) {
        for (i in 0..nodeList.length) {
            if (nodeList.item(i) is Element) {
                println((nodeList.item(i) as Element).tagName)
                if (nodeList.item(i).hasChildNodes()) printNodes(nodeList.item(i).childNodes)
            }
        }
    }

    private fun parseXML(fileName : String) : NodeList {
        val element : Element
        var xmlFile : File = File("")
        val factory : DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val builder : DocumentBuilder = factory.newDocumentBuilder()
        try {
            xmlFile = File(fileName)
        }
        catch (e : IOError) {
            println("No file " + fileName)
        }
        val document = builder.parse(xmlFile)
        element = document.documentElement
        return element.childNodes
//        val parserFactory : XmlPullParserFactory
//        parserFactory = XmlPullParserFactory.newInstance()
//        val parser : XmlPullParser = parserFactory.newPullParser()
//        val inputStream : InputStream = assets.open(fileName)
//        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
//        parser.setInput(inputStream, null)
//        return parser
    }
}