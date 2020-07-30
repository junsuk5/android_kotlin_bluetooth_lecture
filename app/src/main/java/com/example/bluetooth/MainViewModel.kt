package com.example.bluetooth

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val pairedDevices = arrayListOf<BluetoothDevice>()

    val pairedDevicesLiveData = MutableLiveData<List<BluetoothDevice>>()

    fun addDiscoverDevice(device: BluetoothDevice) {
        pairedDevices.add(device)
        pairedDevicesLiveData.value = pairedDevices
    }
}