package smartgis.project.app.smartgis.forms.workspaceforms.gu

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.databinding.FormGuPersonalBinding
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.currentUser
import smartgis.project.app.smartgis.utils.noNull
import smartgis.project.app.smartgis.utils.prefix

abstract class BaseDataPersonalGu : LoginRequiredActivity() {

    private lateinit var binding: FormGuPersonalBinding

    private val gatheredData = mutableMapOf<String, Any>()

    companion object {
        const val NAMA = "nama"
        const val JABATAN = "jabatan"
        const val PREFIX_GU1 = "gu1"
        const val PREFIX_GU2 = "gu2"

        val keys = listOf(NAMA, JABATAN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FormGuPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSave.setOnClickListener {
            onSaveClick(getData())
        }

        binding.etName.addTextChangedListener(WatchChange {
            gatheredData[NAMA] = binding.etName.text.toString()
        })

        binding.etJabatan.addTextChangedListener(WatchChange {
            gatheredData[JABATAN] = binding.etJabatan.text.toString()
        })
    }

    private fun getData(): MutableMap<String, Any> {
        return if (getKeyPrefix().isNotEmpty()) {
            gatheredData.mapKeys { "${getKeyPrefix()}_${it.key}" }.toMutableMap()
        } else {
            gatheredData
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    abstract fun getKeyPrefix(): String

    abstract fun getFirebaseDocReference(
        workspaceId: String,
        email: String
    ): DocumentReference

    private fun doc(): DocumentReference? {
        val workspaceId = intent.getStringExtra(Workspace.INTENT)
        val email = currentUser()?.email

        if (workspaceId == null || email == null) {
            Log.e(localClassName, "workspaceId/email null")
            return null
        }

        return getFirebaseDocReference(workspaceId, email)
    }

    private fun onSaveClick(data: Map<String, Any>) {
        doc()?.set(data, SetOptions.merge())?.apply {
            addOnSuccessListener {
                Log.i(localClassName, "Data successfully merged!")
            }
            addOnFailureListener {
                Log.e(localClassName, "error merge ${it.localizedMessage}")
            }
        }

        Snackbar.make(binding.root, getString(R.string.success_save_data), Snackbar.LENGTH_SHORT).show()
    }

    private var loaded = false

    override fun onStart() {
        super.onStart()

        doc()?.addSnapshotListener(this) { doc, _ ->
            if (loaded) finish()
            Log.i(localClassName, "Got ${doc?.data}")
            loaded = true
        }

        doc()?.get(Source.CACHE)?.addOnSuccessListener {
            populateForm(it?.data)
        }
    }

    private fun String.addPrefix() = prefix(getKeyPrefix())

    private fun populateForm(data: Map<String, Any>?) {
        data?.apply {
            binding.etName.setText(get(NAMA.addPrefix()).toString().noNull())
            binding.etJabatan.setText(get(JABATAN.addPrefix()).toString().noNull())
        }
    }
}