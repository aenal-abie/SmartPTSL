package smartgis.project.app.smartgis.data.repositories.entity.wms


import com.google.gson.annotations.SerializedName

data class PropertiesX(
  @SerializedName("kantah_id")
  val kantahId: String?,
  @SerializedName("nama")
  val nama: String?,
  @SerializedName("nib")
  val nib: String?,
  @SerializedName("no_hak")
  val noHak: String?,
  @SerializedName("wms_id")
  val wmsId: Int?,
  @SerializedName("type_hak")
  val tipeHak: String?,
  @SerializedName("keterangan")
  val keterangan: String?
)