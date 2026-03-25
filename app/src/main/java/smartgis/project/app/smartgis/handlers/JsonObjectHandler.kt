//package smartgis.project.app.smartgis.handlers
//
////import com.loopj.android.http.JsonHttpResponseHandler
////import cz.msebera.android.httpclient.Header
//import org.json.JSONObject
//
//class JsonObjectHandler(val succeed: (JSONObject?) -> Unit, val error: (Throwable?) -> Unit) :
//  JsonHttpResponseHandler() {
//
//  override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
//    succeed(response)
//  }
//
//  override fun onFailure(
//    statusCode: Int,
//    headers: Array<out Header>?,
//    responseString: String?,
//    throwable: Throwable?
//  ) {
//    error(throwable)
//  }
//
//}