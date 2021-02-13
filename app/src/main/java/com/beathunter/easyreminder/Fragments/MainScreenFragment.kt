package com.beathunter.easyreminder.Fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beathunter.easyreminder.Activities.MainScreenActivity
import com.beathunter.easyreminder.Adapters.RemindersAdapter
import com.beathunter.easyreminder.QuickSort
import com.beathunter.easyreminder.R
import com.beathunter.easyreminder.Reminder
import com.beathunter.easyreminder.ViewModels.EditingReminderViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.fragment_main_screen.view.*
import kotlinx.android.synthetic.main.fragment_my_reminders.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var node : JsonNode
    private lateinit var datesList : List<JsonNode>
    private lateinit var timesList : List<JsonNode>
    private lateinit var textsList : List<JsonNode>

    private lateinit var remsList : RecyclerView
    private lateinit var remsAdapter : RemindersAdapter
    lateinit var remindingsFile : File

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
        (activity as AppCompatActivity).supportActionBar!!.title = "Easy Reminder"

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        view.add_rem_button.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainScreenFragment_to_addingReminderFragment)
        }

        view.my_rems_button.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainScreenFragment_to_myRemindersFragment)
        }

        initNearestRems(view)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //Каждый раз при создании Activity приложение считывает JSON-файл с напоминаниями и выводит
    //ближащие 5 напоминаний на главный экран
    private fun initNearestRems(v : View) {
        remsList = v.mainscreen_rv

        //LayoutManager задаёт то, как элементы распределяются в RecyclerView
        //Юзаем LinearLayoutManager - все элементы будут идти друг за другом, построчно
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(context)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        remsList.layoutManager = linearLayoutManager

        remindingsFile = File(MainScreenActivity.FILE_PATH)

        remsAdapter = RemindersAdapter(
            v,
            remindingsFile,
            1,
            context!!
        )
        remsList.adapter = remsAdapter
    }

    private fun sortRems(list : ArrayList<Reminder>) : ArrayList<Reminder> {
        QuickSort.quickSort(list, 0, list.size - 1)
        val sortedList : ArrayList<Reminder> = list
        return sortedList
    }
}