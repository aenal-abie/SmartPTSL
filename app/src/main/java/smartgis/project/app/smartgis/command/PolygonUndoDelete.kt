package smartgis.project.app.smartgis.command

import com.google.android.gms.maps.model.Polygon

class PolygonUndoDelete(val polygon: Polygon, val undoAction: (Polygon) -> Unit) : Actionable {
  override fun act() {
    undoAction(polygon)
  }
}