package smartgis.project.app.smartgis.forms.signature

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.databinding.FormSignatureBinding
import smartgis.project.app.smartgis.documents.Storages
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.currentUser
import smartgis.project.app.smartgis.utils.gone
import smartgis.project.app.smartgis.utils.show
import smartgis.project.app.smartgis.utils.toBytes
import java.util.*

abstract class BaseSignatureFormContainer : LoginRequiredActivity() {

    companion object {
        const val PATH = "path"
        const val TTD_URL_PATTERN = "ttd_%s_url"
    }

    private lateinit var binding: FormSignatureBinding
    private var uploading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FormSignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnClearCanvas.setOnClickListener {
            binding.signatureView.clear()
        }

        binding.btnSaveSignature.setOnClickListener {
            if (binding.signatureView.isEmpty) {
                showSnackbar("Tanda tangan tidak boleh kosong!")
            } else {
                uploadSignature()
            }
        }
    }

    private fun uploadSignature() {
        if (!isInternetAvailable()) {
            showSnackbar(getString(R.string.no_internet_pause_current_work))
            return
        }

        uploading = true
        binding.progressContainer.show()

        val ref = Storages.getSignatureReference("${UUID.randomUUID()}.png")

        ref.putBytes(binding.signatureView.signatureBitmap.toBytes())
            .addOnProgressListener { snapshot ->
                val progress =
                    (100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount
                binding.uploadProgress.progress = progress.toInt()
            }
            .addOnSuccessListener { snapshot ->
                uploading = false

                snapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    getDoc().set(
                        mapOf(TTD_URL_PATTERN.format(getInfix()) to uri.toString()),
                        SetOptions.merge()
                    )
                }

                getDoc()
                    .set(mapOf(PATH to snapshot.metadata?.path), SetOptions.merge())
                    .addOnSuccessListener {
                        showSnackbar(getString(R.string.successful_upload))
                        binding.progressContainer.gone()
                    }
                    .addOnFailureListener { exception ->
                        showSnackbar(exception.localizedMessage ?: "Error")
                        binding.progressContainer.gone()
                    }
            }
            .addOnFailureListener { exception ->
                uploading = false
                showSnackbar(exception.localizedMessage ?: "Upload gagal")
                binding.progressContainer.gone()
            }
            .addOnCompleteListener {
                Log.i(localClassName, it.result.toString())
            }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun isInternetAvailable(): Boolean {
        val cm = ContextCompat.getSystemService(
            this,
            ConnectivityManager::class.java
        ) ?: return false

        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    abstract fun getDocReference(
        workspaceId: String?,
        email: String?
    ): DocumentReference

    open fun getInfix(): String = ""

    private fun getDoc(): DocumentReference =
        getDocReference(
            intent.getStringExtra(Workspace.INTENT),
            currentUser()?.email
        )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressedDispatcher.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}