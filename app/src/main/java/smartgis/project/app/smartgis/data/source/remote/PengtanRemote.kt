package smartgis.project.app.smartgis.data.source.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import smartgis.project.app.smartgis.data.repositories.remote.IPengtanRemote
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.models.response.PengtanResponse
import smartgis.project.app.smartgis.state.ResponseState
import javax.inject.Inject

class PengtanRemote @Inject constructor() : IPengtanRemote {

  override suspend fun getdata(
    email: String,
    polygonId: String
  ): Flow<ResponseState<PengtanResponse>> =
    flow {
      emit(ResponseState.loading())
      val snapshot = Collections.getUserAreaPengtan(email, polygonId).document("pengtan")
        .get().await()
      val data = snapshot.toObject(PengtanResponse::class.java)
      data?.let {
        emit(ResponseState.success(it))
      }

    }
}
