package com.example.bluetooth.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bluetooth.DetailActivity
import com.example.bluetooth.MainViewModel
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentScanBinding
import com.example.bluetooth.ui.main.DeviceAdapter

class ScanFragment : Fragment(R.layout.fragment_scan) {
    private val mainViewModel by activityViewModels<MainViewModel>()

    lateinit var binding: FragmentScanBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentScanBinding.bind(view)

        val adapter = DeviceAdapter { device ->
//            mainViewModel.connect(device)

            startActivity(
                Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra("BluetoothDevice", device)

                }
            )
        }
        binding.recyclerView.adapter = adapter

        mainViewModel.scanDevicesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}