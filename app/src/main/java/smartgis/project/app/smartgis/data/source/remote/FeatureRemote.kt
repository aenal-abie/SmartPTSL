package smartgis.project.app.smartgis.data.source.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import smartgis.project.app.smartgis.data.repositories.entity.wms.WMSKantah
import smartgis.project.app.smartgis.data.repositories.remote.IFeatureRemote
import smartgis.project.app.smartgis.data.repositories.service.Result
import smartgis.project.app.smartgis.data.repositories.service.WMSService
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.models.response.FeaturePro
import smartgis.project.app.smartgis.state.ResponseState
import javax.inject.Inject

class FeatureRemote @Inject constructor(private val wmsService: WMSService) : IFeatureRemote {

  override suspend fun isProFeature(email: String, feature: String): Flow<ResponseState<String>> =
    flow {
      emit(ResponseState.loading())
      val snapshot = Collections.getUserPurchasedItem(email).document(feature)
        .get().await()
      val data = snapshot.toObject(FeaturePro::class.java)
      data?.let {
        emit(ResponseState.success(it.expire_at))
      } ?: emit(ResponseState.success(""))

    }

  override suspend fun getPolygon(coordinate: com.google.android.gms.maps.model.LatLng?): Result<WMSKantah> =
    when (val result = fetchResponse {
      wmsService.getPolygon(
        service = "WMS",
        version = "1.1.0",
        request = "GetFeatureInfo",
        QUERY_LAYERS = "smartportal:portal",
        LAYERS = "smartportal:portal",
        X = "0",
        Y = "0",
        width = "101",
        height = "101",
        srs = "EPSG:4326",
        info_format = "application/json",
        FEATURE_COUNT = "10",
        BBOX = "${coordinate?.longitude},${coordinate?.latitude}," +
            "${coordinate?.longitude?.plus(0.00006)}" +
            ",${coordinate?.latitude?.plus(0.00006)}"
      )
    }) {
      is Result.Success -> Result.Success(result.responseData)
      is Result.Failure -> result
    }
}
