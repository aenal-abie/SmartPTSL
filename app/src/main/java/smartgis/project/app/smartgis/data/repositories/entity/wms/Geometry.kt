package smartgis.project.app.smartgis.data.repositories.entity.wms


import com.google.gson.annotations.SerializedName

data class Geometry(
  @SerializedName("coordinates")
  val coordinates: List<List<List<List<Double>>>>?,
  @SerializedName("type")
  val type: String?
)