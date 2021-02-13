package com.beathunter.easyreminder.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.beathunter.easyreminder.Activities.MainActivity
import com.beathunter.easyreminder.Activities.MainScreenActivity
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.ViewModels.AddingReminderViewModel
import com.beathunter.easyreminder.databinding.FragmentAddingReminderBinding
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.android.synthetic.main.fragment_adding_reminder.view.*
import kotlinx.android.synthetic.main.fragments_activity.*
import java.io.File
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddingReminderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddingReminderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var addingReminderViewModel : AddingReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        addingReminderViewModel = ViewModelProvider(this).get(AddingReminderViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = "Adding Reminder"

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_adding_reminder, container, false)

        view.set_date_button.setOnClickListener {
            onSetDateButtonClick(it, context!!)
        }

        view.set_time_button.setOnClickListener {
            onSetTimeButtonClick(it, context!!)
        }

        view.create_button.setOnClickListener {
            onCreateButtonClick(view, context!!)
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
         * @return A new instance of fragment AddingReminderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddingReminderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun onSetDateButtonClick(v: View, context: Context) {
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);


        // The ViewModelStore provides a new ViewModel or one previously created.
        val datePickerDialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
            val date = "${mDay}.${mMonth + 1}.${mYear}"
            v.set_date_button.setText(date.trim());
            addingReminderViewModel.setDateButtonText(date) }, year, month, day
        )
        datePickerDialog.show()

    }

    private fun onSetTimeButtonClick(v: View, context: Context) {
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
                v.set_time_button.setText(time)
                addingReminderViewModel.setTimeButtonText(time.trim())
            }, 12, 0, false
        )
        timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.updateTime(timeHour, timeMinute)
        timePickerDialog.show()
    }

    fun onCreateButtonClick(v : View, context: Context) {
        if (    !v.set_date_button.text.contains(".") ||
            !v.set_time_button.text.contains(":") ||
            v.reminding_text.text == null
        ) Toast.makeText(context, "Please, set all the fields!", Toast.LENGTH_LONG).show()
        else {
            addingReminderViewModel.setRemindingText(v.reminding_text.text.toString())
            //добавляем напоминание в JSON-файл
            val mapper = ObjectMapper()
            val file = File(MainScreenActivity.FILE_PATH)
            val node = mapper.readValue(
                file, JsonNode::class.java
            )
            val remsArrayNode: ArrayNode = node.findValue("reminders") as ArrayNode
            val addedNode: ObjectNode = remsArrayNode.addObject()
            addedNode
                .put("text", addingReminderViewModel.getRemindingText().trim())
                .put("date", addingReminderViewModel.getDateButtonText().trim())
                .put("time", addingReminderViewModel.getTimeButtonText().trim())
            mapper.writeValue(file, node)

            Navigation.findNavController(v).navigate(R.id.action_addingReminderFragment_to_mainScreenFragment)
        }

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
