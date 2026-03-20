package smartgis.project.app.smartgis.decorators

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.firebase.firestore.DocumentReference

data class PolygonDecorator(val documentReference: DocumentReference, val polygon: Polygon)

data class PolylineDecorator(val documentReference: DocumentReference, val polyline: Polyline)

data class ShapeImportedDecorator(val properties: Map<String, Any>, val polygon: Polygon?)

data class ShapeWMSDecorator(val properties: WMSProperties, val polygon: List<LatLng>)

data class WMSProperties(
  val nama: String,
  val nib: String,
  val no_hak: String,
  val tipe_hak: String,
  val keterangan: String
)