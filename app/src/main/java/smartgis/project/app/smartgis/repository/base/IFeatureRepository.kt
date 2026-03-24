package smartgis.project.app.smartgis.repository.base

import kotlinx.coroutines.flow.Flow
import smartgis.project.app.smartgis.data.repositories.entity.wms.WMSKantah
import smartgis.project.app.smartgis.data.repositories.service.Result
import smartgis.project.app.smartgis.models.response.PengtanResponse
import smartgis.project.app.smartgis.state.ResponseState

interface IFeatureRepository {
  suspend fun isProFeature(email: String, feature: String): Flow<ResponseState<String>>
  suspend fun getPolygon(coordinate: com.google.android.gms.maps.model.LatLng?): Result<WMSKantah>
  suspend fun getPengtan(email: String, areaID: String): Flow<ResponseState<PengtanResponse>>
}