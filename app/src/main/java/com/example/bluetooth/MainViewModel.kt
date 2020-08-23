package com.example.bluetooth

import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Initializes Bluetooth adapter.
    private val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    private var mScanning = false

    private val pairedDevices = arrayListOf<BluetoothDevice>()
    private val scanDevices = arrayListOf<BluetoothDevice>()

    val pairedDevicesLiveData = MutableLiveData<List<BluetoothDevice>>()
    val scanDevicesLiveData = MutableLiveData<List<BluetoothDevice>>()

    var bluetoothGatt: BluetoothGatt? = null

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            addScanDevice(result.device)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(
                        "MainViewModel",
                        "onConnectionStateChange: ${bluetoothGatt?.discoverServices()}"
                    )
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i("MainViewModel", "Disconnected from GATT server.")
                }
            }
        }

        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
//            when (status) {
//                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
//                else -> Log.w(TAG, "onServicesDiscovered received: $status")
//            }
        }

        // Result of a characteristic read operation
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
//                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                }
            }
        }
    }

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

    fun leScan() {
//        bluetoothAdapter?.startDiscovery()
        viewModelScope.launch {
            mScanning = true
            bluetoothLeScanner.startScan(leScanCallback)
            Log.d("leScan", "leScan: start")
            delay(SCAN_PERIOD)
            leScanStop()
        }
    }

    fun leScanStop() {
        if (mScanning) {
            bluetoothLeScanner.stopScan(leScanCallback)
            mScanning = false
            Log.d("leScan", "leScan: end")
        }
    }

    private fun addDiscoverDevice(device: BluetoothDevice) {
        pairedDevices.add(device)
        pairedDevicesLiveData.value = pairedDevices
    }

    fun addScanDevice(device: BluetoothDevice) {
        if (!scanDevices.contains(device)) {
            scanDevices.add(device)
        }
        scanDevicesLiveData.value = scanDevices
    }

    fun connect(device: BluetoothDevice) {

//        val socket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            device.createRfcommSocketToServiceRecord(myUUID)
//        }
//
//        bluetoothAdapter?.cancelDiscovery()
//
//        socket?.use { socket ->
//            socket.connect()
//
//
//        }
        bluetoothGatt = device.connectGatt(getApplication(), false, gattCallback)

        leScanStop()
    }

    override fun onCleared() {
        leScanStop()
        super.onCleared()
    }

    companion object {
        private const val SCAN_PERIOD: Long = 10000
    }
}