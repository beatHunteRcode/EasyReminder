package com.beathunter.easyreminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class RemindersAdapter : RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder> {

    private var numbElements = 0
    private var viewHolderCounter : Int
    private lateinit var node : JsonNode
    lateinit var remindingsFile : File

    //_____
    val textsList : List<String> = listOf(
        "android deadline", "bread", "driving", "conquer the world"
    )
    val datesList : List<String> = listOf(
        "8.11.2020", "12.12.2021", "8.7.2020","7.11.2020"
    )
    val timesList : List<String> = listOf(
        "12:30 PM", "5:10 pm", "12:23 AM", "10:03 AM"
    )
    var i = -1
    //_____
    constructor (numbElements : Int, remsFile : File) {
        this.numbElements = numbElements
        viewHolderCounter = 0
        remindingsFile = remsFile
    }

    //создаем n-ое количество ViewHolder'ов
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindersViewHolder {
        val context : Context = parent.context
        val layoutId = R.layout.rem_list_item

        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutId, parent, false)

        val viewHolder : RemindersViewHolder = RemindersViewHolder(view)
        viewHolder.dateTV.text = "date"
        viewHolder.timeTV.text = "time"
        viewHolder.textTV.text = "remText"

        viewHolderCounter++
        i++

        //инициализация JSON-парсера
//        val mapper = ObjectMapper()
//        node = mapper.readValue(
//                remindingsFile, JsonNode::class.java
//        )

        return viewHolder
    }

    //у созданных ViewHolder'ов меняем значения
    override fun onBindViewHolder(holder: RemindersViewHolder, position: Int) {
//        val textsList = node.findValue("remindings").findValues("text")
//        val datesList = node.findValue("remindings").findValues("date")
//        val timesList = node.findValue("remindings").findValues("time")
        holder.bind(datesList[i], timesList[i], textsList[i])
    }

    //подсчитывает общее количество элементов в списке
    override fun getItemCount(): Int {
        return numbElements
    }

    //обёртка для элемента списка
    class RemindersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var dateTV : TextView
        lateinit var timeTV : TextView
        lateinit var textTV : TextView

        //блок инициализации для основного (первичного) конструктора
        init {
            dateTV = itemView.findViewById(R.id.tv_date)
            timeTV = itemView.findViewById(R.id.tv_time)
            textTV = itemView.findViewById(R.id.tv_text)
        }

        //юзается для многократного обновления одного и того же элемента
        //(т.к. RecyclerView не создаёт новые элементы, а обновляет старые)
        fun bind(dateText : String, timeText : String, remText : String) {
            dateTV.text = dateText
            timeTV.text = timeText
            textTV.text = remText
        }
    }


}