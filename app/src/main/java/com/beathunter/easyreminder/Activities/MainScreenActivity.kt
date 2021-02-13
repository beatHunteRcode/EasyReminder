package com.beathunter.easyreminder.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.beathunter.easyreminder.QuickSort
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.Reminder
import com.beathunter.easyreminder.Services.ReminderService
import com.beathunter.easyreminder.ViewModels.EditingReminderViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class MainScreenActivity : AppCompatActivity() {

    val TAG : String = "lifecycle"
    val REQUEST_CODE_ADD_REM = 1
    private val FILE_NAME = "reminders.json"
    private lateinit var sPref : SharedPreferences
    private val JSON_REMINDINGS_FILE_PATH = "JSON_reminders_file_path"
    private var numbElements = 0

    private lateinit var node : JsonNode
    private lateinit var datesList : List<JsonNode>
    private lateinit var timesList : List<JsonNode>
    private lateinit var textsList : List<JsonNode>

    companion object {
        lateinit var FILE_PATH : String
        public var arrReminders : ArrayList<Reminder> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)

        val addRemButton = findViewById<Button>(R.id.add_rem_button)
        addRemButton.setOnClickListener {
            val intent: Intent = Intent(this, AddingReminderActivity::class.java)
            startActivity(intent)
            finish()
        }

        val myRemsButton = findViewById<Button>(R.id.my_rems_button)
        myRemsButton.setOnClickListener {
            val intent: Intent = Intent(this, MyRemindersActivity::class.java)
            startActivity(intent)
            finish()
        }

        val nearest = findViewById<TextView>(R.id.nearest_textview)
        nearest.setOnClickListener {
            val intent: Intent = Intent(this, FragmentsActivity::class.java)
            startActivity(intent)
            finish()
        }

        val toLoadButton = findViewById<FloatingActionButton>(R.id.toLoadScreenFloatButton)
        toLoadButton.setOnClickListener {
            val intent: Intent = Intent(this, LoadImageActivity::class.java)
            startActivity(intent)
            finish()
        }

        val toServiceBtn = findViewById<FloatingActionButton>(R.id.toServiceBtn)
        toServiceBtn.setOnClickListener {
            val intent: Intent = Intent(this, ImageLoadServiceActivity::class.java)
            startActivity(intent)
            finish()
        }

        createJSONFile()
        initNearestRems()

        //запускаем Service для Notifications
        startService(Intent(this, ReminderService::class.java))
    }

    private fun createNearestReminding(remText : String, dateText : String, timeText : String) {
        val mainLinLayout = findViewById<LinearLayout>(R.id.hLinLayout)

        val constraintLayout = ConstraintLayout(this)
        val constrParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        constraintLayout.layoutParams = constrParams
        constraintLayout.id = mainLinLayout.childCount + 1 + 1
        val set : ConstraintSet = ConstraintSet()

        val textView = TextView(this)
        constraintLayout.addView(textView)
        val buttonParams = ConstraintLayout.LayoutParams(
            0, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams.dimensionRatio = "4:5"
        buttonParams.marginEnd = 20
        buttonParams.marginStart = 20
        textView.layoutParams = buttonParams
        textView.background = resources.getDrawable(R.drawable.rectangle)
        textView.id = mainLinLayout.childCount + 1


        set.clone(constraintLayout)
        set.connect(textView.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP)
        set.connect(textView.id, ConstraintSet.BOTTOM, constraintLayout.id, ConstraintSet.BOTTOM)
        set.connect(textView.id, ConstraintSet.START, constraintLayout.id, ConstraintSet.START)
        set.connect(textView.id, ConstraintSet.END, constraintLayout.id, ConstraintSet.END)
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
        dateTextView.text = dateText                   //ДАТА НАПОМИНАЛКИ
        dateTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
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
        timeTextView.text = timeText                   //ВРЕМЯ НАПОМИНАЛКИ
        timeTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
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
        innerLinLayout.setPadding(20, 0, 20, 0)
        innerScrollView.scrollBarSize = 0
        innerScrollView.isScrollbarFadingEnabled = false
        innerScrollView.layoutParams = innerScrollViewParams

        val scrollViewLinearLayout : LinearLayout = LinearLayout(this)
        val scrollViewLinearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        scrollViewLinearLayout.layoutParams = scrollViewLinearLayoutParams

        val remTextView : TextView = TextView(this)
        val remTextTextViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        remTextView.layoutParams = remTextTextViewParams
        remTextView.text = remText                 //ТЕКСТ НАПОМИНАЛКИ
        remTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        scrollViewLinearLayout.addView(remTextView)
        innerScrollView.addView(scrollViewLinearLayout)

        mainLinLayout.addView(constraintLayout)

        textView.setOnClickListener {
            EditingReminderViewModel.setRemindingText(remText)
            EditingReminderViewModel.setDateButtonText(dateText)
            EditingReminderViewModel.setTimeButtonText(timeText)

            val intent: Intent = Intent(this, EditingReminderActivity::class.java)
            startActivity(intent)
            finish()
        }
        remTextView.setOnClickListener {
            EditingReminderViewModel.setRemindingText(remText)
            EditingReminderViewModel.setDateButtonText(dateText)
            EditingReminderViewModel.setTimeButtonText(timeText)

            val intent: Intent = Intent(this, EditingReminderActivity::class.java)
            startActivity(intent)
            finish()
        }
        dateTextView.setOnClickListener {
            EditingReminderViewModel.setRemindingText(remText)
            EditingReminderViewModel.setDateButtonText(dateText)
            EditingReminderViewModel.setTimeButtonText(timeText)

            val intent: Intent = Intent(this, EditingReminderActivity::class.java)
            startActivity(intent)
            finish()
        }
        scrollViewLinearLayout.setOnClickListener {
            EditingReminderViewModel.setRemindingText(remText)
            EditingReminderViewModel.setDateButtonText(dateText)
            EditingReminderViewModel.setTimeButtonText(timeText)

            val intent: Intent = Intent(this, EditingReminderActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Создаёт JSON-файл, который будет хранить напоминания
    //Файл создаётся при первом заходе в приложение
    private fun createJSONFile() {
        var file = File(filesDir, FILE_NAME)
        var fileExists = file.exists()

        if (!fileExists) {
            val fos: FileOutputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
            val osw = OutputStreamWriter(fos)
            val text = "{\n" +
                    "    \"reminders\" : [\n" +
                    "    ]\n" +
                    "}"
            osw.write(text);
            osw.flush();
            osw.close();
            fos.close()
            Toast.makeText(this, "Saved to ${filesDir}/${FILE_NAME}", Toast.LENGTH_LONG).show()
            FILE_PATH = "${filesDir}/${FILE_NAME}"

            sPref = getPreferences(Context.MODE_PRIVATE)
            val editor = sPref.edit()
            editor.putString(JSON_REMINDINGS_FILE_PATH, FILE_PATH)
            editor.commit()
        }
        else {
            sPref = getPreferences(Context.MODE_PRIVATE)
            val jsonFilePath : String? = sPref.getString(JSON_REMINDINGS_FILE_PATH, "")
            FILE_PATH = jsonFilePath.toString()
        }
    }

    //Каждый раз при создании Activity приложение считывает JSON-файл с напоминаниями и выводит
    //ближащие 5 напоминаний на главный экран
    private fun initNearestRems() {
        val mapper = ObjectMapper()
        node = mapper.readValue(
            File(FILE_PATH), JsonNode::class.java
        )
        datesList = node.findValue("reminders").findValues("date")
        timesList = node.findValue("reminders").findValues("time")
        textsList = node.findValue("reminders").findValues("text")

        var i = 0
        val arrRems : ArrayList<Reminder> = ArrayList()

        while (i < textsList.size) {
            val reminder = Reminder(textsList[i].asText(), datesList[i].asText(), timesList[i].asText())
            arrRems.add(reminder)
            i++
        }
        val sortedArrRems = sortRems(arrRems)
        arrReminders.clear()
        arrReminders.addAll(sortedArrRems)
        i = 0
        while (i < sortedArrRems.size) {
            if (i == 5) break
            createNearestReminding(sortedArrRems[i].text, sortedArrRems[i].date, sortedArrRems[i].time)
            i++
        }
    }

    private fun sortRems(list : ArrayList<Reminder>) : ArrayList<Reminder> {
        QuickSort.quickSort(list, 0, list.size - 1)
        val sortedList : ArrayList<Reminder> = list
        return sortedList
    }
}
