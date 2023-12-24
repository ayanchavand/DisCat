package com.example.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LOG", "Hello")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image: ImageView =  findViewById(R.id.image)
        val submitCat: Button = findViewById(R.id.submitCat)
        val submitDog: Button = findViewById(R.id.submitDog)
        val apiToggle: Switch = findViewById(R.id.apiToggle)
        val apiText: TextView = findViewById(R.id.apiText)

        fun showToast( message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        submitCat.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                Glide.with(this@MainActivity).clear(image)
                if (apiToggle.isChecked()) {
                    apiText.text = "API: cataas.com"
                    val imageUrl = "https://cataas.com/cat?timestamp=${System.currentTimeMillis()}"
                    Glide.with(this@MainActivity).load(imageUrl).into(image)
                } else {
                    apiText.text = "API: thecatapi.com"
                    try {
                        val response = httpReq("null", "https://api.thecatapi.com/v1/images/search")
                        Log.d("LOG!", response)
                        val imageUrl = jsonParserCat(response)
                        Log.d("LOG!", imageUrl)
                        Glide.with(this@MainActivity).load(imageUrl).into(image)
                    } catch (e: Exception) {
                        // Handle exceptions, e.g., network errors
                        Log.e("LOG!", "Error fetching data", e)
                        showToast("Something went wrong: Error fetching data")
                    }
                }
            }
        }

        submitDog.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                apiText.text = "API: dog.ceo"
                try {
                    val response = httpReq("", "https://dog.ceo/api/breeds/image/random")
                    val imageUrl = jsonParserDog(response)
                    Log.d("LOG!", imageUrl)
                    Glide.with(this@MainActivity).load(imageUrl).into(image)
                } catch (e: Exception) {
                    // Handle exceptions, e.g., network errors
                    Log.e("LOG!", "Error fetching data", e)
                    showToast("Something went wrong: Error fetching data")
                }
            }
        }
    }
}


private suspend fun httpReq(apiKey: String, url: String): String = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return@withContext try {
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body!!.string()
            } else {
                throw IOException("Unexpected HTTP code: ${response.code}")
            }
        }
    } catch (e: IOException) {
        throw IOException("Error during HTTP request", e)
    }
}

private fun jsonParserCat(responseString: String): String{
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

private fun jsonParserDog(responseString: String): String{
    val gson = Gson()
    val dogData = gson.fromJson(responseString, DogData::class.java)

    return dogData.message;
}



