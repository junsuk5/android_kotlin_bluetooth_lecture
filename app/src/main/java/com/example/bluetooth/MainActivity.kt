package com.example.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val requestPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            Toast.makeText(this, "권한 성공", Toast.LENGTH_SHORT).show()
            bluetoothAdapter?.startDiscovery()
        } else {
            Toast.makeText(this, "권한 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.action_paired) {
                navController.navigate(R.id.pairedFragment)
            } else {
                navController.navigate(R.id.scanFragment)
            }
            true
        }

        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)


        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        // 연결된 기기 정보
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            Log.d(TAG, "onCreate: $deviceName $deviceHardwareAddress")

            viewModel.addDiscoverDevice(device)
        }

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let { action ->
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
                        val device: BluetoothDevice =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        val deviceName = device.name
                        val deviceHardwareAddress = device.address // MAC address

                        Log.d(TAG, "onReceive: $deviceName $deviceHardwareAddress")
                    }
                    else -> Log.d(TAG, "onReceive: else")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        const val REQUEST_ENABLE_BT = 1000
    }
}