package smartgis.project.app.smartgis.data.repositories.entity.wms


import com.google.gson.annotations.SerializedName

data class Crs(
  @SerializedName("properties")
  val properties: Properties?,
  @SerializedName("type")
  val type: String?
)