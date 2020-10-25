package com.beathunter.easyreminder.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.beathunter.easyreminder.R


class MainActivity : AppCompatActivity() {

    val TAG : String = "lifecycle"

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
    }
}
