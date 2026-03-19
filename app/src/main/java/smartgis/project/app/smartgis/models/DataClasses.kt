package smartgis.project.app.smartgis.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.json.JSONObject
//import smartgis.project.app.smartgis.export.DataAndReferenceHolder
import smartgis.project.app.smartgis.utils.toTm3

data class RtkStatusHolder(val polygonId: String, val statusses: List<MutableMap<String, Any>?>)
data class ImportedHolder(val properties: JSONObject, val points: List<LatLng>)
data class GnssStatusHolder(
  var point: GeoPoint = GeoPoint(0.0, 0.0),
  var status: String = "",
  var origin: String = "",
  var hrms: Double = 0.0,
  var vrms: Double = 0.0,
  var rms: Double = 0.0,
  var hdop: Float = 0f,
  var vdop: Float = 0f,
  var pdop: Float = 0f,
  var altitude: Float = 0f
) {

  companion object {
    const val STATUS = "status"
    const val SOURCE = "sumber"
    const val HRMS = "hrms"
    const val VRMS = "vrms"
    const val RMS = "rms"
    const val HDOP = "hdop"
    const val VDOP = "vdop"
    const val PDOP = "pdop"
    const val POINT = "point"
    const val LAT = "lat"
    const val LON = "lon"
    const val URUT = "urut"
    const val ALTITUDE = "altitude"
    const val X = "x"
    const val Y = "y"
    val keys = mutableListOf(POINT, STATUS, SOURCE, HRMS, VRMS, RMS, HDOP, VDOP, PDOP)
  }

  fun data(): MutableMap<String, Any> {
    return mutableMapOf(
      POINT to point,
      STATUS to status,
      SOURCE to origin,
      HRMS to hrms,
      VRMS to vrms,
      RMS to rms,
      HDOP to hdop,
      VDOP to vdop,
      PDOP to pdop
    )
  }

  fun saveToDB(): MutableMap<String, Any> {
    val geopoint = GeoPoint(point.latitude, point.longitude)
    val latLng = LatLng(geopoint.latitude, geopoint.longitude)
    val tm3 = latLng.toTm3()
    return mutableMapOf(
      POINT to point,
      STATUS to status,
      SOURCE to origin,
      HRMS to hrms,
      X to tm3.first,
      Y to tm3.second,
      VRMS to vrms,
      RMS to rms,
      HDOP to hdop,
      VDOP to vdop,
      PDOP to pdop,
      ALTITUDE to altitude
    )
  }

  fun dataPointExport(urut: Int): MutableMap<String, Any> {
    val geopoint = GeoPoint(point.latitude, point.longitude)
    val latLng = LatLng(geopoint.latitude, geopoint.longitude)
    val tm3 = latLng.toTm3()
    return mutableMapOf(
      URUT to urut,
      LAT to point.latitude,
      LON to point.longitude,
      X to tm3.first,
      Y to tm3.second,
      STATUS to status,
      SOURCE to origin,
      HRMS to hrms,
      VRMS to vrms,
      RMS to rms,
      HDOP to hdop,
      VDOP to vdop,
      PDOP to pdop,
      ALTITUDE to altitude,
    )
  }


  fun reset() {
    point = GeoPoint(0.0, 0.0)
    status = ""
    origin = ""
    hrms = 0.0
    vrms = 0.0
    rms = 0.0
    hdop = 0f
    vdop = 0f
    pdop = 0f
    altitude = 0f
  }
}

data class ReferenceToGnssStatusHolder(val doc: DocumentReference, val data: GnssStatusHolder)

data class TableItem(
  val zone: String,
  val nub: String,
  val number: Int,
  val x: Double,
  val y: Double,
  val status: String,
  val hrms: Double,
  val vrms: Double,
  val rms: Double,
  val hdop: Double,
  val vdop: Double,
  val pdop: Double,
  val altitude: Double
)

//@Parcelize
//data class DelinasiIntentHolder(val delinasiHolders: @RawValue MutableList<DataAndReferenceHolder>) :
//  Parcelable

