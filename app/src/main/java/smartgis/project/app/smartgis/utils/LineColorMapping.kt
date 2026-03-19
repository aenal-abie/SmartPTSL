package smartgis.project.app.smartgis.utils

import android.content.Context
import smartgis.project.app.smartgis.R

class LineColorMapping(private val ctx: Context) {

//  val colorMaps = jenisColorMaps()

//  private fun jenisColorMaps(): Map<String, PolylineColorHolder> {
////    val colors = ctx.resources.obtainTypedArray(R.array.line_colors)
////    val lines = ctx.resources.getStringArray(R.array.line_items)
////    val mapped = lines
////      .withIndex()
////      .map {
////        it.value to PolylineColorHolder(
////          colors.getResourceId(it.index, R.color.yellow)
////        )
////      }.toMap()
////    colors.recycle()
//    return mapped
//  }

}

data class PolylineColorHolder(val color: Int)