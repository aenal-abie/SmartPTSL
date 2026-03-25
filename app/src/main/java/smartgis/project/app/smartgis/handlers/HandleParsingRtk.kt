package smartgis.project.app.smartgis.handlers

import android.location.Location
import android.util.Log
import org.greenrobot.eventbus.EventBus
import smartgis.project.app.smartgis.events.HrmsVrmsEvent
import smartgis.project.app.smartgis.events.LocationEvent
import smartgis.project.app.smartgis.events.QualityEvent
import smartgis.project.app.smartgis.events.SatelliteStatusEvent
import smartgis.project.app.smartgis.nmea.GpsSatellite
import smartgis.project.app.smartgis.nmea.NMEAHandler
import smartgis.project.app.smartgis.nmea.basic.BasicNMEAHandler

class HandleParsingRtk : NMEAHandler {
  override fun hdopVdopPdop(hdop: Float, vdop: Float, pdop: Float) {
    EventBus.getDefault().post(SatelliteStatusEvent(hdop, vdop, pdop))
  }

  override fun hrmsVrms(hrms: Double, vrms: Double, rms: Double) {
    EventBus.getDefault().post(HrmsVrmsEvent(hrms, vrms, rms))
  }

  override fun quality(
    quality: BasicNMEAHandler.FixQuality,
    origin: BasicNMEAHandler.QualityOrigin,
    altitude: Float
  ) {
    if (origin != BasicNMEAHandler.QualityOrigin.GLL)
      EventBus.getDefault().post(QualityEvent(quality.translate, origin.name, null))
  }

  override fun onLocation(location: Location?) {
    Log.i("parse", "location $location")
    EventBus.getDefault().post(LocationEvent(location))
  }

  override fun onStart() {
    Log.i("PARSE", "starting handler")
  }

  override fun onSatellites(satellites: MutableList<GpsSatellite>?) {
    Log.i("PARSE", "satellite ${satellites?.size}")
  }

  override fun onUnrecognized(sentence: String?) {
    Log.i("PARSE", "unrecognized data: $sentence")
  }

  override fun onBadChecksum(expected: Int, actual: Int) {
    Log.i("PARSE", "bad sum expected $expected, actual $actual")
  }

  override fun onException(e: Exception?) {
    Log.i("PARSE", "error: ${e?.localizedMessage}")
  }

  override fun onFinish() {
    Log.i("PARSE", "finishing handler")
  }
}