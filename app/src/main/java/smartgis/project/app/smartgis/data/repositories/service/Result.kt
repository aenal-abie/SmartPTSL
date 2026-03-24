package smartgis.project.app.smartgis.data.repositories.service

sealed class Result<out T> {
  data class Success<out T>(val responseData: T, val successCode: Int? = 200) : Result<T>()
  data class Failure(val errorData: Exception, val errorCode: Int? = -1) : Result<Nothing>()
}