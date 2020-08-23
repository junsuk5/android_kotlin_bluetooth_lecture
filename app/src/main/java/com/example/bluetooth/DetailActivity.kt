package com.example.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetooth.service.BluetoothLeService
import com.example.bluetooth.service.BluetoothLeService.LocalBinder
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*


class DetailActivity : AppCompatActivity() {
    companion object {
        val TAG = DetailActivity::class.java.simpleName
        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
        val UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT)
    }

    private var mBluetoothLeService: BluetoothLeService? = null
    private var mDeviceAddress: String? = null

    private var connected = false

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as LocalBinder).service

            mBluetoothLeService?.let { bluetoothService ->
                if (!bluetoothService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth")
                    finish()
                }
                // Automatically connects to the device upon successful start-up initialization.
                bluetoothService.connect(mDeviceAddress)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val device = intent.getParcelableExtra<BluetoothDevice>("BluetoothDevice")

        device?.let {
            mDeviceAddress = it.address
        }

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        mBluetoothLeService?.let {
            val result = it.connect(mDeviceAddress)
            Log.d(TAG, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        mBluetoothLeService = null
    }

    private val gattUpdateReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_GATT_CONNECTED -> {
                    Log.d(TAG, "onReceive: ACTION_GATT_CONNECTED")

                    connected = true
                    textView.text = "ACTION_GATT_CONNECTED"
//                    updateConnectionState(R.string.connected)
//                    (context as? Activity)?.invalidateOptionsMenu()
                }
                ACTION_GATT_DISCONNECTED -> {
                    Log.d(TAG, "onReceive: ACTION_GATT_DISCONNECTED")

                    connected = false
                    textView.text = "ACTION_GATT_DISCONNECTED"
//                    updateConnectionState(R.string.disconnected)
//                    (context as? Activity)?.invalidateOptionsMenu()
//                    clearUI()
                }
                ACTION_GATT_SERVICES_DISCOVERED -> {
                    Log.d(TAG, "onReceive: ACTION_GATT_SERVICES_DISCOVERED")

                    textView.text = "ACTION_GATT_SERVICES_DISCOVERED"

                    // Show all the supported services and characteristics on the
                    // user interface.
//                    displayGattServices(bluetoothLeService.supportedGattServices)
                }
                ACTION_DATA_AVAILABLE -> {
                    Log.d(TAG, "onReceive: ACTION_DATA_AVAILABLE")

                    textView.text = "ACTION_DATA_AVAILABLE"

//                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
                }
            }
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }
}