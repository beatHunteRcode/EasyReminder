package com.beathunter.easyreminder.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.beathunter.easyreminder.Activities.MainScreenActivity
import com.beathunter.easyreminder.Activities.MyRemindersActivity
import com.beathunter.easyreminder.Adapters.RemindersAdapter
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.ViewModels.EditingReminderViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.android.synthetic.main.fragment_adding_reminder.view.*
import kotlinx.android.synthetic.main.fragment_editing_reminder.view.*
import kotlinx.android.synthetic.main.fragment_editing_reminder.view.set_date_button
import kotlinx.android.synthetic.main.fragment_editing_reminder.view.set_time_button
import java.io.File
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditingReminderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditingReminderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    lateinit var selectTimeButton : Button
    lateinit var selectDateButton : Button
    lateinit var reminding : TextView

    lateinit var oldText : String
    lateinit var oldDate : String
    lateinit var oldTime : String

    lateinit var fm : FragmentManager
    lateinit var trans : FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = "Editing Reminder"

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_editing_reminder, container, false)

        selectTimeButton = view.set_time_button
        selectDateButton = view.set_date_button
        reminding = view.edit_reminding_text

        selectDateButton.text = EditingReminderViewModel.getDateButtonText()
        selectTimeButton.text = EditingReminderViewModel.getTimeButtonText()
        reminding.text = EditingReminderViewModel.getRemindingText()

        oldDate = EditingReminderViewModel.getDateButtonText()
        oldTime = EditingReminderViewModel.getTimeButtonText()
        oldText = EditingReminderViewModel.getRemindingText()


        view.set_date_button.setOnClickListener {
            onSetDateButtonClick(it, context!!)
        }

        view.set_time_button.setOnClickListener {
            onSetTimeButtonClick(it, context!!)
        }

        view.save_button.setOnClickListener {
            onSaveButtonClick(view)
        }

        view.remove_button.setOnClickListener {
            onRemoveButtonClick(view)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditingReminderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditingReminderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun onSetDateButtonClick(v: View, context: Context) {
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);

        // The ViewModelStore provides a new ViewModel or one previously created.
        selectDateButton.setOnClickListener {
            val datPickerDialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val date = "${mDay}.${mMonth + 1}.${mYear}"
                selectDateButton.setText(date);
                EditingReminderViewModel.setDateButtonText(date)
            }, year, month, day)
            datPickerDialog.show()
        }
    }

    fun onSetTimeButtonClick(v: View, context: Context) {
        var timeHour = 0
        var timeMinute = 0
        val timePickerDialog = TimePickerDialog (
            context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                timeHour = hour
                timeMinute = minute
                var strHour = hour.toString()
                var strMinute = minute.toString()
                var time: String
                if (hour < 10) strHour = "0$strHour"
                if (minute < 10) strMinute = "0$strMinute"
                if (hour <= 12) time = "$strHour:$strMinute AM"
                else {
                    timeHour -= 12
                    strHour = timeHour.toString()
                    time = "$strHour:$strMinute PM"
                }
                selectTimeButton.setText(time)
                EditingReminderViewModel.setTimeButtonText(time)
            }, 12, 0, false
        )
        timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.updateTime(timeHour, timeMinute)
        timePickerDialog.show()
    }

    /**
     * Редактирует уже имеющееся напоминание
     *
     * Алгоритм работы:
     * 1. Парсится JSON-файл и составялется массив из всех напоминаний
     * 2. Из массива удаляется напоминание со старой информацией (до редактирования)
     * 3. В массив добавляется напоминенаие с новой (отредактированной) информацией
     * 4. Весь массив заносится в JSON-файл, который затем будет считываться и отображаться в MyRemindersActivity.kt
     * */
    fun onSaveButtonClick(v : View) {
        EditingReminderViewModel.setRemindingText(v.edit_reminding_text.text.toString())
        //добавляем напоминание в JSON-файл
        val mapper = ObjectMapper()
        val file = File(MainScreenActivity.FILE_PATH)
        val node = mapper.readValue(
            file, JsonNode::class.java
        )

        val datesList = node.findValue("reminders").findValues("date")
        val timesList = node.findValue("reminders").findValues("time")
        val textsList = node.findValue("reminders").findValues("text")

        //поиск индекса напоминания в JSON-файле для дальнейшего удаления
        var i = 0
        while (i < textsList.size) {
            if (textsList[i].asText() == oldText &&
                datesList[i].asText() == oldDate &&
                timesList[i].asText() == oldTime) break
            else i++
        }

        val remsArrayNode: ArrayNode = node.findValue("reminders") as ArrayNode
        remsArrayNode.remove(i)
        val addedNode: ObjectNode = remsArrayNode.addObject()
        addedNode
            .put("text", EditingReminderViewModel.getRemindingText().trim())
            .put("date", EditingReminderViewModel.getDateButtonText().trim())
            .put("time", EditingReminderViewModel.getTimeButtonText().trim())
        mapper.writeValue(file, node)

        Navigation.findNavController(v).navigate(R.id.action_editingReminderFragment_to_myRemindersFragment)
    }

    fun onRemoveButtonClick(v : View) {
        EditingReminderViewModel.setRemindingText(v.edit_reminding_text.text.toString())
        //добавляем напоминание в JSON-файл
        val mapper = ObjectMapper()
        val file = File(MainScreenActivity.FILE_PATH)
        val node = mapper.readValue(
            file, JsonNode::class.java
        )

        val datesList = node.findValue("reminders").findValues("date")
        val timesList = node.findValue("reminders").findValues("time")
        val textsList = node.findValue("reminders").findValues("text")

        //поиск индекса напоминания в JSON-файле для дальнейшего удаления
        var i = 0
        while (i < textsList.size) {
            if (textsList[i].asText() == oldText &&
                datesList[i].asText() == oldDate &&
                timesList[i].asText() == oldTime) break
            else i++
        }

        val remsArrayNode: ArrayNode = node.findValue("reminders") as ArrayNode
        remsArrayNode.remove(i)
        mapper.writeValue(file, node)

        Navigation.findNavController(v).navigate(R.id.action_editingReminderFragment_to_myRemindersFragment)
    }

//    private fun getVisibleFragment() : Fragment? {
//        val fm = activity?.supportFragmentManager
//        val fragmentsList = fm?.fragments
//        for (fragment in fragmentsList!!) {
//            if (fragment != null && fragment.isVisible) return fragment
//        }
//        return null
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        val fm = activity?.supportFragmentManager
//        val trans = fm?.beginTransaction()
//        trans?.remove(getVisibleFragment()!!)
//        trans?.commit()
//        fm?.popBackStack()
//    }

}