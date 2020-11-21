package com.beathunter.easyreminder.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beathunter.easyreminder.Activities.EditingReminderActivity
import com.beathunter.easyreminder.QuickSort
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.Reminder
import com.beathunter.easyreminder.ViewModels.EditingReminderViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class RemindersAdapter(remsFile: File, context: Context) :
    RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder>() {

    private var numbElements = 0
    private var node : JsonNode
    private var remindingsFile : File = remsFile

    private var datesList : List<JsonNode>
    private var timesList : List<JsonNode>
    private var textsList : List<JsonNode>

    private val sortedArrRems : ArrayList<Reminder>

    private var i = 0
    var parentContext : Context

    init {
        //инициализация JSON-парсера
        val mapper = ObjectMapper()
        node = mapper.readValue(
            remindingsFile, JsonNode::class.java
        )
        textsList = node.findValue("reminders").findValues("text")
        datesList = node.findValue("reminders").findValues("date")
        timesList = node.findValue("reminders").findValues("time")

        var i = 0
        val arrRems : ArrayList<Reminder> = ArrayList()

        while (i < textsList.size) {
            val reminder = Reminder(
                textsList[i].asText(),
                datesList[i].asText(),
                timesList[i].asText()
            )
            arrRems.add(reminder)
            i++
        }
        sortedArrRems = sortRems(arrRems)

        numbElements = sortedArrRems.size

        parentContext = context
    }

    //создаем n-ое количество ViewHolder'ов
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindersViewHolder {
        val context : Context = parent.context
        val layoutId = R.layout.rem_list_item

        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutId, parent, false)

        val viewHolder : RemindersViewHolder =
            RemindersViewHolder(
                view,
                parentContext
            )
        viewHolder.dateTV.text = "date"
        viewHolder.timeTV.text = "time"
        viewHolder.textTV.text = "remText"

        return viewHolder
    }

    //у созданных ViewHolder'ов меняем значения
    override fun onBindViewHolder(holder: RemindersViewHolder, position: Int) {
        holder.bind(sortedArrRems[i].text, sortedArrRems[i].date, sortedArrRems[i].time)

        if (i < itemCount - 1) i++
        else i = 0
    }

    //подсчитывает общее количество элементов в списке
    override fun getItemCount(): Int {
        return numbElements
    }

    //обёртка для элемента списка
    class RemindersViewHolder(itemView: View, parentContext: Context) : RecyclerView.ViewHolder(itemView) {

        var dateTV : TextView = itemView.findViewById(R.id.tv_date)
        var timeTV : TextView = itemView.findViewById(R.id.tv_time)
        var textTV : TextView = itemView.findViewById(R.id.tv_text)

        init {
            itemView.setOnClickListener {
                EditingReminderViewModel.setDateButtonText(dateTV.text.toString())
                EditingReminderViewModel.setTimeButtonText(timeTV.text.toString())
                EditingReminderViewModel.setRemindingText(textTV.text.toString())
                val intent = Intent(parentContext, EditingReminderActivity::class.java)
                parentContext.startActivity(intent)
            }
        }
        //юзается для многократного обновления одного и того же элемента
        //(т.к. RecyclerView не создаёт новые элементы, а обновляет старые)
        fun bind(remText : String, dateText : String, timeText : String) {
            textTV.text = remText
            dateTV.text = dateText
            timeTV.text = timeText
        }
    }

    private fun sortRems(list : ArrayList<Reminder>) : ArrayList<Reminder> {
        QuickSort.quickSort(
            list,
            0,
            list.size - 1
        )
        val sortedList : ArrayList<Reminder> = list
        return sortedList
    }

}