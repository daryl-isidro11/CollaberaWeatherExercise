package com.dainekoichi.collaberaweatherexercise

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*

class WeatherViewModel : ViewModel() {

    private lateinit var requestQueue: RequestQueue

    val weatherResult: LiveData<JSONObject>
        get() = _weatherResult
    private val _weatherResult = MutableLiveData<JSONObject>()

    fun initializeViewModel(context: Context) {
        requestQueue = Volley.newRequestQueue(context)
    }

    fun requestWeather(location: Location, context: Context) {
        val resources = context.resources
        val url = resources.getString(
            R.string.weather_api_url, location.latitude, location.longitude,
            resources.getString(R.string.api_key)
        )
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Put results in LiveData
                _weatherResult.value = response
                // Save results to file
                val filename = Calendar.getInstance().timeInMillis.toString()
                context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                    it.write(response.toString().toByteArray())
                }
            },
            { error ->
                try {
                    Log.e("???", String(error.networkResponse.data))
                } catch (ex: Exception) {
                    Log.e("???", "Cannot get error data")
                }
                _weatherResult.value = JSONObject()
            },
        )
        requestQueue.add(request)
    }
}