package com.beathunter.easyreminder.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.beathunter.easyreminder.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    private val FILE_NAME = "reminders.json"
    private lateinit var sPref : SharedPreferences
    private val JSON_REMINDINGS_FILE_PATH = "JSON_reminders_file_path"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_activity)

        createJSONFile()
    }


    //Создаёт JSON-файл, который будет хранить напоминания
    //Файл создаётся при первом заходе в приложение
    private fun createJSONFile() {
        var file = File(filesDir, FILE_NAME)
        var fileExists = file.exists()

        if (!fileExists) {
            val fos: FileOutputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
            val osw = OutputStreamWriter(fos)
            val text = "{\n" +
                    "    \"reminders\" : [\n" +
                    "    ]\n" +
                    "}"
            osw.write(text);
            osw.flush();
            osw.close();
            fos.close()
            Toast.makeText(this, "Saved to ${filesDir}/${FILE_NAME}", Toast.LENGTH_LONG).show()
            MainScreenActivity.FILE_PATH = "${filesDir}/${FILE_NAME}"

            sPref = getPreferences(Context.MODE_PRIVATE)
            val editor = sPref.edit()
            editor.putString(JSON_REMINDINGS_FILE_PATH, MainScreenActivity.FILE_PATH)
            editor.commit()
        }
        else {
            sPref = getPreferences(Context.MODE_PRIVATE)
            val jsonFilePath : String? = sPref.getString(JSON_REMINDINGS_FILE_PATH, "")
            MainScreenActivity.FILE_PATH = jsonFilePath.toString()

        }
    }

}