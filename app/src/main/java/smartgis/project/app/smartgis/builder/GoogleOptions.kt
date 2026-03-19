package smartgis.project.app.smartgis.builder

import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleOptions {

  fun get(token: String): GoogleSignInOptions =
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(token)
      .requestEmail()
      .build()

}