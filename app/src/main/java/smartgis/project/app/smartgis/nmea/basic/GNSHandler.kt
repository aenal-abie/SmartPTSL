package smartgis.project.app.smartgis.nmea.basic

interface GNSHandler {
  fun onGns(
    time: Long,
    latitude: Double,
    longitude: Double,
    altitude: Float,
    hdop: Float,
    indicator: ModeIndicator,
    satellites: Int
  )
}