package com.beathunter.easyreminder.Activities

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.beathunter.easyreminder.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.picasso_activity.view.*

class PicassoActivity : AppCompatActivity() {

    private val url = "https://sun9-32.userapi.com/impg/Z4-t3rPk-_iFbUB7m0UtEalGBUO89LaOzrurnw/bE6jbeWsmqw.jpg?size=604x403&quality=96&proxy=1&sign=aa92cca44ae28a8a42e1923454fd215a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Picasso"
        setContentView(R.layout.picasso_activity)

        val loadButton = findViewById<Button>(R.id.load_with_picasso_button)
        val imageView = findViewById<ImageView>(R.id.image_for_picasso)

        loadButton.setOnClickListener {
            Picasso.get().load(url).into(imageView)
        }
    }
}