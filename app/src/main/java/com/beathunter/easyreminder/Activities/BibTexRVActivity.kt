package com.beathunter.easyreminder.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beathunter.easyreminder.Adapters.BibTexAdapter
import com.beathunter.easyreminder.R
import name.ank.lab4.BibDatabase
import java.io.InputStreamReader

class BibTexRVActivity : AppCompatActivity() {

    private lateinit var bibTexList : RecyclerView
    private lateinit var bibTexAdapter : BibTexAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bibtex_rv)
//        getSupportActionBar()?.hide()
        supportActionBar?.title = "BibTex"
        bibTexList = findViewById(R.id.rv_rems)

        //LayoutManager задаёт то, как элементы распределяются в RecyclerView
        //Юзаем LinearLayoutManager - все элементы будут идти друг за другом, построчно
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this)
        bibTexList.layoutManager = linearLayoutManager

        val bibFile = resources.openRawResource(R.raw.publication_ferro)

        val reader = InputStreamReader(bibFile)
        val bibDatabase = BibDatabase(reader)

        bibTexAdapter = BibTexAdapter(bibDatabase, this)
        bibTexList.adapter = bibTexAdapter
    }
}