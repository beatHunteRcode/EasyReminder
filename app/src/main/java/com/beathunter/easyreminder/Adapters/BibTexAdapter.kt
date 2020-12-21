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


class BibTexAdapter(db: BibDatabase, context: Context) : RecyclerView.Adapter<BibTexAdapter.BibTexViewHolder>() {

    private var numbElements = 0

    private val itemsList : ArrayList<BibEntry> = ArrayList()
    private var parentContext : Context

    private var pagesList  = ArrayList<String>()
    private var yearsList  = ArrayList<String>()
    private var titlesList  = ArrayList<String>()
    private var typesList = ArrayList<Types>()

    private var i = 0


    init {
        for (i in 0..104) {
            itemsList.add(db.getEntry(i))
        }

        for (item in itemsList) {
            pagesList.add(if (item.getField(Keys.PAGES) != null) item.getField(Keys.PAGES) else "")
            yearsList.add(if (item.getField(Keys.YEAR) != null) item.getField(Keys.YEAR) else "")
            titlesList.add(if (item.getField(Keys.TITLE) != null) item.getField(Keys.TITLE) else "")
            typesList.add(if (item.type != null) item.type else Types.UNPUBLISHED)
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

        holder.bind(titlesList[i], pagesList[i], yearsList[i], typesList[i])

        if (i < numbElements - 1) i++
        else i = 0
    }

    class BibTexViewHolder(itemView: View, parentContext: Context) : RecyclerView.ViewHolder(itemView) {
        private var pagesTV : TextView = itemView.findViewById(R.id.tv_date)
        private var yearTV : TextView = itemView.findViewById(R.id.tv_time)
        private var titleTV : TextView = itemView.findViewById(R.id.tv_text)

        var context = parentContext

        var bg : FrameLayout = itemView.findViewById(R.id.rem_frame)

        private val mapColorsForTypes : Map<Types, Int> = mapOf(
            Types.ARTICLE to R.color.waters,
            Types.BOOK to R.color.avocado,
            Types.BOOKLET to R.color.cyberpunkBackground1,
            Types.CONFERENCE to R.color.bee,
            Types.INBOOK to R.color.bloody_red,
            Types.INCOLLECTION to R.color.pinkie,
            Types.INPROCEEDINGS to R.color.orange,
            Types.MANUAL to R.color.purple,
            Types.MASTERSTHESIS to R.color.swamp,
            Types.MISC to R.color.yellow,
            Types.PHDTHESIS to R.color.toxic,
            Types.PROCEEDINGS to R.color.colorAccent,
            Types.TECHREPORT to R.color.colorPrimaryDark,
            Types.UNPUBLISHED to R.color.colorPrimary
        )

        fun bind(titleText: String, pagesText: String, yearText: String, type: Types) {
            titleTV.text = titleText
            pagesTV.text = pagesText
            yearTV.text = yearText

            bg.background = context.getDrawable(mapColorsForTypes[type]!!)
        }
    }
}