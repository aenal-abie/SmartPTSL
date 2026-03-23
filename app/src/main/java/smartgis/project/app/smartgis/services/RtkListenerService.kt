package smartgis.project.app.smartgis.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import androidx.core.app.NotificationCompat
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import smartgis.project.app.smartgis.BluetoothDevices
import smartgis.project.app.smartgis.BluetoothDevices.Companion.CONNECT
import smartgis.project.app.smartgis.BluetoothDevices.Companion.ITEM_INDEX
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.events.*
//import smartgis.project.app.smartgis.handlers.HandleParsingRtk
//import smartgis.project.app.smartgis.handlers.RtkDeviceBluetoothHandler
//import smartgis.project.app.smartgis.ntrip.service.NTRIPService
import smartgis.project.app.smartgis.utils.hvrms
import smartgis.project.app.smartgis.utils.rtkQuality
import java.lang.ref.WeakReference

class RtkListenerService : Service() {

  companion object {
    const val BLUETOOTH_ADDRESS = "BLUETOOTH_ADDRESS"
    const val NOTIFICATION_ID = 1
  }

//  private var handler: RtkDeviceBluetoothHandler? = null
//  private var bluetoothAddress: String? = null
//  private val bluetoothManager: BluetoothManager = BluetoothManager.getInstance()
//  private var deviceInterface: SimpleBluetoothDeviceInterface? = null
  private var pendingIntent: PendingIntent? = null
  private var itemPosition: Int? = -1
  private var rtkQuality = ""
  private var hvrms = ""
  private var bt: BluetoothSPP? = null
  private var MODE = ""

  override fun onCreate() {
    super.onCreate()
    registerEvent()
    bt = BluetoothSPP(this) // sudah diinisialisasi disini
    bt?.setupService()
    bt?.startService(BluetoothState.DEVICE_OTHER)


    bt?.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
      override fun onDeviceDisconnected() {
      }

      override fun onDeviceConnectionFailed() {
        Log.i(javaClass.name, "Closing all connections to bluetooth devices!")
        onError()
      }

      override fun onDeviceConnected(name: String, address: String) {

      }
    })

    bt?.setOnDataReceivedListener { _, message ->
      onMessageReceived(message)
    }
  }

  @SuppressLint("ForegroundServiceType")
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//    bluetoothAddress = intent?.getStringExtra(BLUETOOTH_ADDRESS)
//
//    itemPosition = intent?.getIntExtra(ITEM_INDEX, -1)
//    val connect = intent?.getBooleanExtra(CONNECT, false)
//    log(connect.toString())
//    connect?.apply {
//      if (this) {
//        connectDeviceTo(bluetoothAddress)
//        handler = RtkDeviceBluetoothHandler.getInstance(HandleParsingRtk())
//      } else closeConnection()
//    }
//    pendingIntent = PendingIntent.getActivity(
//      this,
//      0,
//      intentFor<BluetoothDevices>(ITEM_INDEX to itemPosition, CONNECT to connect),
//      FLAG_IMMUTABLE
//    )
//    startForeground(NOTIFICATION_ID, getNotificationWith("Connecting to $bluetoothAddress", ""))
    return START_NOT_STICKY
  }


  val inMessenger = Messenger(IncomingHandler(this))

  class IncomingHandler internal constructor(target: RtkListenerService) :
    Handler() { // Handler of incoming messages from clients.
    private val mTarget: WeakReference<RtkListenerService>

    init {
      mTarget = WeakReference<RtkListenerService>(target)
    }


  }


  override fun onBind(intent: Intent?): IBinder? = null

  override fun onDestroy() {
    Log.i("rtk_service", "destroying service")
//    stopService(Intent(this, NTRIPService::class.java))
//    bluetoothManager.close()
    super.onDestroy()
  }

  private fun connectDeviceTo(address: String?) {
    Log.i(javaClass.name, "Connecting to $address")
    bt?.apply {
      connect(address)
    } ?: let {
      Log.e("bebe", "connectDeviceTo. bt state = bt")
    }
  }

  private fun closeConnection() {
    Log.i(javaClass.name, "Closing all connections to bluetooth devices!")
    EventBus.getDefault().post(RtkEvent(false, -1))
//    bluetoothManager.close()
    bt?.disconnect()
    unregisterEvent()
    stopSelf()
  }


  @SuppressLint("SetTextI18n", "ForegroundServiceType")
  private fun onMessageReceived(message: String) {
//    handler?.handleMessage(message)
    EventBus.getDefault().post(RtkDataEvent(message))
    var altitude: Double
    message.rtkQuality().apply {
      if (!isEmpty()) {
        rtkQuality = "Status: $this"
        EventBus.getDefault().post(QualityEvent(this, "GGA", 0.0))
      }
    }

    message.hvrms().apply {
      if (size > 0) {
        hvrms = "HRMS: %.3f - VRMS: %.3f".format(get(0), get(1))
      }
    }

    val bigContent = "$rtkQuality\n$hvrms$MODE"
//    startForeground(NOTIFICATION_ID, getNotificationWith("Terhubung ", bigContent))
  }

//  private fun getNotificationWith(content: String, bigText: String) =
//    NotificationCompat.Builder(this, CHANNEL_ID)
//      .setContentTitle("External GNSS Connection Status")
//      .setContentText(content)
//      .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
//      .setSmallIcon(R.drawable.logo)
//      .setContentIntent(pendingIntent)
//      .build()


  private fun onError() {
//    longToast("Gagal terhubung. Device tidak aktif atau koneksi ke device terhambat.")
    itemPosition?.apply {
      EventBus.getDefault().post(RtkEvent(false, this))
    }
    stopSelf()
  }

  fun log(msg: String?) {
    Log.i(javaClass.name, msg ?: "")
  }

  @Subscribe
  fun onRtcm3Event(event: Rtcm3Event) {
    bt?.apply {
      send(event.buffer, false)
    } ?: let {
      Log.e("bebe", "bt state = $bt")
    }
    this.MODE = "\nMode: NTRIP"
  }

  @Subscribe
  fun onModeEvent(event: ModeEvent) {
    this.MODE = event.mode
  }

  private fun unregisterEvent() {
    Log.i(javaClass.name, "unregistering event")
    if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
  }

  private fun registerEvent() {
    if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
  }

}