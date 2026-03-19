package smartgis.project.app.smartgis

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.appcompat.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.widget.Button
import smartgis.project.app.smartgis.utils.USER_AGREE_TO_PRIVACY_POLICY
import smartgis.project.app.smartgis.utils.appPreference

//import org.jetbrains.anko.*
//import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
//import org.jetbrains.anko.sdk27.coroutines.onClick
//import smartgis.project.app.smartgis.utils.USER_AGREE_TO_PRIVACY_POLICY
//import smartgis.project.app.smartgis.utils.appPreference
//import smartgis.project.app.smartgis.utils.disable

abstract class CrashlyticsActivity : AppCompatActivity() {

  private lateinit var privacyPolicyManager: PrivacyPolicyManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    privacyPolicyManager = PrivacyPolicyManager(this)
  }

  fun showPrivacyPolicyIfNotAgree() {
    val isUserAgree = appPreference().getBoolean(USER_AGREE_TO_PRIVACY_POLICY, false)
    if (!isUserAgree) privacyPolicyManager.showDialog()
  }

}

class PrivacyPolicyManager(private val context: Context) {

  private var dialogInterface: DialogInterface? = null

  private fun btnAgreeClicked() {
    context.appPreference().edit().putBoolean(USER_AGREE_TO_PRIVACY_POLICY, true).apply()
    dialogInterface?.dismiss()
  }

  fun showDialog() {
    dialogInterface?.dismiss()
//    dialogInterface = context.alert {
//      isCancelable = false
//      title = "Persetujuan Privacy Policy"
//      customView {
//        verticalLayout {
//          padding = dip(20)
//          textView(
//            HtmlCompat.fromHtml(
//              ctx.getString(R.string.privacy_policy_title),
//              HtmlCompat.FROM_HTML_MODE_LEGACY
//            )
//          ) {
//            movementMethod = LinkMovementMethod.getInstance()
//            textColor = Color.BLACK
//          }.lparams {
//            bottomMargin = dip(20)
//          }
//          checkBox(R.string.agree_to_privacy_policy) {
//            onCheckedChange { _, isChecked ->
//              val btn = this@verticalLayout.findViewById<Button>(0)
//              btn.isEnabled = isChecked
//            }
//          }.lparams {
//            bottomMargin = dip(20)
//          }
//          button("Gunakan aplikasi ini") {
//            id = 0
//            disable()
//            onClick {
//              this@PrivacyPolicyManager.btnAgreeClicked()
//            }
//          }
//        }
//      }
//    }.show()
  }

}