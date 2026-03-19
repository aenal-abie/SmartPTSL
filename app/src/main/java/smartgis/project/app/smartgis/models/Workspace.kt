package smartgis.project.app.smartgis.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import smartgis.project.app.smartgis.utils.JavaUtil

@Parcelize
data class Workspace(var id: String, val name: String, var rt: String, var rw: String) :
  Parcelable {

  private fun getRw(): Int {
    if (rw == "null") return 0
    return JavaUtil.getOnlyNumber(rw)
  }

  private fun getRt(): Int {
    if (rt == "null") return 0
    return JavaUtil.getOnlyNumber(rt)
  }

  fun getFormattedRw(): String {
    return try {
      String.format("%02d", getRw())
    } catch (e: Exception) {
      rw
    }
  }

  fun getFormattedRt(): String {
    return try {
      String.format("%02d", getRt())
    } catch (e: Exception) {
      rt
    }
  }

  companion object {
    const val INTENT = "Workspace Data Intent"
    fun toObject(data: DocumentSnapshot): Workspace {
      return Workspace(
        data.id,
        data.data?.get("name").toString(),
        data.data?.get("rt").toString(),
        data.data?.get("rw").toString()
      )
    }
  }

  override fun toString(): String = name
}