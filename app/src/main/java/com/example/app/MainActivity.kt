package com.example.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {

    private lateinit var catViewModel: CatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Log message for debugging
        Log.d("LOG", "Hello")

        // Standard onCreate setup
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View references
        val image: ImageView = findViewById(R.id.image)
        val submitCat: Button = findViewById(R.id.submitCat)
        val submitDog: Button = findViewById(R.id.submitDog)
        val apiToggle: Switch = findViewById(R.id.apiToggle)
        val apiText: TextView = findViewById(R.id.apiText)
        val download: ImageButton = findViewById(R.id.download)
        val like: Button = findViewById(R.id.like)

        var URL: String = ""

        catViewModel = ViewModelProvider(this)[CatViewModel::class.java]

        // Function to display Toast messages
        fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Click listener for the "Submit Cat" button
        submitCat.setOnClickListener {
            apiText.text = "API: thecatapi.com"
            catViewModel.catButtonHandler(
                onSuccess = {imageUrl ->
                    Glide.with(this).load(imageUrl).into(image)
                },
                onError = { error ->
                    // Show a Toast message for error notification
                    showToast("Something went wrong: $error")
                }
            )
        }

        // Click listener for the "Submit Dog" button
        submitDog.setOnClickListener {
            // Update the API text
            apiText.text = "API: dog.ceo"
            catViewModel.dogButtonClick(
                onSuccess = { imageUrl ->
                    Glide.with(this@MainActivity).load(imageUrl).into(image)},
                onError = {error ->
                    // Show a Toast message for error notification
                    showToast("Something went wrong: $error")
                }
            )
        }

        download.setOnClickListener {
            if (catViewModel.URL.isBlank()) {
                showToast("No image loaded")
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(catViewModel.URL))
                startActivity(intent)
            }
        }
    }
}
