package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import  android.widget.Toast
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image: ImageView =  findViewById(R.id.image)
        val submit: Button = findViewById(R.id.submit)

        submit.setOnClickListener{
            Glide.with(this).clear(image)

            val imageUrl = "https://cataas.com/cat?timestamp=${System.currentTimeMillis()}"
            Glide.with(this).load(imageUrl).into(image)
        }
    }
}