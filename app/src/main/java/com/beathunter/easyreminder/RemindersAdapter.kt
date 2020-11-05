package com.beathunter.easyreminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RemindersAdapter : RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder> {

    private var numbElements = 0
    private var viewHolderCounter : Int

    constructor (numbElements : Int) {
        this.numbElements = numbElements
        viewHolderCounter = 0
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

        return viewHolder
    }

    //у созданных ViewHolder'ов меняем значения
    override fun onBindViewHolder(holder: RemindersViewHolder, position: Int) {
        holder.bind("date $viewHolderCounter",
                    "time $viewHolderCounter",
                    "rem $viewHolderCounter")
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