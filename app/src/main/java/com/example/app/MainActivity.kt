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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class MainActivity : AppCompatActivity() {

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

        var URL: String = "";

        // Function to display Toast messages
        fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Click listener for the "Submit Cat" button
        submitCat.setOnClickListener {
            // Coroutine launched on the main thread
            lifecycleScope.launch(Dispatchers.Main) {
                // Clear the image
                Glide.with(this@MainActivity).clear(image)

                if (apiToggle.isChecked()) {
                    // API toggle is checked, load cat image from "cataas.com"
                    apiText.text = "API: cataas.com"
                    val imageUrl = "https://cataas.com/cat?timestamp=${System.currentTimeMillis()}"
                    Glide.with(this@MainActivity).load(imageUrl).into(image)
                } else {
                    // API toggle is not checked, load cat image from "thecatapi.com"
                    apiText.text = "API: thecatapi.com"
                    try {
                        // Make an asynchronous HTTP request for cat image
                        val response = httpReq("null", "https://api.thecatapi.com/v1/images/search")
                        Log.d("LOG!", response)

                        // Parse the JSON response to get the image URL
                        val imageUrl = jsonParserCat(response)
                        Log.d("LOG!", imageUrl)

                        // Load the cat image using Glide
                        Glide.with(this@MainActivity).load(imageUrl).into(image)
                        URL = imageUrl
                    } catch (e: Exception) {
                        // Handle exceptions, e.g., network errors
                        Log.e("LOG!", "Error fetching data", e)

                        // Show a Toast message for error notification
                        showToast("Something went wrong: Error fetching data")
                    }
                }
            }
        }

        // Click listener for the "Submit Dog" button
        submitDog.setOnClickListener {
            // Coroutine launched on the main thread
            lifecycleScope.launch(Dispatchers.Main) {
                // Update the API text
                apiText.text = "API: dog.ceo"
                try {
                    // Make an asynchronous HTTP request for dog image
                    val response = httpReq("", "https://dog.ceo/api/breeds/image/random")
                    val imageUrl = jsonParserDog(response)
                    Log.d("LOG!", imageUrl)

                    // Load the dog image using Glide
                    Glide.with(this@MainActivity).load(imageUrl).into(image)
                    URL = imageUrl
                } catch (e: Exception) {
                    // Handle exceptions, e.g., network errors
                    Log.e("LOG!", "Error fetching data", e)

                    // Show a Toast message for error notification
                    showToast("Something went wrong: Error fetching data")
                }
            }
        }

        download.setOnClickListener {
            if(URL == ""){
                    showToast("No image loaded")
            }
            else{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
                startActivity(intent)
            }

        }
    }
}

// Function to make an asynchronous HTTP request
private suspend fun httpReq(apiKey: String, url: String): String = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return@withContext try {
        // Execute the HTTP request synchronously and get the response
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                // Return the response body as a string
                response.body!!.string()
            } else {              // Throw an exception for unexpected HTTP codes
                throw IOException("Unexpected HTTP code: ${response.code}")
            }
        }
    } catch (e: IOException) {
        // Throw an exception for errors during the HTTP request
        throw IOException("Error during HTTP request", e)
    }
}

// Function to parse JSON response for cat image
private fun jsonParserCat(responseString: String): String {
    val gson = Gson()
    val catDataArray = gson.fromJson(responseString, Array<CatData>::class.java)

    // Check if the array is not null and not empty
    if (catDataArray != null && catDataArray.isNotEmpty()) {
        // Assuming you want the first element of the array
        val imageUrl = catDataArray[0].url
        Log.d("LOG", imageUrl)
        return imageUrl
    } else {
        // Handle the case when the array is null or empty
        return ""
    }
}

// Function to parse JSON response for dog image
private fun jsonParserDog(responseString: String): String {
    val gson = Gson()
    val dogData = gson.fromJson(responseString, DogData::class.java)

    // Return the dog image URL
    return dogData.message
}

