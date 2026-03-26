package smartgis.project.app.smartgis.forms.workspaceforms.saksi

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.ItemSelected
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.SaksiFormContainerBinding
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.*

abstract class SaksiFormContainer : LoginRequiredActivity() {

    private lateinit var binding: SaksiFormContainerBinding

    private val religions = mutableListOf<String>()
    private var initialCalendar = "1980/01/01".toDate().toCalendar()
    private val gatheredData = mutableMapOf<String, Any>()

    companion object {
        const val NAMA = "nama"
        const val NIK = "nik"
        const val AGAMA = "agama"
        const val USIA = "usia"
        const val PEKERJAAN = "pekerjaan"
        const val ALAMAT = "alamat"

        const val SAKSI_PERTAMA = "saksi_pertama"
        const val SAKSI_KEDUA = "saksi_kedua"

        val keys = listOf(NAMA, NIK, AGAMA, USIA, PEKERJAAN, ALAMAT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SaksiFormContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSave.setOnClickListener {
            onSaveClick(getData())
        }

        religions.addAll(resources.getStringArray(R.array.religion_items))

        binding.formSaksi.etUsia.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                binding.formSaksi.etUsia.setText(
                    initialCalendar.year(year).month(month).day(dayOfMonth).time.toSimpleDate()
                )
            }, initialCalendar.year(), initialCalendar.month(), initialCalendar.day()).show()
        }

        binding.formSaksi.etName.addTextChangedListener(WatchChange {
            gatheredData[NAMA] = binding.formSaksi.etName.text.toString()
        })

        binding.formSaksi.etNik.addTextChangedListener(WatchChange {
            gatheredData[NIK] = binding.formSaksi.etNik.text.toString()
        })

        binding.formSaksi.sReligions.onItemSelectedListener =
            ItemSelected { gatheredData[AGAMA] = religions[it] }

        binding.formSaksi.etUsia.addTextChangedListener(WatchChange {
            gatheredData[USIA] = binding.formSaksi.etUsia.text.toString()
        })

        binding.formSaksi.etJob.addTextChangedListener(WatchChange {
            gatheredData[PEKERJAAN] = binding.formSaksi.etJob.text.toString()
        })

        binding.formSaksi.etAddress.addTextChangedListener(WatchChange {
            gatheredData[ALAMAT] = binding.formSaksi.etAddress.text.toString()
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

    abstract fun getFireBaseDocReference(
        workspaceId: String,
        email: String
    ): DocumentReference

    fun onSaveClick(data: Map<String, Any>) {
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

    private fun doc(): DocumentReference? {
        val workspaceId = intent.getStringExtra(Workspace.INTENT)
        val email = currentUser()?.email

        if (workspaceId == null || email == null) {
            Log.e(localClassName, "workspaceId/email null")
            return null
        }

        return getFireBaseDocReference(workspaceId, email)
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
            binding.formSaksi.etName.setText(get(NAMA.addPrefix()).toString().noNull())
            binding.formSaksi.etNik.setText(get(NIK.addPrefix()).toString().noNull())
            binding.formSaksi.etUsia.setText(get(USIA.addPrefix()).toString().noNull())

            get(USIA.addPrefix()).toString().noNull()?.let {
                if (it.isNotEmpty()) {
                    initialCalendar = it.toDate().toCalendar()
                }
            }

            binding.formSaksi.etJob.setText(get(PEKERJAAN.addPrefix()).toString().noNull())
            binding.formSaksi.etAddress.setText(get(ALAMAT.addPrefix()).toString().noNull())

            religions.withIndex()
                .find { it.value == get(AGAMA.addPrefix()).toString() }
                ?.let { binding.formSaksi.sReligions.setSelection(it.index) }
        }
    }
}