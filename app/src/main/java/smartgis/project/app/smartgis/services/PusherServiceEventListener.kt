package smartgis.project.app.smartgis.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import smartgis.project.app.smartgis.R


class PusherServiceEventListener : FirebaseMessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    sendNotification(
      remoteMessage.notification?.title.toString(),
      remoteMessage.notification?.body.toString(),
      FLAG_IMMUTABLE
    )
  }


  private fun sendNotification(title: String, body: String, id: Int) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("com.smartptsl.panutan.ui.ChatActivity"))
    intent.putExtra("url", "com.smartptsl.panutan.ui.ChatActivity")
    val pendingIntent = PendingIntent.getActivity(
      this, 0, intent,
        PendingIntent.FLAG_ONE_SHOT or FLAG_IMMUTABLE
    )

    val channelId = getString(R.string.general_announcements)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder = NotificationCompat.Builder(this, channelId)
      .setSmallIcon(R.drawable.logo)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setAutoCancel(true)
      .setSound(defaultSoundUri)
      .setContentIntent(pendingIntent)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        channelId,
        "Channel human readable title",
        NotificationManager.IMPORTANCE_DEFAULT
      )
      notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(0, notificationBuilder.build())
  }

}