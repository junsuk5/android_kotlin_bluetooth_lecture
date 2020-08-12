package com.example.bluetooth.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.bluetooth.MainViewModel
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentScanBinding

class ScanFragment : Fragment(R.layout.fragment_scan) {
    private val mainViewModel by activityViewModels<MainViewModel>()

    lateinit var binding: FragmentScanBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentScanBinding.bind(view)

        val adapter = DeviceAdapter {
            mainViewModel.connect(it)
        }
        binding.recyclerView.adapter = adapter

        mainViewModel.scanDevicesLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}