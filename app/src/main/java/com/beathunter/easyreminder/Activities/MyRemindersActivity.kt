package com.beathunter.easyreminder.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.Adapters.RemindersAdapter
import kotlinx.android.synthetic.main.my_reminders.*
import java.io.File

class MyRemindersActivity : AppCompatActivity() {

    private lateinit var remsList : RecyclerView
    private lateinit var remsAdapter : RemindersAdapter

    lateinit var remindingsFile : File

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

        remindingsFile = File(MainActivity.FILE_PATH)

        remsAdapter = RemindersAdapter(
            remindingsFile,
            this
        )
        remsList.adapter = remsAdapter

        floatingActionButton.setOnClickListener {
            val intent: Intent = Intent(this, BibTexRVActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}