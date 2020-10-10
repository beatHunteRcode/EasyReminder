package com.beathunter.easyreminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.adding_reminder.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.IOError
import java.io.InputStream
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class MainActivity : AppCompatActivity() {

    val TAG : String = "lifecycle"

    private val mainScreen = R.layout.activity_main
    private val addingReminderScreen = R.layout.adding_reminder
    private val myRemindersScreen =  R.layout.my_reminders

    private val setDateButton = R.id.set_date_button
    private val setTimeButton = R.id.set_time_button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(mainScreen)

        Log.d(TAG, "Activity onCreate(): created")
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "Activity onStart(): started")
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "Activity onResume(): resumed")
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "Activity onPause(): paused")
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "Activity onStop(): stopped")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "Activity onDestroy(): destroyed")
    }

    fun onAddRemButtonClick(v: View) {

        setContentView(addingReminderScreen)
    }

    fun onMyRemsButtonClick(v: View) = setContentView(myRemindersScreen)

    var timeHour: Int = 0
    var timeMinute: Int = 0


    fun onSelectDateButtonClick(v: View) {
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

    fun onCreateButtonClick(v : View) {
//        val nodeList = parseXML("activity_main.xml")

        //выходим из сцены создания напоминания и переключаемся на главное меню
        setContentView(mainScreen)
        //создаем виджет напоминания в главном меню в HorizontalScrollView
        createHelpReminder(R.id.hLinLayout)
        //переключаемся на сцену со списком всех напоминаний, чтобы добавить напоминание и туда
//        setContentView(myRemindersScreen)
        //добавляем напоминание в общий список
//        createHelpReminder(R.id.myrems_linlayout, nodeList)
        //возвращаемся на главное меню
//        setContentView(mainScreen)
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

    fun createHelpReminder(layoutId : Int) {
        val mainLinLayout = findViewById<LinearLayout>(layoutId)
        mainLinLayout.childCount
        val inflater : LayoutInflater = LayoutInflater.from(this)
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
            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT
        )
        innerLinLayout.orientation = LinearLayout.VERTICAL
        innerLinLayout.elevation = 5.25f //=2dp
        innerLinLayout.layoutParams = innerLinLayoutParams
        constraintLayout.addView(innerLinLayout)

        val dateTextView : TextView = TextView(this)
        innerLinLayout.addView(dateTextView)
        val dateTextViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dateTextView.text = "DateTextView"
        dateTextViewParams.topMargin = 20
        dateTextViewParams.bottomMargin = 20
        dateTextViewParams.marginStart = 20
        dateTextViewParams.marginEnd = 20
        dateTextView.layoutParams = dateTextViewParams

        val timeTextView : TextView = TextView(this)
        innerLinLayout.addView(timeTextView)
        val timeTextViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        )
        timeTextViewParams.topMargin = 20
        timeTextView.text = "TimeTextView"
        timeTextViewParams.topMargin = 20
        timeTextViewParams.bottomMargin = 20
        timeTextViewParams.marginStart = 20
        timeTextViewParams.marginEnd = 20
        timeTextView.layoutParams = timeTextViewParams

        val innerScrollView : ScrollView = ScrollView(this)
        innerLinLayout.addView(innerScrollView)
        val innerScrollViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT
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
            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        )
        scrollViewLinearLayout.layoutParams = scrollViewLinearLayoutParams

        val remTextTextView : TextView = TextView(this)
        val remTextTextViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        )
        remTextTextView.layoutParams = remTextTextViewParams
        remTextTextView.text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        scrollViewLinearLayout.addView(remTextTextView)
        innerScrollView.addView(scrollViewLinearLayout)

        mainLinLayout.addView(constraintLayout)

    }
    override fun onBackPressed() = setContentView(mainScreen)

//    override fun onSaveInstanceState(outState: Bundle) {
//        setContentView(addingReminderScreen)
//        outState.putString("time", findViewById<Button>(R.id.add_rem_button).text.toString())
//        outState.putString("date", findViewById<Button>(setDateButton).text.toString())
//
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//
//        findViewById<Button>(setTimeButton).text = savedInstanceState.getString("time")
//        findViewById<Button>(setDateButton).text = savedInstanceState.getString("date")
//        setContentView(mainScreen)
//    }
}
