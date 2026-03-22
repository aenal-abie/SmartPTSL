package smartgis.project.app.smartgis.utils

import com.google.android.gms.maps.model.LatLng

data class Coordinate(val x: Double, val y: Double)

data class CircleFromLatLng(val origin: LatLng, val p2: LatLng) {

    fun originCoordinate(): Coordinate {
        val sUtm = origin.toUtm()
        return Coordinate(sUtm.coordinates[0], sUtm.coordinates[1])
        return Coordinate(0.0, 0.00)
    }

    private fun destCoordinate(): Coordinate {
        val sUtm = p2.toUtm()
        return Coordinate(sUtm.coordinates[0], sUtm.coordinates[1])
        return Coordinate(0.0, 0.00)
    }

    fun radius(): Double =
        Math.hypot(
            destCoordinate().x - originCoordinate().x,
            destCoordinate().y - originCoordinate().y
        )

}