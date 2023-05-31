package com.dainekoichi.collaberaweatherexercise

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dainekoichi.collaberaweatherexercise.adapters.WeatherAppPagerAdapter
import com.dainekoichi.collaberaweatherexercise.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Weather ViewModel
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java].apply {
            initializeViewModel(this@MainActivity)
        }
        // Check location permission and trigger request for weather data
        checkLocationPermission()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            val titles = listOf(
                R.string.tab_weather,
                R.string.tab_history
            )
            val adapter = WeatherAppPagerAdapter(this@MainActivity)
            viewPager.adapter = adapter
            TabLayoutMediator(tabs, viewPager) { tabLayout, position ->
                tabLayout.text = getString(titles[position])
            }.attach()
        }
    }

    private fun checkLocationPermission() {
        val fineLocPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    fineLocPermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(fineLocPermission), 101
                )
            } else {
                val locationClient = LocationServices.getFusedLocationProviderClient(this)
                locationClient.lastLocation.addOnSuccessListener {
                    weatherViewModel.requestWeather(it, applicationContext)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}