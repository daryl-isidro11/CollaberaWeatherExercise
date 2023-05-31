package com.dainekoichi.collaberaweatherexercise.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dainekoichi.collaberaweatherexercise.R
import com.dainekoichi.collaberaweatherexercise.databinding.RecyclerWeatherHistoryBinding
import org.json.JSONObject
import java.text.DateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class WeatherHistoryAdapter(
    private val itemList: List<String>,
) : RecyclerView.Adapter<WeatherHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerWeatherHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filename = itemList[position]
        holder.binding.apply {
            val jsonObject: JSONObject
            root.context.openFileInput(filename).bufferedReader().use {
                jsonObject = JSONObject(it.readText())
            }
            val locale = Locale("", jsonObject.getJSONObject("sys").getString("country"))
            val dtFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            val tFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(ZoneId.of("UTC"))
            weatherTime.text = dtFormatter.format(Date(itemList[position].toLong()))
            cityTv.text = jsonObject.getString("name")
            countryTv.text = locale.displayCountry
            tempTextView.text =
                root.resources.getString(
                    R.string.display_degrees_celsius,
                    jsonObject.getJSONObject("main").getDouble("temp")
                )
            val sunriseTime: Instant
            val sunsetTime: Instant
            val secondsShift = jsonObject.getLong("timezone")
            jsonObject.getJSONObject("sys").apply {
                sunriseTime = Date(getLong("sunrise") * 1000L).toInstant()
                sunsetTime = Date(getLong("sunset") * 1000L).toInstant()
            }
            sunriseTextView.text = tFormatter.format(sunriseTime.plusSeconds(secondsShift))
            sunsetTextView.text = tFormatter.format(sunsetTime.plusSeconds(secondsShift))
            val description =
                jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
            val dayStartTime = Calendar.getInstance()
            dayStartTime[Calendar.HOUR_OF_DAY] = 5
            dayStartTime[Calendar.MINUTE] = 0
            val nightStartTime = Calendar.getInstance()
            nightStartTime[Calendar.HOUR_OF_DAY] = 18
            nightStartTime[Calendar.MINUTE] = 0
            val currentTime = Calendar.getInstance()
            currentTime.timeInMillis = filename.toLong()
            val newImage = when {
                description.contains("rain", true) ->
                    R.drawable.ic_weather_rain
                currentTime <= dayStartTime || currentTime >= nightStartTime ->
                    R.drawable.ic_weather_moon
                else ->
                    R.drawable.ic_weather_sun
            }
            recyclerWeatherIcon.setImageResource(newImage)
        }
    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(val binding: RecyclerWeatherHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}