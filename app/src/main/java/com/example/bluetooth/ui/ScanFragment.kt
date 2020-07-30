package com.example.bluetooth.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentScanBinding

class ScanFragment : Fragment(R.layout.fragment_scan) {
    lateinit var binding: FragmentScanBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentScanBinding.bind(view)
    }
}