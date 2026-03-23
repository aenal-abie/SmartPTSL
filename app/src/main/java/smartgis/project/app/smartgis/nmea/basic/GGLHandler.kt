package smartgis.project.app.smartgis.nmea.basic

interface GGLHandler {
  fun onGll(
    time: Long,
    latitude: Double,
    longitude: Double,
    arrivalQuality: ArrivalQuality,
    modeIndicator: ModeIndicator
  )
}

enum class ArrivalQuality {
  PERPENDICULAR, VOID
}

enum class ModeIndicator {
  AUTONOMUS, DIFFERENTIAL, ESTIMATED, MANUAL_INPUT, NOT_VALID, PRECISE, IRTK, FRTK, SIMULATOR
}

interface GSTHandler {
  fun onGst(hrms: Double, vrms: Double, rms: Double)
}