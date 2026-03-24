package smartgis.project.app.smartgis.data.repositories.entity.wms


import com.google.gson.annotations.SerializedName

data class Properties(
  @SerializedName("name")
  val name: String?
)