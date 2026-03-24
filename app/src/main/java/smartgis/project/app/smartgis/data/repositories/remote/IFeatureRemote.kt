package smartgis.project.app.smartgis.data.repositories.remote

import kotlinx.coroutines.flow.Flow
import smartgis.project.app.smartgis.data.repositories.entity.wms.WMSKantah
import smartgis.project.app.smartgis.data.repositories.service.HandleResponse
import smartgis.project.app.smartgis.data.repositories.service.Result
import smartgis.project.app.smartgis.state.ResponseState

interface IFeatureRemote : HandleResponse {
  suspend fun isProFeature(email: String, feature: String): Flow<ResponseState<String>>
  suspend fun getPolygon(coordinate: com.google.android.gms.maps.model.LatLng?): Result<WMSKantah>
}