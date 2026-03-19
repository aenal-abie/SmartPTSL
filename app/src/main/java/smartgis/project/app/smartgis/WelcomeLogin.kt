package smartgis.project.app.smartgis

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import smartgis.project.app.smartgis.databinding.ActivityWelcomeLoginBinding
import smartgis.project.app.smartgis.utils.appPreference
import smartgis.project.app.smartgis.utils.gone
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import androidx.core.content.edit
import com.google.firebase.auth.GoogleAuthProvider
import smartgis.project.app.smartgis.utils.googleSignInClient
import smartgis.project.app.smartgis.utils.showToast

class WelcomeLogin : CrashlyticsActivity() {

    companion object {
        private const val RC_SIGN_IN: Int = 0
    }

    private lateinit var auth: FirebaseAuth
    private var progressDialog: ProgressDialog? = null

    private lateinit var binding: ActivityWelcomeLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 4. Tangani Window Insets agar UI tidak tertutup Status Bar/Nav Bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        binding.btnSignIn.setOnClickListener { signIn() }
    }

    private fun startAnnouncementListenerService() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                return@OnCompleteListener
//            }
//            val token = task.result
//            Log.e("SMARTPTSL", token)
//            currentUser()?.email?.let {
//                Collections.setToken(it)
//                    .set(
//                        hashMapOf("token" to token, "created_at" to FieldValue.serverTimestamp()),
//                        SetOptions.merge()
//                    )
//            }
//
//        })
    }

    override fun onStart() {
        super.onStart()
        redirectToHomeIfAuth()
    }

    private fun redirectToHomeIfAuth() {
        auth.currentUser?.apply {
            binding.btnSignIn.gone()
            binding.tvWelcomeDesc.gone()
            binding.switchSurveyor.gone()
            binding.tvKeterangan.gone()
            moveToMain(2000)
        } ?: showPrivacyPolicyIfNotAgree()
    }

    override fun onResume() {
        super.onResume()
        redirectToHomeIfAuth()
    }

    private fun moveToMain(i: Long) {
        startAnnouncementListenerService()
        var url = ""
        intent.getStringExtra("url")?.apply {
            url = this
        }
        Thread {
            Thread.sleep(i)
            val isPemohon = appPreference().getBoolean("is_pemohon", false)
            if (isPemohon) {
//                startActivity<HomePemohon>("url" to url)
            } else {
                val intent = Intent(this@WelcomeLogin, Home::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
            }
            finish()
        }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    firebaseAuthWithGoogle(it)
                }
            } catch (e: ApiException) {
                Log.i(localClassName, e.localizedMessage)
//                layoutContainer.snackbar(e.localizedMessage)
//                toast(getString(R.string.something_wrong)).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
//        progressDialog = indeterminateProgressDialog(getString(R.string.trying_login))
//        progressDialog?.show()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                progressDialog?.hide()
//                if (task.isSuccessful) moveToMain(500)
//                else toast(getString(R.string.login_failed)).show()
//            }
        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE

                if (task.isSuccessful) moveToMain(500)
                else showToast(getString(R.string.login_failed))
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient().signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @SuppressLint("CommitPrefEdits")
    private fun setUserAsPemohon(isPemohon: Boolean) {
        appPreference().edit { putBoolean("is_pemohon", isPemohon) }
    }
}