package smartgis.project.app.smartgis.utils

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.gavaghan.geodesy.Ellipsoid
import org.gavaghan.geodesy.GeodeticCalculator
import org.gavaghan.geodesy.GlobalCoordinates
//import org.jscience.geography.coordinates.LatLong
//import org.jscience.geography.coordinates.UTM
//import org.osgeo.proj4j.BasicCoordinateTransform
//import org.osgeo.proj4j.CRSFactory
//import org.osgeo.proj4j.ProjCoordinate
import smartgis.project.app.smartgis.utils.geometry.Circle
import smartgis.project.app.smartgis.utils.geometry.Vector2
//import javax.measure.unit.NonSI
import kotlin.math.absoluteValue


fun LatLng.distanceTo(destination: LatLng): Float {
  val results = FloatArray(1)
  Location.distanceBetween(
    latitude,
    longitude,
    destination.latitude,
    destination.longitude,
    results
  )
  return results[0]
}

fun LatLng.getNewCoordinateWith(bearing: Double, distance: Double): LatLng {
  val calculator = GeodeticCalculator().calculateEndingGlobalCoordinates(
    Ellipsoid.WGS84,
    GlobalCoordinates(latitude, longitude),
    bearing,
    distance
  )
  return LatLng(calculator.latitude, calculator.longitude)
}

fun LatLng.computeBearing(otherPoint: LatLng): Double {
  val fromLat = Math.toRadians(latitude)
  val fromLng = Math.toRadians(longitude)
  val toLat = Math.toRadians(otherPoint.latitude)
  val toLng = Math.toRadians(otherPoint.longitude)
  val dLng = toLng - fromLng
  val heading = Math.atan2(
    Math.sin(dLng) * Math.cos(toLat),
    Math.cos(fromLat) * Math.sin(toLat) - Math.sin(fromLat) * Math.cos(toLat) * Math.cos(dLng)
  )
  return Math.toDegrees(heading)
}

fun Double.toPositiveDegree(): Double {
  return if (this > 0) this else 360 + this
}

//fun CircleFromLatLng.toGeometryCircle(): Circle =
//  Circle(Vector2(originCoordinate().x, originCoordinate().y), radius())

//fun LatLng.toUtm(): UTM {
//  val converter = LatLong.CRS.getConverterTo(UTM.CRS)
//  val latLongSource = LatLong.valueOf(latitude, longitude, NonSI.DEGREE_ANGLE)
//  return converter.convert(latLongSource)
//}
//
//fun UTM.toLatLng(): LatLng {
//  val reverter = coordinateReferenceSystem.getConverterTo(LatLong.CRS)
//  val latlong = reverter.convert(this)
//  return LatLng(latlong.coordinates[0], latlong.coordinates[1])
//}

fun computeAreaByCoordinate(coordinates: List<Vector2>): Double {
  val operatedCoordinates = coordinates.toSet().withIndex().map {
    it.value.x.times(coordinates[it.index + 1].y)
      .minus(it.value.y.times(coordinates[it.index + 1].x))
  }
  return operatedCoordinates.sum().div(2).absoluteValue
}

fun LatLng.toTm3(): Pair<Double, Double> {
  try {
//    val epsgCode = getTm3Zone()?.epsgCode()
//    Log.i("epsg", "Got $epsgCode from $longitude")
//    val factory = CRSFactory()
//    val srcCrs = factory.createFromName("EPSG:4326")
//    val dstCrs = factory.createFromName(epsgCode)
//    val transform = BasicCoordinateTransform(srcCrs, dstCrs)
//    // Note these are x, y so lng, lat
//    val srcCoord = ProjCoordinate(longitude, latitude)
//    val dstCoord = ProjCoordinate()
//    transform.transform(srcCoord, dstCoord)
//    return Pair(dstCoord.x, dstCoord.y)
      return  Pair(0.0, 0.0)
  } catch (e: IllegalStateException) {
    Log.i("transformation", e.localizedMessage)
  }
  return Pair(0.0, 0.0)
}

fun List<LatLng>.getCenter(): LatLng {
  val lats = map { latLng -> latLng.latitude }
  val longs = map { latLng -> latLng.longitude }
  val minLat = lats.min()!!
  val minLong = longs.min()!!
  val maxLat = lats.max()!!
  val maxLong = longs.max()!!
  val center = LatLngBounds(LatLng(minLat, minLong), LatLng(maxLat, maxLong)).center
  return center
}