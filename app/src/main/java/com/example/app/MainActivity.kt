package com.example.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LOG", "Hello")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image: ImageView =  findViewById(R.id.image)
        val submitCataas: Button = findViewById(R.id.submitCataas)
        val submitCatApi:Button = findViewById(R.id.submitCatapi)
        submitCataas.setOnClickListener{
            Glide.with(this).clear(image)

            val imageUrl = "https://cataas.com/cat?timestamp=${System.currentTimeMillis()}"
            Glide.with(this).load(imageUrl).into(image)
        }

        submitCatApi.setOnClickListener {
            val response = httpReq("null", "https://api.thecatapi.com/v1/images/search")
            Log.d("LOG!", response)
            val imageUrl = jsonParser(response)
            Log.d("LOG!", imageUrl)
            Glide.with(this).load(imageUrl).into(image)

        }
    }
}

private fun httpReq(apiKey: String, url: String): String{
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    var jsonResponse = ""

    client.newCall(request).enqueue(object :Callback{
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }
        //On successful request
        override fun onResponse(call: Call, response: Response) {
            if(response.isSuccessful){
                jsonResponse = response.body!!.string()
                Log.d("LOG", jsonResponse)

            }
        }
    })
    while(jsonResponse == ""){
        continue
    }
    return jsonResponse
}

private fun jsonParser(responseString: String): String{
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