package smartgis.project.app.smartgis

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import smartgis.project.app.smartgis.adapter.BluetoothRtkAdapter
import smartgis.project.app.smartgis.databinding.ActivityBluetoothDevicesBinding
import smartgis.project.app.smartgis.decorators.BluetoothDeviceDecorator
import smartgis.project.app.smartgis.events.RtkEvent

class BluetoothDevices : LoginRequiredActivity() {

    companion object {
        const val ITEM_INDEX = "ITEM_INDEX"
        const val CONNECT = "CONNECT"
        const val REQUEST_BLUETOOTH_PERMISSION = 1001
    }

    private lateinit var binding: ActivityBluetoothDevicesBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val data: MutableList<BluetoothDeviceDecorator> = mutableListOf()

    private val adapter: BluetoothRtkAdapter =
        BluetoothRtkAdapter(data) { position, isChecked ->

            // 👉 nanti di sini bisa kamu sambungkan ke BluetoothSocket (RTK)
            val device = data[position].device

            if (isChecked) {
                connectToDevice(device, position)
            } else {
                // disconnect logic kalau perlu
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBluetoothDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        setupRecyclerView()

        checkPermissionAndLoadDevices()
    }

    private fun setupRecyclerView() {
        binding.rvRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rvRecyclerView.adapter = adapter
    }

    // =========================
    // 🔐 PERMISSION HANDLER
    // =========================
    private fun checkPermissionAndLoadDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_BLUETOOTH_PERMISSION
                )
            } else {
                loadPairedDevices()
            }
        } else {
            loadPairedDevices()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                loadPairedDevices()
            }
        }
    }

    // =========================
    // 📡 LOAD DEVICE
    // =========================
    @SuppressLint("NotifyDataSetChanged")
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun loadPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices

        data.clear()

        pairedDevices?.forEach { device ->
            data.add(BluetoothDeviceDecorator(device))
        }

        adapter.notifyDataSetChanged()
    }

    // =========================
    // 🔌 CONNECT (RTK / GPS)
    // =========================
    private fun connectToDevice(device: BluetoothDevice, position: Int) {
//        ContextCompat.startForegroundService(
//            this,
//            intentFor<RtkListenerService>(
//                RtkListenerService.BLUETOOTH_ADDRESS to data[position].device.address,
//                ITEM_INDEX to position,
//                CONNECT to isChecked
//            )
//        )
    }

    // =========================
    // 🔄 EVENT BUS
    // =========================
    override fun onStart() {
        super.onStart()
        registerEvent()
    }

    override fun onStop() {
        unregisterEvent()
        super.onStop()
    }

    private fun registerEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun unregisterEvent() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onRtkEvent(event: RtkEvent) {
        if (event.position != -1) {
            data[event.position].isConnected = event.connect
            adapter.notifyItemChanged(event.position)
        }
    }

    // =========================
    // 🔙 BACK BUTTON
    // =========================
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}