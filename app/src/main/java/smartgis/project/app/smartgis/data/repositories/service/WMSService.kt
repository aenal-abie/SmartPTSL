package smartgis.project.app.smartgis.data.repositories.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import smartgis.project.app.smartgis.data.repositories.entity.wms.WMSKantah

interface WMSService {
  @GET("smartportal/wms")
  @Headers("Accept: application/json")
  suspend fun getPolygon(
    @Query("service") service: String,
    @Query("version") version: String,
    @Query("request") request: String,
    @Query("QUERY_LAYERS") QUERY_LAYERS: String,
    @Query("LAYERS") LAYERS: String,
    @Query("X") X: String,
    @Query("Y") Y: String,
    @Query("width") width: String,
    @Query("height") height: String,
    @Query("srs") srs: String,
    @Query("info_format") info_format: String,
    @Query("FEATURE_COUNT") FEATURE_COUNT: String,
    @Query("BBOX") BBOX: String,
  ): Response<WMSKantah>
}