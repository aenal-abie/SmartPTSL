package smartgis.project.app.smartgis

import android.annotation.SuppressLint
import android.os.Bundle
//import dagger.hilt.android.AndroidEntryPoint
import smartgis.project.app.smartgis.utils.currentUser

//@SuppressLint("Registered")
//@AndroidEntryPoint
open class LoginRequiredActivity : CrashlyticsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showPrivacyPolicyIfNotAgree()
    }

    override fun onResume() {
        super.onResume()
        showPrivacyPolicyIfNotAgree()
    }

    override fun onStart() {
        super.onStart()
        currentUser() ?: finish()
    }
}