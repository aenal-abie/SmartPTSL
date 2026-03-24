package smartgis.project.app.smartgis.data.repositories

import kotlinx.coroutines.flow.Flow
import smartgis.project.app.smartgis.data.repositories.entity.wms.WMSKantah
import smartgis.project.app.smartgis.data.repositories.remote.IFeatureRemote
import smartgis.project.app.smartgis.data.repositories.remote.IPengtanRemote
import smartgis.project.app.smartgis.data.repositories.service.Result
import smartgis.project.app.smartgis.models.response.PengtanResponse
import smartgis.project.app.smartgis.repository.base.IFeatureRepository
import smartgis.project.app.smartgis.state.ResponseState
import javax.inject.Inject

class FeatureRepository @Inject constructor(
  private var featureRemote: IFeatureRemote,
  private var pengatanRemote: IPengtanRemote
) :
  IFeatureRepository {

  override suspend fun isProFeature(email: String, feature: String): Flow<ResponseState<String>> =
    featureRemote.isProFeature(email, feature)

  override suspend fun getPolygon(coordinate: com.google.android.gms.maps.model.LatLng?): Result<WMSKantah> =
    featureRemote.getPolygon(coordinate)

  override suspend fun getPengtan(
    email: String,
    areaID: String
  ): Flow<ResponseState<PengtanResponse>> =
    pengatanRemote.getdata(email, areaID)
}