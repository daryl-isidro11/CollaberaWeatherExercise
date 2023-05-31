package com.dainekoichi.collaberaweatherexercise.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dainekoichi.collaberaweatherexercise.fragment.HistoryFragment
import com.dainekoichi.collaberaweatherexercise.fragment.MainFragment

class WeatherAppPagerAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> HistoryFragment.newInstance()
            else -> MainFragment.newInstance()
        }
    }
}