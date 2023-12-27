package com.example.app.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.app.R
import com.example.app.db.AppDatabase
import com.example.app.db.LikedImage
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var catViewModel: CatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Log message for debugging
        Log.d("LOG", "Hello")

        // Standard onCreate setup
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "liked_images"
        ).build()

        // Access the LikedImageDao
        val likedImageDao = db.likedImageDao()

        setContentView(R.layout.activity_main)

        // View references
        val image: ImageView = findViewById(R.id.image)
        val submitCat: Button = findViewById(R.id.submitCat)
        val submitDog: Button = findViewById(R.id.submitDog)
        val apiText: TextView = findViewById(R.id.apiText)
        val download: ImageButton = findViewById(R.id.download)
        val like: Button = findViewById(R.id.like)

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

        like.setOnClickListener{
            val likedImage = LikedImage(image = catViewModel.URL)
            lifecycleScope.launch {
                if(catViewModel.URL.isBlank()) {
                    showToast("No image loaded, Cannot add to favs")
                } else {
                    db.likedImageDao().insert(likedImage)
                }

                // Assuming you have a LikedImageDao instance
                val allLikedImages: List<LikedImage> = likedImageDao.getAllLikedImages()

                // Iterate through the list and print or log the data
                for (likedImage in allLikedImages) {
                    Log.d("Database Data", "ID: ${likedImage.id}, Image URL: ${likedImage.image}")
                }
            }
        }
    }
}
