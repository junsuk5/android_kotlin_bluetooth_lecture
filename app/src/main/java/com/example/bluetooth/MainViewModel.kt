package com.example.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class MainViewModel : ViewModel() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val myUUID = UUID.randomUUID()

    private val pairedDevices = arrayListOf<BluetoothDevice>()
    private val scanDevices = arrayListOf<BluetoothDevice>()

    val pairedDevicesLiveData = MutableLiveData<List<BluetoothDevice>>()
    val scanDevicesLiveData = MutableLiveData<List<BluetoothDevice>>()

    fun fetchPairedDevices() {
        pairedDevices.clear()

        // 연결된 기기 정보
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            Log.d(MainActivity.TAG, "onCreate: $deviceName $deviceHardwareAddress")

            addDiscoverDevice(device)
        }
    }

    fun startDiscovery() {
        bluetoothAdapter?.startDiscovery()
    }

    private fun addDiscoverDevice(device: BluetoothDevice) {
        pairedDevices.add(device)
        pairedDevicesLiveData.value = pairedDevices
    }

    fun addScanDevice(device: BluetoothDevice) {
        scanDevices.add(device)
        scanDevicesLiveData.value = scanDevices
    }

    fun connect(device: BluetoothDevice) {
        val socket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(myUUID)
        }

        bluetoothAdapter?.cancelDiscovery()

        socket?.use { socket ->
            socket.connect()


        }
    }
}