package com.beathunter.easyreminder.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.beathunter.easyreminder.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class LoadImageActivity : AppCompatActivity() {

    private val urlForAsyncTask = "https://medialeaks.ru/wp-content/uploads/2020/06/laika-studios.jpg"
    private val urlForCoroutines = "https://sun9-25.userapi.com/OUVWMtar9YKLzhD3nz_OHD1Y7jJyLPqXO39noA/-ONaP92RrhM.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_image)


        val loadButtonAsyncTask = findViewById<Button>(R.id.loadButton_AsyncTask)
        loadButtonAsyncTask.setOnClickListener {
            DownloadImageTask(findViewById(R.id.image_to_load)).execute(urlForAsyncTask)
        }

        val loadButtonCoroutines = findViewById<Button>(R.id.loadButton_Coroutines)
        loadButtonCoroutines.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val image = loadImageForCoroutines(urlForCoroutines)
                launch(Dispatchers.Main) {
                    val bmImage = findViewById<ImageView>(R.id.image_to_load)
                    bmImage.setImageBitmap(image)
                }
            }
        }

        val toPicassoFloatingButton = findViewById<FloatingActionButton>(R.id.to_Picasso_button)
        toPicassoFloatingButton.setOnClickListener {
            val intent = Intent(this, PicassoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadImageForCoroutines(vararg urls : String) : Bitmap? {
        val urldisplay = urls[0]
        var mIcon11 : Bitmap? = null
        try {
            val input = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(input)
        } catch (e : Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }
        return mIcon11
    }

    class DownloadImageTask(val bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls : String) : Bitmap? {
            val urldisplay = urls[0]
            var mIcon11 : Bitmap? = null
            try {
                val input = java.net.URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(input)
            } catch (e : Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result : Bitmap) {
            bmImage.setImageBitmap(result)
        }
    }
}