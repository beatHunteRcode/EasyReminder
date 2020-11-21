package com.beathunter.easyreminder.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beathunter.easyreminder.R
import name.ank.lab4.BibDatabase
import name.ank.lab4.BibEntry
import name.ank.lab4.Keys
import name.ank.lab4.Types

class BibTexAdapter(db : BibDatabase, context : Context) : RecyclerView.Adapter<BibTexAdapter.BibTexViewHolder>() {

    private var numbElements = 0

    private val itemsList : ArrayList<BibEntry> = ArrayList()
    var parentContext : Context

    var pagesList  = ArrayList<String>()
    var yearsList  = ArrayList<String>()
    var titlesList  = ArrayList<String>()
    var typesList = ArrayList<Types>()

    private var i = 0

    init {
        for (i in 0..104) {
            itemsList.add(db.getEntry(i))
        }

        for (item in itemsList) {
            pagesList.add(item.getField(Keys.PAGES))
            yearsList.add(item.getField(Keys.YEAR))
            titlesList.add(item.getField(Keys.TITLE))
            typesList.add(item.type)
        }
        numbElements = titlesList.size

        parentContext = context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BibTexViewHolder {
        val context : Context = parent.context
        val layoutId = R.layout.rem_list_item

        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutId, parent, false)

        val viewHolder = BibTexAdapter.BibTexViewHolder(
            view,
            parentContext
        )

        return viewHolder
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: BibTexViewHolder, position: Int) {

        if (titlesList[i] == null) titlesList[i] = ""
        if (pagesList[i] == null) pagesList[i] = ""
        if (yearsList[i] == null) yearsList[i] = ""
        if (typesList[i] == null) typesList[i] = Types.UNPUBLISHED

        holder.bind(titlesList[i], pagesList[i], yearsList[i], typesList[i])

        if (i < numbElements - 1) i++
        else i = 0
    }

    class BibTexViewHolder(itemView : View, parentContext: Context) : RecyclerView.ViewHolder(itemView) {
        var pagesTV : TextView = itemView.findViewById(R.id.tv_date)
        var yearTV : TextView = itemView.findViewById(R.id.tv_time)
        var titleTV : TextView = itemView.findViewById(R.id.tv_text)

        var context = parentContext

        var bg : FrameLayout = itemView.findViewById(R.id.rem_frame)

        fun bind(titleText : String, pagesText : String, yearText : String, type: Types) {
            titleTV.text = titleText
            pagesTV.text = pagesText
            yearTV.text = yearText

            if (type == Types.ARTICLE) bg.background = context.getDrawable(R.color.waters)
            if (type == Types.BOOK) bg.background = context.getDrawable(R.color.avocado)
            if (type == Types.BOOKLET) bg.background = context.getDrawable(R.color.cyberpunkBackground1)
            if (type == Types.CONFERENCE) bg.background = context.getDrawable(R.color.bee)
            if (type == Types.INBOOK) bg.background = context.getDrawable(R.color.bloody_red)
            if (type == Types.INCOLLECTION) bg.background = context.getDrawable(R.color.pinkie)
            if (type == Types.INPROCEEDINGS) bg.background = context.getDrawable(R.color.orange)
            if (type == Types.MANUAL) bg.background = context.getDrawable(R.color.purple)
            if (type == Types.MASTERSTHESIS) bg.background = context.getDrawable(R.color.swamp)
            if (type == Types.MISC) bg.background = context.getDrawable(R.color.yellow)
            if (type == Types.PHDTHESIS) bg.background = context.getDrawable(R.color.toxic)
            if (type == Types.PROCEEDINGS) bg.background = context.getDrawable(R.color.colorAccent)
            if (type == Types.TECHREPORT) bg.background = context.getDrawable(R.color.colorPrimaryDark)
            if (type == Types.UNPUBLISHED) bg.background = context.getDrawable(R.color.colorPrimary)

        }
    }
}