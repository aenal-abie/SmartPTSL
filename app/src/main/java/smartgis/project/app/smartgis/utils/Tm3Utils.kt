package smartgis.project.app.smartgis.utils

import com.google.android.gms.maps.model.LatLng

object Tm3Utils {
  val mapEpsgToZone = mapOf(
    91.6600 to Zone("23830", "DGN95 / Indonesia TM-3 zone 46.2"),
    96.0000 to Zone("23831", "DGN95 / Indonesia TM-3 zone 47.1"),
    99.0000 to Zone("23832", "DGN95 / Indonesia TM-3 zone 47.2"),
    102.0000 to Zone("23833", "DGN95 / Indonesia TM-3 zone 48.1"),
    105.0000 to Zone("23834", "DGN95 / Indonesia TM-3 zone 48.2"),
    108.0000 to Zone("23835", "DGN95 / Indonesia TM-3 zone 49.1"),
    111.0000 to Zone("23836", "DGN95 / Indonesia TM-3 zone 49.2"),
    114.0000 to Zone("23837", "DGN95 / Indonesia TM-3 zone 50.1"),
    117.0000 to Zone("23838", "DGN95 / Indonesia TM-3 zone 50.2"),
    120.0000 to Zone("23839", "DGN95 / Indonesia TM-3 zone 51.1"),
    123.0000 to Zone("23840", "DGN95 / Indonesia TM-3 zone 51.2"),
    126.0000 to Zone("23841", "DGN95 / Indonesia TM-3 zone 52.1"),
    129.0000 to Zone("23842", "DGN95 / Indonesia TM-3 zone 52.2"),
    132.0000 to Zone("23843", "DGN95 / Indonesia TM-3 zone 53.1"),
    135.0000 to Zone("23844", "DGN95 / Indonesia TM-3 zone 53.2"),
    138.0000 to Zone("23845", "DGN95 / Indonesia TM-3 zone 54.1"),
    141.0000 to Zone("23845", "DGN95 / Indonesia TM-3 zone 54.1")
  )

  data class Zone(val code: String, val label: String) {
    fun epsgCode() = "EPSG:$code"
  }
}

fun LatLng.getTm3Zone(): Tm3Utils.Zone? {
  val longitudes = Tm3Utils.mapEpsgToZone.keys.toList()
  val gotIndex = (0 until Tm3Utils.mapEpsgToZone.keys.size - 1)
    .first { longitude in longitudes[it]..longitudes[it + 1] }
  return Tm3Utils.mapEpsgToZone[longitudes[gotIndex]]
}
