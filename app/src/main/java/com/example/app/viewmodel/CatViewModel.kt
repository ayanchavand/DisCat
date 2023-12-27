package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class CatViewModel: ViewModel() {


    var URL: String =""

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

    fun dogButtonClick(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit) {

        viewModelScope.launch {
            try {
                // Make an asynchronous HTTP request for dog image
                val response = httpReq("", "https://dog.ceo/api/breeds/image/random")
                val imageUrl = jsonParserDog(response)
                Log.d("LOG!", imageUrl)
                URL = imageUrl
                onSuccess(imageUrl)
            } catch (e: Exception) {
                // Handle exceptions, e.g., network errors
                Log.e("LOG!", "Error fetching data", e)
                onError("Error fetching data")

            }
        }
    }

    fun catButtonHandler(
        onSuccess: (String) -> Unit,
        onError: (String)  -> Unit){

        viewModelScope.launch {
            try {
                // Make an asynchronous HTTP request for cat image
                val response = httpReq("null", "https://api.thecatapi.com/v1/images/search")
                Log.d("LOG!", response)
                // Parse the JSON response to get the image URL
                val imageUrl = jsonParserCat(response)
                Log.d("LOG!", imageUrl)
                URL = imageUrl
                onSuccess(imageUrl)
            } catch (e: Exception) {
                // Handle exceptions, e.g., network errors
                Log.e("LOG!", "Error fetching data", e)
                onError("Error fetching data")
            }
        }

    }

}