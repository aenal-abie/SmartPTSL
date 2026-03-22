package smartgis.project.app.smartgis.forms.delinasi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import kotlinx.android.synthetic.main.form_subject_identity.*
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.ItemSelected
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormDelinasiBinding
import smartgis.project.app.smartgis.databinding.FormSubjectIdentityBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.*

class SubjectIdentity : GatherableFormFragment() {

    companion object {
        const val NAMA = "nama"
        const val NIK = "nik"
        const val TEMPAT_LAHIR = "tempat_lahir"
        const val TANGGAL_LAHIR = "tangggal_lahir"
        const val ALAMAT = "alamat"
        const val PEKERJAAN = "pekerjaan"
        const val PREFIX = "subject_identity"
        val keys = listOf(NAMA, NIK, TEMPAT_LAHIR, TANGGAL_LAHIR, ALAMAT, PEKERJAAN)
    }

    private val initialCalendar = "1980/01/01".toDate().toCalendar()
    private val jobsItems = mutableListOf<String>()
    private lateinit var binding: FormSubjectIdentityBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FormSubjectIdentityBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        required = keys
        jobsItems.addAll(resources.getStringArray(R.array.job_items))

        binding.etBornDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                binding.etBornDate.setText(
                    initialCalendar.year(year).month(month).day(dayOfMonth).time.toSimpleDate()
                )
            }, initialCalendar.year(), initialCalendar.month(), initialCalendar.day()).show()
        }

        binding.etName.addTextChangedListener(WatchChange {
            gatheredData[NAMA] = binding.etName.text.toString()
        })
        binding.etNik.addTextChangedListener(WatchChange {
            gatheredData[NIK] = binding.etNik.text.toString()
        })
        binding.etBornAddress.addTextChangedListener(WatchChange {
            gatheredData[TEMPAT_LAHIR] = binding.etBornAddress.text.toString()
        })
        binding.etBornDate.addTextChangedListener(WatchChange {
            gatheredData[TANGGAL_LAHIR] = binding.etBornDate.text.toString()
        })
        binding.etAddress.addTextChangedListener(WatchChange {
            gatheredData[ALAMAT] = binding.etAddress.text.toString()
        })
        binding.sJobs.onItemSelectedListener = ItemSelected {
            jobsItems.get(it).apply { gatheredData[PEKERJAAN] = this }
        }
    }

    override fun getKeyPrefix(): String {
        return PREFIX
    }

    override fun populateForm(data: Map<String, Any>?) {
        data?.apply {
            binding.etName?.setText(get(NAMA.addPrefix()).toString().noNull())
            binding.etNik?.setText(get(NIK.addPrefix()).toString().noNull())
            binding.etBornAddress?.setText(get(TEMPAT_LAHIR.addPrefix()).toString().noNull())
            binding.etBornDate?.setText(get(TANGGAL_LAHIR.addPrefix()).toString().noNull())
            binding.etAddress?.setText(get(ALAMAT.addPrefix()).toString().noNull())
            jobsItems.withIndex()
                .find { indexedValue -> get(PEKERJAAN.addPrefix()).toString() == indexedValue.value }
                ?.apply { binding.sJobs?.setSelection(index) }
        }
    }

}