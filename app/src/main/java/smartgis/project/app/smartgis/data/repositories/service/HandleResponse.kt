package smartgis.project.app.smartgis.data.repositories.service

import retrofit2.Response

private const val TAG = "Handle Response"

interface HandleResponse {
  suspend fun <T> fetchResponse(call: suspend () -> Response<T>): Result<T> {
    return try {
      val apiResponse = call.invoke()
      if (apiResponse.isSuccessful) {
        Result.Success(apiResponse.body() as T, successCode = apiResponse.code())
      } else {
        Result.Failure(
          Exception(apiResponse.errorBody()?.string()),
          errorCode = apiResponse.code()
        )
      }
    } catch (ex: Exception) {
      ex.printStackTrace()
      Result.Failure(ex, 1000)
    }
  }
}