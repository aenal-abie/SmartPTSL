package smartgis.project.app.smartgis.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import smartgis.project.app.smartgis.BluetoothDevices.Companion.CONNECT
import smartgis.project.app.smartgis.BluetoothDevices.Companion.ITEM_INDEX
import smartgis.project.app.smartgis.events.*
import smartgis.project.app.smartgis.utils.hvrms
import smartgis.project.app.smartgis.utils.rtkQuality
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import android.bluetooth.*

class RtkListenerService : Service() {

    companion object {
        const val BLUETOOTH_ADDRESS = "BLUETOOTH_ADDRESS"
        const val NOTIFICATION_ID = 1
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var readThread: Thread? = null

    private var bluetoothAddress: String? = null
    private var itemPosition: Int = -1

    private var rtkQuality = ""
    private var hvrms = ""
    private var MODE = ""

    override fun onCreate() {
        super.onCreate()
        registerEvent()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        bluetoothAddress = intent?.getStringExtra(BLUETOOTH_ADDRESS)
        itemPosition = intent?.getIntExtra(ITEM_INDEX, -1) ?: -1
        val connect = intent?.getBooleanExtra(CONNECT, false) ?: false

        log("Connect: $connect")

        if (connect) {
            connectDeviceTo(bluetoothAddress)
        } else {
            closeConnection()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        log("Service destroyed")
        closeConnection()
        super.onDestroy()
    }

    // =========================
    // 🔹 CONNECT
    // =========================
    @SuppressLint("MissingPermission")
    private fun connectDeviceTo(address: String?) {
        try {
            log("Connecting to $address")

            val device = bluetoothAdapter?.getRemoteDevice(address)

            val uuid = device?.uuids?.getOrNull(0)?.uuid
                ?: UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

            bluetoothSocket = device?.createRfcommSocketToServiceRecord(uuid)

            bluetoothAdapter?.cancelDiscovery()
            bluetoothSocket?.connect()

            inputStream = bluetoothSocket?.inputStream
            outputStream = bluetoothSocket?.outputStream

            startReading()

            EventBus.getDefault().post(RtkEvent(true, itemPosition))
            log("Connected successfully")

        } catch (e: Exception) {
            e.printStackTrace()
            onError()
        }
    }

    // =========================
    // 🔹 READ DATA
    // =========================
    private fun startReading() {
        readThread = Thread {
            val buffer = ByteArray(1024)

            try {
                while (!Thread.currentThread().isInterrupted) {
                    val bytes = inputStream?.read(buffer) ?: -1

                    if (bytes > 0) {
                        val message = String(buffer, 0, bytes)
                        onMessageReceived(message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
        readThread?.start()
    }

    // =========================
    // 🔹 SEND DATA
    // =========================
    private fun sendData(data: ByteArray) {
        try {
            outputStream?.write(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // =========================
    // 🔹 RECEIVE MESSAGE
    // =========================
    private fun onMessageReceived(message: String) {

        EventBus.getDefault().post(RtkDataEvent(message))

        message.rtkQuality().apply {
            if (isNotEmpty()) {
                rtkQuality = "Status: $this"
                EventBus.getDefault().post(QualityEvent(this, "GGA", 0.0))
            }
        }

        message.hvrms().apply {
            if (isNotEmpty()) {
                hvrms = "HRMS: %.3f - VRMS: %.3f".format(get(0), get(1))
            }
        }

        val bigContent = "$rtkQuality\n$hvrms$MODE"
        log(bigContent)
    }

    // =========================
    // 🔹 DISCONNECT
    // =========================
    private fun closeConnection() {
        try {
            readThread?.interrupt()
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        EventBus.getDefault().post(RtkEvent(false, itemPosition))
        unregisterEvent()
        stopSelf()
    }

    // =========================
    // 🔹 ERROR HANDLER
    // =========================
    private fun onError() {
        log("Connection error")

        EventBus.getDefault().post(RtkEvent(false, itemPosition))
        stopSelf()
    }

    // =========================
    // 🔹 EVENTBUS
    // =========================
    @Subscribe
    fun onRtcm3Event(event: Rtcm3Event) {
        sendData(event.buffer)
        MODE = "\nMode: NTRIP"
    }

    @Subscribe
    fun onModeEvent(event: ModeEvent) {
        MODE = event.mode
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

    // =========================
    // 🔹 LOG
    // =========================
    private fun log(msg: String?) {
        Log.i("RTK_SERVICE", msg ?: "")
    }
}