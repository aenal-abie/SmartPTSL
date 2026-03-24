package smartgis.project.app.smartgis

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
//import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
//import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
//import dagger.hilt.android.HiltAndroidApp
//import dagger.hilt.android.HiltAndroidApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


//import io.realm.RealmConfiguration
//import org.jetbrains.anko.toast


@HiltAndroidApp
class App : Application() {

  companion object {
    const val CHANNEL_ID = "MAIN_STICKY_NOTIF_CHANNEL_ID"
  }

  override fun onCreate() {
    super.onCreate()
    createRtkNotification()
    createAnnouncementNotificationManager()
  }

  private fun createAnnouncementNotificationManager() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        getString(R.string.general_announcements),
        getString(R.string.announcements),
        NotificationManager.IMPORTANCE_DEFAULT
      )

      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(channel)
    }
  }

  private fun createRtkNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val serviceChannel = NotificationChannel(
        CHANNEL_ID,
        "RTK SERVICE CHANNEL",
        NotificationManager.IMPORTANCE_DEFAULT
      )

      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(serviceChannel)
    }
  }
}

//fun Context.onConnectionStateChanged(onChange: (Connectivity) -> Unit): Disposable? {
//  return ReactiveNetwork
//    .observeNetworkConnectivity(this)
//    .subscribeOn(Schedulers.io())
//    .observeOn(AndroidSchedulers.mainThread())
//    .subscribe({
//      onChange(it)
//    }, {
//      toast(it.localizedMessage).show()
//    }, {})
//}