package com.dainekoichi.collaberaweatherexercise.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dainekoichi.collaberaweatherexercise.R
import com.dainekoichi.collaberaweatherexercise.WeatherViewModel
import com.dainekoichi.collaberaweatherexercise.databinding.FragmentMainBinding
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class MainFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val weatherResultObserver = Observer<JSONObject> {
        binding.apply {
            if (it.optJSONArray("weather") == null) {
                constraintLayout.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                Toast.makeText(
                    requireActivity().applicationContext,
                    getString(R.string.toast_load_failure), Toast.LENGTH_SHORT
                ).show()
                return@Observer
            }
            constraintLayout.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            val locale = Locale("", it.getJSONObject("sys").getString("country"))
            cityTextView.text = it.getString("name")
            countryTextView.text = locale.displayCountry
            temperatureTextView.text =
                getString(
                    R.string.display_degrees_celsius,
                    it.getJSONObject("main").getDouble("temp")
                )
            val tFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(ZoneId.of("UTC"))
            val sunriseTime: Instant
            val sunsetTime: Instant
            val secondsShift = it.getLong("timezone")
            it.getJSONObject("sys").apply {
                sunriseTime = Date(getLong("sunrise") * 1000L).toInstant()
                sunsetTime = Date(getLong("sunset") * 1000L).toInstant()
            }
            sunriseTextView.text = tFormatter.format(sunriseTime.plusSeconds(secondsShift))
            sunsetTextView.text = tFormatter.format(sunsetTime.plusSeconds(secondsShift))
            val description = it.getJSONArray("weather").getJSONObject(0).getString("description")
            val dayStartTime = Calendar.getInstance()
            dayStartTime[Calendar.HOUR_OF_DAY] = 5
            dayStartTime[Calendar.MINUTE] = 0
            val nightStartTime = Calendar.getInstance()
            nightStartTime[Calendar.HOUR_OF_DAY] = 18
            nightStartTime[Calendar.MINUTE] = 0
            val currentTime = Calendar.getInstance()
            val newImage = when {
                description.contains("rain", true) -> R.drawable.ic_weather_rain
                currentTime <= dayStartTime || currentTime >= nightStartTime -> R.drawable.ic_weather_moon
                else -> R.drawable.ic_weather_sun
            }
            weatherIcon.setImageResource(newImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherViewModel.weatherResult.observe(viewLifecycleOwner, weatherResultObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        weatherViewModel.weatherResult.removeObserver(weatherResultObserver)
    }

    companion object {
        @JvmStatic
        fun newInstance(): MainFragment {
            return MainFragment().apply {
                arguments = Bundle().apply {
                    // args
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}