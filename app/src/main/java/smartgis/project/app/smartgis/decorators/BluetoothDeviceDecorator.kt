package smartgis.project.app.smartgis.decorators

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceDecorator(val device: BluetoothDevice, var isConnected: Boolean = false)