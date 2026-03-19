package smartgis.project.app.smartgis.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import androidx.databinding.BindingAdapter
//import com.bumptech.glide.Glide
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import smartgis.project.app.smartgis.R
//import smartgis.project.app.smartgis.builder.GoogleOptions
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.loadWithGlide(url: String?) {
//  Glide.with(this)
//    .load(url ?: R.drawable.icon)
//    .thumbnail(.1f)
//    .into(this)
}

fun View.gone() {
  visibility = View.GONE
}

fun View.enable() {
  isEnabled = true
}

fun View.disable() {
  isEnabled = false
}

fun View.show() {
  visibility = View.VISIBLE
}

fun Context.rColor(int: Int): Int = ContextCompat.getColor(this, int)

fun Context.appPreference(): SharedPreferences =
  getSharedPreferences("SMART PTSL PREFERENCES", MODE_PRIVATE)

//fun Activity.googleSignInClient(): GoogleSignInClient = GoogleSignIn
//  .getClient(
//    this, GoogleOptions
//      .get(getString(R.string.default_web_client_id))
//  )

//fun Activity.logout() {
//  appPreference().edit().putBoolean("is_pemohon", false).apply()
//  FirebaseAuth.getInstance().signOut()
//  googleSignInClient().signOut()
//}

fun currentUser() = FirebaseAuth.getInstance().currentUser

fun timeStamp(map: MutableMap<String, Any>): Map<String, Any> {
  map["created_at"] = FieldValue.serverTimestamp()
  return map.toMap()
}

fun waktu(map: MutableMap<String, Any>): Map<String, Any> {
  map["waktu"] = FieldValue.serverTimestamp()
  return map.toMap()
}

fun Calendar.year(): Int = get(Calendar.YEAR)
fun Calendar.month(): Int = get(Calendar.MONTH)
fun Calendar.day(): Int = get(Calendar.DAY_OF_MONTH)

fun Calendar.year(year: Int): Calendar {
  set(Calendar.YEAR, year)
  return this
}

fun Calendar.month(month: Int): Calendar {
  set(Calendar.MONTH, month)
  return this
}

fun Calendar.day(day: Int): Calendar {
  set(Calendar.DAY_OF_MONTH, day)
  return this
}

@SuppressLint("SimpleDateFormat")
fun Date.toSimpleDate(): String = SimpleDateFormat("yyyy/MM/dd").format(this)

@SuppressLint("SimpleDateFormat")
fun String.toDate(): Date = SimpleDateFormat("yyyy/MM/dd").parse(this)

fun Bitmap.toBytes(): ByteArray {
  val baos = ByteArrayOutputStream()
  compress(Bitmap.CompressFormat.PNG, 90, baos)
  return baos.toByteArray()
}

fun String.prefix(prefix: String) = "${prefix}_${this}"

fun Date.toCalendar(): Calendar {
  val cal = Calendar.getInstance()
  cal.time = this
  return cal
}

fun String.youtubeUrl(): String = "https://www.youtube.com/watch?v=$this"

fun String.rtkQuality(): String {
  val splitter = TextUtils.SimpleStringSplitter(',')
  splitter.setString(this)
  val cmd = splitter.next()
  var qType = ""
  if (!cmd.isEmpty() && cmd.length > 2)
    if (cmd.substring(3, cmd.length) == "GGA") {
      splitter.next() //time
      splitter.next() //lat
      splitter.next() //latDir
      splitter.next() //lon
      splitter.next() //lonDir
      val quality = splitter.next()
      qType = quality.toInt().translateQuality()
    }
  return qType
}

fun String.altitude(): Double? {
  val splitter = TextUtils.SimpleStringSplitter(',')
  splitter.setString(this)
  val cmd = splitter.next()
  var altitude: Double? = null
  if (!cmd.isEmpty() && cmd.length > 2)
    if (cmd.substring(3, cmd.length) == "GGA") {
      splitter.next() //time
      splitter.next() //lat
      splitter.next() //latDir
      splitter.next() //lon
      splitter.next() //lonDir
      splitter.next() //quality
      splitter.next() //numberOfSatellites
      splitter.next() //numberOfSatellites
      var altitudeTemp = splitter.next()
      altitudeTemp?.let {
        if (it.isNotEmpty())
          altitude = it.toDouble()
      }
    }
  return altitude
}

fun Int.translateQuality(): String {
  return when (this) {
    0 -> "Invalid"
    1 -> "Stand Alone"
    2 -> "Differential"
    3 -> "PPS Fix"
    4 -> "Fixed" /*Real Time Kinematic*/
    5 -> "Float RTK"
    6 -> "Estimated (Dead Reckoning) 2.3 Feature"
    7 -> "Manual Input Mode"
    8 -> "Simulation Mode"
    else -> ""
  }
}

fun String.hvrms(): List<Double> {
  val splitter = TextUtils.SimpleStringSplitter(',')
  splitter.setString(this)
  val cmd = splitter.next()
  if (!cmd.isEmpty() && cmd.length > 2)
    if (cmd.substring(3, cmd.length) == "GST") {
      splitter.next() //1
      var rms = -0.1
      try {
        rms = splitter.next().toDouble() //2
      } catch (e: Exception) {
      }
      splitter.next() //3
      splitter.next() //4
      splitter.next() //5
      var latError = -1.0
      try {
        latError = splitter.next().toDouble() //6
      } catch (e: Exception) {
      }

      var longError = -1.0
      var vError = -1.0
      try {
        longError = splitter.next().toDouble() //7
      } catch (e: Exception) {
      }

      val splitterV = TextUtils.SimpleStringSplitter('*')
      splitterV.setString(splitter.next()) // 8
      try {
        vError = splitterV.next().toDouble() // 8.1
      } catch (e: Exception) {
      }
      val hrms = Math.sqrt(((Math.pow(latError, 2.0) + Math.pow(longError, 2.0)) / 2))
      return listOf(hrms, vError, rms)
    }
  return listOf()
}

fun Context.copyText(data: String) {
  val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val toCopy = ClipData.newPlainText(data, data)
  clipboard.setPrimaryClip(toCopy)
}

@SuppressLint("SimpleDateFormat")
fun Date.toLocalDateWithDayName(): String = SimpleDateFormat("EEE, dd MMM yyy").format(this)

fun String.noNull(): String = if (this == "null") "" else this


fun String.stringFromAssets(context: Context): String = try {
  context.assets.open(this).bufferedReader().use { it.readText() }
} catch (e: Exception) {
  e.printStackTrace()
  ""
}

fun Double.format(digits: Int): String = java.lang.String.format("%.${digits}f", this)


//@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean?) {
  with(view) {
    visible?.let {
      visibility = if (it) View.VISIBLE
      else View.GONE
    }
  }
}

@SuppressLint("SimpleDateFormat")
fun getDate(): String {
  val calendar = Calendar.getInstance()
  val date = calendar.time
  val format1 = SimpleDateFormat("yyyy/MM/dd")
  return format1.format(date)

}

@SuppressLint("Recycle")
fun Context.getFileName(uri: Uri): String {
  var result: String? = null
  if (uri.scheme == "content") {
    val cursor: Cursor? =
      contentResolver.query(uri, null, null, null, null)

    if (cursor != null && cursor.moveToFirst()) {
//      result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
  }
  return result ?: ""
}

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
  ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
  ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(
  permissionsArray: Array<String>,
  requestCode: Int
) {
  ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}