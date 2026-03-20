package smartgis.project.app.smartgis.command

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon

class PolygonUndoSnapp(
  val polygon: Polygon,
  val points: List<LatLng>,
  val after: (Polygon) -> Unit
) : Actionable {
  override fun act() {
    polygon.points = points
    after(polygon)
  }
}