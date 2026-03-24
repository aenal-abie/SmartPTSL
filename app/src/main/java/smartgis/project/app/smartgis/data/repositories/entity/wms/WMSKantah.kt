package smartgis.project.app.smartgis.data.repositories.entity.wms


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WMSKantah(
  @SerializedName("crs")
  val crs: Crs?,
  @SerializedName("features")
  val features: List<Feature>?,
  @SerializedName("numberReturned")
  val numberReturned: Int?,
  @SerializedName("timeStamp")
  val timeStamp: String?,
  @SerializedName("totalFeatures")
  val totalFeatures: String?,
  @SerializedName("type")
  val type: String?
)