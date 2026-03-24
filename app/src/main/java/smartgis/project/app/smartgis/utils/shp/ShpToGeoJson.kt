package smartgis.project.app.smartgis.utils.shp

import com.linuxense.javadbf.DBFReader
import com.linuxense.javadbf.DBFRow
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader
import org.nocrala.tools.gis.data.esri.shapefile.shape.ShapeType
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape
import java.io.File


object ShpToGeoJson {

  fun transformShpDbfToGeoJson(shp: File, dbf: File): Map<String, Any> {
    val shapeFile = ShapeFileReader(shp.inputStream())
    val dbfInputStream = DBFReader(dbf.inputStream())

    val keys = (0 until dbfInputStream.fieldCount).map { dbfInputStream.getField(it) }
    val features = mutableListOf<Any>()

    var esriRecord = shapeFile.next()
    var row: DBFRow? = dbfInputStream.nextRow()

    while (esriRecord != null && row != null) {
      val shapeTypeStr = esriRecord.shapeType
      if (shapeTypeStr == ShapeType.POLYGON) {
        val polyRec = esriRecord as PolygonShape

        for (i in 0 until polyRec.numberOfParts) {
          val properties = mutableMapOf<String, Any>()

          keys.forEach {
            row?.getObject(it.name)?.apply { properties[it.name] = this }
          }

          val poly = polyRec.getPointsOfPart(i)
          val polygonCoordinates = poly.map { listOf(it.x, it.y) }
          features.add(
            mapOf(
              "geometry" to mapOf(
                "coordinates" to listOf(polygonCoordinates),
                "type" to "Polygon"
              ),
              "properties" to properties,
              "type" to "Feature"
            )
          )
        }
      }
      esriRecord = shapeFile.next()
      row = dbfInputStream.nextRow()
    }
    return mapOf("generate" to "ok", "type" to "FeatureCollection", "features" to features)
  }
}