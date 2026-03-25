package smartgis.project.app.smartgis.handlers

import android.content.Context
//import com.loopj.android.http.FileAsyncHttpResponseHandler
//import cz.msebera.android.httpclient.Header
//import org.jetbrains.anko.toast
import java.io.File

//class DownloadFile(val context: Context, val onSuccess: (File) -> Unit) :
//  FileAsyncHttpResponseHandler(context) {
//  override fun onSuccess(statusCode: Int, headers: Array<out Header>?, file: File?) {
//    file?.apply {
//      try {
//        onSuccess(this)
//      } catch (e: Exception) {
//        context.toast("Ada kesalahan. Pastikan memory anda cukup untuk menyimpan berkas").show()
//      }
//    }
//  }
//
//  override fun onFailure(
//    statusCode: Int,
//    headers: Array<out Header>?,
//    throwable: Throwable?,
//    file: File?
//  ) {
//    context.toast("Ada kesalahan: ${throwable?.localizedMessage}").show()
//  }
//}