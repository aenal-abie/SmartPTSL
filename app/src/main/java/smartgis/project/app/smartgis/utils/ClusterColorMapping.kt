package smartgis.project.app.smartgis.utils

import android.content.Context
import smartgis.project.app.smartgis.R

class ClusterColorMapping(private val ctx: Context) {

//  val colorMaps = clusterColorMaps()

//  private fun clusterColorMaps(): Map<String, PolygonColorHolder> {
////    val strokeColors = ctx.resources.obtainTypedArray(R.array.cluster_stroke_colors)
////    val fillColors = ctx.resources.obtainTypedArray(R.array.cluster_fill_colors)
////    val clusters = ctx.resources.getStringArray(R.array.cluster_items)
////    val mapped = clusters
////      .withIndex()
////      .map {
////        it.value to PolygonColorHolder(
////          strokeColors.getResourceId(it.index, R.color.yellow),
////          fillColors.getResourceId(it.index, R.color.yellow_transparent)
////        )
////      }.toMap()
////    strokeColors.recycle()
////    fillColors.recycle()
////    return mapped
//  }

}

data class PolygonColorHolder(val strokeColor: Int, val fillColor: Int)