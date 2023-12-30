package com.example.app.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.app.R
import com.example.app.db.AppDatabase
import com.example.app.db.LikedImage
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var catViewModel: CatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val db = Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "liked_images"
        ).build()

        // Access the LikedImageDao
        val likedImageDao = db.likedImageDao()

        // View references
        val image: ImageView = view.findViewById(R.id.image)
        val submitCat: Button = view.findViewById(R.id.submitCat)
        val submitDog: Button = view.findViewById(R.id.submitDog)
        val apiText: TextView = view.findViewById(R.id.apiText)
        val download: Button = view.findViewById(R.id.download)
        val like: Button = view.findViewById(R.id.like)

        catViewModel = ViewModelProvider(this)[CatViewModel::class.java]

        // Function to display Toast messages
        fun showToast(message: String) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Click listener for the "Submit Cat" button
        submitCat.setOnClickListener {
            apiText.text = "API: thecatapi.com"
            catViewModel.catButtonHandler(
                onSuccess = { imageUrl ->
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
                    Glide.with(requireContext()).load(imageUrl).into(image)
                },
                onError = { error ->
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

        like.setOnClickListener {

            lifecycleScope.launch {
                // Assuming you have a LikedImageDao instance
                val allLikedImages: List<LikedImage> = likedImageDao.getAllLikedImages()
                var exists: Boolean = true

                // Iterate through the list and print or log the data
                for (likedImage in allLikedImages) {
                    Log.d("Database Data", "ID: ${likedImage.id}, Image URL: ${likedImage.image}")
                    if (likedImage.image == catViewModel.URL) {
                        exists = true
                        break
                    } else {
                        exists = false
                    }
                }

                if (catViewModel.URL.isNotBlank() and !exists) {
                    val likedImage = LikedImage(image = catViewModel.URL)
                    db.likedImageDao().insert(likedImage)

                } else {
                    showToast("No image loaded, Cannot add to favs")
                }

            }
        }

        return view
    }
}
