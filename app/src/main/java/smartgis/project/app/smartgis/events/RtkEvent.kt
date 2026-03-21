package smartgis.project.app.smartgis.events

import android.location.Location

data class RtkEvent(val connect: Boolean, val position: Int)
data class LocationEvent(val location: Location?)
data class RtkDataEvent(val message: String)
data class QualityEvent(val quality: String?, val origin: String?, val altitude: Double?)
data class HrmsVrmsEvent(val hrms: Double, val vrms: Double, val rms: Double)
data class SatelliteStatusEvent(val hdop: Float, val vdop: Float, val pdop: Float)
data class SerialConnectionError(val throwable: Throwable)
data class Rtcm3Event(val buffer: ByteArray)
data class ModeEvent(val mode: String)
data class NtripEvent(val message: String)
data class MountpointEvent(val selected: Boolean)