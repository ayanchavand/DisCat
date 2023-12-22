package com.example.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
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
        val submitCat: Button = findViewById(R.id.submitCat)
        val submitDog: Button = findViewById(R.id.submitDog)
        val apiToggle: Switch = findViewById(R.id.apiToggle)
        val apiText: TextView = findViewById(R.id.apiText)


        submitCat.setOnClickListener {
            Glide.with(this).clear(image)
            if(apiToggle.isChecked()){
                apiText.text = "API: cataas.com"
                val imageUrl = "https://cataas.com/cat?timestamp=${System.currentTimeMillis()}"
                Glide.with(this).load(imageUrl).into(image)
            }

            else{
                apiText.text = "API: thecatapi.com"
                val response = httpReq("null", "https://api.thecatapi.com/v1/images/search")
                Log.d("LOG!", response)
                val imageUrl = jsonParserCat(response)
                Log.d("LOG!", imageUrl)
                Glide.with(this).load(imageUrl).into(image)
            }
        }

        submitDog.setOnClickListener {
            apiText.text = "API: dog.ceo"
            val response = httpReq("", "https://dog.ceo/api/breeds/image/random")
            val imageUrl = jsonParserDog(response)
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

