package com.dainekoichi.collaberaweatherexercise.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dainekoichi.collaberaweatherexercise.WeatherViewModel
import com.dainekoichi.collaberaweatherexercise.adapters.WeatherHistoryAdapter
import com.dainekoichi.collaberaweatherexercise.databinding.FragmentHistoryBinding
import org.json.JSONObject

class HistoryFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel
    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    private val weatherResultObserver = Observer<JSONObject> {
        refreshList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshList()
        weatherViewModel.weatherResult.observe(viewLifecycleOwner, weatherResultObserver)

    }

    override fun onDestroy() {
        super.onDestroy()
        weatherViewModel.weatherResult.removeObserver(weatherResultObserver)
    }

    companion object {
        @JvmStatic
        fun newInstance(): HistoryFragment {
            return HistoryFragment().apply {
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

    private fun refreshList() {
        binding.apply {
            historyRecycler.apply {
                if (itemDecorationCount == 0) {
                    addItemDecoration(
                        DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
                    )
                }
                if (layoutManager == null)
                    layoutManager = LinearLayoutManager(requireActivity())
                val itemList = context.filesDir.list() ?: return
                val filteredList = itemList.filter {
                    it.toLongOrNull() != null
                }.toList()
                adapter = WeatherHistoryAdapter(filteredList)
            }
        }
    }
}