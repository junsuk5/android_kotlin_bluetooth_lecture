package com.example.bluetooth.ui

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetooth.MainViewModel
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentPairedBinding
import com.example.bluetooth.databinding.ItemDeviceBinding

class PairedFragment : Fragment(R.layout.fragment_paired) {
    val mainViewModel by activityViewModels<MainViewModel>()

    lateinit var binding: FragmentPairedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPairedBinding.bind(view)

        val adapter = DeviceAdapter {

        }
        binding.recyclerView.adapter = adapter

        mainViewModel.pairedDevicesLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}

class DeviceAdapter(private val clickListener: (item: BluetoothDevice) -> Unit) :
    ListAdapter<BluetoothDevice, DeviceAdapter.DeviceViewHolder>(object :
        DiffUtil.ItemCallback<BluetoothDevice>() {
        override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: BluetoothDevice,
            newItem: BluetoothDevice
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    class DeviceViewHolder(val binding: ItemDeviceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        val viewHolder = DeviceViewHolder(ItemDeviceBinding.bind(view))
        view.setOnClickListener {
            clickListener.invoke(getItem(viewHolder.adapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.binding.device = getItem(position)
    }
}

