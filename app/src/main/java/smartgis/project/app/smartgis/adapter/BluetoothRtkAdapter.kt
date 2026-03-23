package smartgis.project.app.smartgis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import smartgis.project.app.smartgis.databinding.BluetoothDeviceItemBinding
import smartgis.project.app.smartgis.decorators.BluetoothDeviceDecorator

class BluetoothRtkAdapter(
    private val data: List<BluetoothDeviceDecorator>,
    private val onChecked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<BluetoothRtkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BluetoothDeviceItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onChecked)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(
        private val binding: BluetoothDeviceItemBinding,
        private val onChecked: (Int, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BluetoothDeviceDecorator) {
            binding.apply {
                tvDeviceName.text = item.device.name ?: "Unknown Device"
                tvDeviceAddress.text = item.device.address
                sToggleActivation.isChecked = item.isConnected

                sToggleActivation.setOnCheckedChangeListener(null) // reset listener dulu

                sToggleActivation.setOnCheckedChangeListener { _, isChecked ->
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        onChecked(pos, isChecked)
                    }
                }
            }
        }
    }
}