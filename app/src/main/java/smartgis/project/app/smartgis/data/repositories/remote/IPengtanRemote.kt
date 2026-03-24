package smartgis.project.app.smartgis.data.repositories.remote

import kotlinx.coroutines.flow.Flow
import smartgis.project.app.smartgis.data.repositories.service.HandleResponse
import smartgis.project.app.smartgis.models.response.PengtanResponse
import smartgis.project.app.smartgis.state.ResponseState

interface IPengtanRemote : HandleResponse {
  suspend fun getdata(email: String, polygonId: String): Flow<ResponseState<PengtanResponse>>
}