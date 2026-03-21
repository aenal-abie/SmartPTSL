package smartgis.project.app.smartgis.shape

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.utils.computeBearing
import smartgis.project.app.smartgis.utils.distanceTo
import smartgis.project.app.smartgis.utils.getNewCoordinateWith
import smartgis.project.app.smartgis.utils.toPositiveDegree
import java.util.*


fun GoogleMap.defaultCircle(): CircleOptions {
  var radius = (maxZoomLevel - cameraPosition.zoom + 1) * 1.0
  if (radius <= 4)
    radius /= 3
  return CircleOptions()
    .zIndex(1f)
    .radius(radius)
}

fun Context.defaultIconGenerator(drawable: Int): IconGenerator {
  val iconGenerator = IconGenerator(this)
  iconGenerator.setContentPadding(15, 0, 0, 2)
  iconGenerator.setTextAppearance(R.style.MarkerTitle)
  iconGenerator.setBackground(getDrawable(drawable))
  return iconGenerator
}

fun Context.defaultMarker(number: Int): MarkerOptions {
  return MarkerOptions()
    .icon(
      BitmapDescriptorFactory.fromBitmap(
        defaultIconGenerator(R.drawable.ic_map_marker_grey).makeIcon(
          number.toString()
        )
      )
    )
    .zIndex(1f)
    .title(number.toString())
}

fun Context.defaultMarkerPoint(number: Int, status: Int): MarkerOptions {
  val color = mutableListOf<Int>()
  color.add(R.drawable.ic_map_marker_red)
  color.add(R.drawable.ic_map_marker_green)
  color.add(R.drawable.ic_map_marker_yellow)
  color.add(R.drawable.ic_map_marker_blue)

  return MarkerOptions()
    .icon(BitmapDescriptorFactory.fromBitmap(defaultIconGenerator(color[status]).makeIcon(number.toString())))
    .zIndex(1f)
    .title(number.toString())
}


fun Context.generateLabelBetween(latLng0: LatLng, latLng1: LatLng): MarkerOptions {
  val distanceLabel = IconGenerator(this)
  val distance = latLng0.distanceTo(latLng1)
  val bearing = latLng0.computeBearing(latLng1)
  val center = latLng0.getNewCoordinateWith(
    bearing.toPositiveDegree(),
    (latLng0.distanceTo(latLng1) / 2).toDouble()
  )
  distanceLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.transparent))
  distanceLabel.setTextAppearance(R.style.PolyLineLabelTextStyle)
  distanceLabel.setContentPadding(0, 0, 0, 20)
  return MarkerOptions()
    .icon(
      BitmapDescriptorFactory.fromBitmap(
        distanceLabel.makeIcon(
          "%.2f m".format(
            Locale.ENGLISH,
            distance
          )
        )
      )
    )
    .position(center)
    .anchor(distanceLabel.anchorU, distanceLabel.anchorV)
}