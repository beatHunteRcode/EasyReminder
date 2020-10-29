package com.beathunter.easyreminder.Activities

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.beathunter.easyreminder.Fragments.BlankFragment
import com.beathunter.easyreminder.R


class MainActivity : AppCompatActivity() {

    val TAG : String = "lifecycle"
    val REQUEST_CODE_ADD_REM = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)

        val addRemButton = findViewById<Button>(R.id.add_rem_button)
        addRemButton.setOnClickListener {
            val intent: Intent = Intent(this, AddingReminderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivityForResult(intent, REQUEST_CODE_ADD_REM)
        }

        val myRemsButton = findViewById<Button>(R.id.my_rems_button)
        myRemsButton.setOnClickListener {
            val intent: Intent = Intent(this, MyRemindersActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        val nearest = findViewById<TextView>(R.id.nearest_textview)
        nearest.setOnClickListener {
            val intent: Intent = Intent(this, FragmentsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) return

        val dateText = data.getStringExtra("dateText")
        val timeText = data.getStringExtra("timeText")
        val remText = data.getStringExtra("remText")

        createNearestReminding(dateText!!, timeText!!, remText!!)
    }

    private fun createNearestReminding(dateText : String, timeText : String, remText : String) {
        val mainLinLayout = findViewById<LinearLayout>(R.id.hLinLayout)

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
        buttonParams.marginEnd = 20
        buttonParams.marginStart = 20
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

//        val inflater = LayoutInflater.from(this)
//        val layout = inflater.inflate(R.layout.activity_main, null, false)
//        val constr : ConstraintLayout = findViewById(R.id.main_screen_constr_layout)
//        constr.addView(layout)
    }


}
