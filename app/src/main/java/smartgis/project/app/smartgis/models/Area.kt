package smartgis.project.app.smartgis.models

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Area(var workspaceId: String?, val points: @RawValue MutableList<GeoPoint>?) :
  Parcelable {

  fun closedPoints(): List<GeoPoint>? {
    if (points?.first() != points?.last()) {
      this.points?.add(points.size, points.first())
    }
    return points
  }

  constructor() : this(null, null)

  override fun toString(): String = "$workspaceId"
}