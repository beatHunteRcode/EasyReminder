package com.beathunter.easyreminder.Activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import com.beathunter.easyreminder.NearestRemField
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.ViewModels.AddingReminderViewModel
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.IOError
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class AddingReminderActivity : AppCompatActivity() {

    val mainScreen = R.layout.activity_main

    lateinit var addingReminderViewModel : AddingReminderViewModel

    lateinit var selectTimeButton : Button
    lateinit var selectDateButton : Button
//    lateinit var reminding : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adding_reminder)
        getSupportActionBar()?.hide()

        addingReminderViewModel = ViewModelProvider(this).get(AddingReminderViewModel::class.java)
        selectTimeButton = findViewById(R.id.set_time_button)
        selectDateButton = findViewById(R.id.set_date_button)
//        reminding = findViewById(R.id.reminding_text)


        if (selectDateButton.text == "set date")
            selectDateButton.setText(addingReminderViewModel.getDateButtonText())
        if (selectTimeButton.text == "set time")
            selectTimeButton.setText(addingReminderViewModel.getTimeButtonText())
//        if (reminding.text.equals("write your reminding here"))
//            reminding.text = addingReminderViewModel.getRemindingText()
    }

    fun onSelectDateButtonClick(v: View) {
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);

        // The ViewModelStore provides a new ViewModel or one previously created.
        selectDateButton.setOnClickListener {
            val datPickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val date = "" + mDay + "/" + mMonth + "/" + mYear
                selectDateButton.setText(date);
                addingReminderViewModel.setDateButtonText(date)
            }, year, month, day)
            datPickerDialog.show()
        }
    }

    fun onSelectTimeButtonClick(v: View) {
        var timeHour: Int = 0
        var timeMinute: Int = 0
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
                    addingReminderViewModel.setTimeButtonText(time)
                }
            }, 12, 0, false
        )
        timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.updateTime(timeHour, timeMinute)
        timePickerDialog.show()
    }

    fun onCreateButtonClick(v : View) {
        addingReminderViewModel.setRemindingText(findViewById<TextView>(R.id.reminding_text).text.toString())
//        val nodeList = parseXML("activity_main.xml")

        //выходим из сцены создания напоминания и переключаемся на главное меню
        setContentView(mainScreen)
        //создаем виджет напоминания в главном меню в HorizontalScrollView
        NearestRemField.createIn(R.id.hLinLayout)
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

    fun createHelpReminder(layoutId : Int) {
        val mainLinLayout = findViewById<LinearLayout>(layoutId)

        val constraintLayout = ConstraintLayout(this)
        val constrParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        constraintLayout.layoutParams = constrParams
        constraintLayout.id = mainLinLayout.childCount + 1 + 1
        val set : ConstraintSet = ConstraintSet()

        val button = Button(this)
        constraintLayout.addView(button)
        val buttonParams = ConstraintLayout.LayoutParams(
            0, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams.dimensionRatio = "4:5"
        button.layoutParams = buttonParams
        button.background = resources.getDrawable(R.drawable.rectangle)
        button.id = mainLinLayout.childCount + 1


        set.clone(constraintLayout)
        set.connect(button.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP)
        set.connect(button.id, ConstraintSet.BOTTOM, constraintLayout.id, ConstraintSet.BOTTOM)
        set.connect(button.id, ConstraintSet.START, constraintLayout.id, ConstraintSet.START)
        set.connect(button.id, ConstraintSet.END, constraintLayout.id, ConstraintSet.END)
        set.applyTo(constraintLayout)

        val innerLinLayout : LinearLayout = LinearLayout(this)
        val innerLinLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        innerLinLayout.orientation = LinearLayout.VERTICAL
        innerLinLayout.elevation = 5.25f //=2dp
        innerLinLayout.layoutParams = innerLinLayoutParams
        constraintLayout.addView(innerLinLayout)

        val dateTextView : TextView = TextView(this)
        innerLinLayout.addView(dateTextView)
        val dateTextViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dateTextView.text = addingReminderViewModel.getDateButtonText()
        dateTextViewParams.topMargin = 20
        dateTextViewParams.bottomMargin = 20
        dateTextViewParams.marginStart = 20
        dateTextViewParams.marginEnd = 20
        dateTextView.layoutParams = dateTextViewParams

        val timeTextView : TextView = TextView(this)
        innerLinLayout.addView(timeTextView)
        val timeTextViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        timeTextViewParams.topMargin = 20
        timeTextView.text = addingReminderViewModel.getTimeButtonText()
        timeTextViewParams.topMargin = 20
        timeTextViewParams.bottomMargin = 20
        timeTextViewParams.marginStart = 20
        timeTextViewParams.marginEnd = 20
        timeTextView.layoutParams = timeTextViewParams

        val innerScrollView : ScrollView = ScrollView(this)
        innerLinLayout.addView(innerScrollView)
        val innerScrollViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        innerScrollViewParams.topMargin = 20
        innerScrollViewParams.bottomMargin = 40
        innerScrollViewParams.marginStart = 20
        innerScrollViewParams.marginEnd = 20
        innerScrollView.scrollBarSize = 0
        innerScrollView.isScrollbarFadingEnabled = false
        innerScrollView.layoutParams = innerScrollViewParams

        val scrollViewLinearLayout : LinearLayout = LinearLayout(this)
        val scrollViewLinearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        scrollViewLinearLayout.layoutParams = scrollViewLinearLayoutParams

        val remTextTextView : TextView = TextView(this)
        val remTextTextViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        remTextTextView.layoutParams = remTextTextViewParams
        remTextTextView.text = addingReminderViewModel.getRemindingText()
        scrollViewLinearLayout.addView(remTextTextView)
        innerScrollView.addView(scrollViewLinearLayout)

        mainLinLayout.addView(constraintLayout)

        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.activity_main, null, false)
        val constr : ConstraintLayout = findViewById(R.id.main_screen_constr_layout)
        constr.addView(layout)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}