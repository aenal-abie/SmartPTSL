package smartgis.project.app.smartgis.utils.youtube

import android.content.Context
//import com.loopj.android.http.JsonHttpResponseHandler
//import cz.msebera.android.httpclient.Header
import org.json.JSONObject
//import smartgis.project.app.smartgis.http.Http

private const val TUTORIAL_SMART_PTSL_PLAYLIST_ID: String = "PLH9vOk93icWx9D9k5wt6o8G_8PmvBqXmL"
private const val PUBLIC_KEY_NO_RESTRICTION = "AIzaSyCVRc5luhURzjpFsCBCl6JTZwmmkezKons"

fun Context.getYoutubePlaylistVideos(succeed: (JSONObject?) -> Unit) {
//  val requestUrl =
//    "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=50&playlistId=$TUTORIAL_SMART_PTSL_PLAYLIST_ID&key=$PUBLIC_KEY_NO_RESTRICTION"
//  Http.client.get(this, requestUrl,
//    object : JsonHttpResponseHandler() {
//
//      override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
//        succeed(response)
//      }
//
//      override fun onFailure(
//        statusCode: Int,
//        headers: Array<out Header>?,
//        throwable: Throwable?,
//        errorResponse: JSONObject?
//      ) {
//
//      }
//    })
}
