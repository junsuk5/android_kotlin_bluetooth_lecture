package com.example.bluetooth.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bluetooth.MainViewModel
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentPairedBinding
import com.example.bluetooth.ui.main.DeviceAdapter

class PairedFragment : Fragment(R.layout.fragment_paired) {
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentPairedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPairedBinding.bind(view)

        val adapter = DeviceAdapter {

        }
        binding.recyclerView.adapter = adapter

        mainViewModel.pairedDevicesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}

