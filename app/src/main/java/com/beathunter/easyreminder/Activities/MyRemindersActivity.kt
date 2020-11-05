package com.beathunter.easyreminder.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.RemindersAdapter
import com.beathunter.easyreminder.ViewModels.AddingReminderViewModel

class MyRemindersActivity : AppCompatActivity() {

    private lateinit var remsList : RecyclerView
    private lateinit var remsAdapter : RemindersAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_reminders)
//        getSupportActionBar()?.hide()
        supportActionBar?.title = "My Reminders"

        remsList = findViewById(R.id.rv_rems)

        //LayoutManager задаёт то, как элементы распределяются в RecyclerView
        //Юзаем LinearLayoutManager - все элементы будут идти друг за другом, построчно
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this)
        remsList.layoutManager = linearLayoutManager

        remsAdapter = RemindersAdapter(10)
        remsList.adapter = remsAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}