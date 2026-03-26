package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormYuridisNikBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.*
import java.util.*

class YuridisNIK : GatherableFormFragment() {

    companion object {
        const val NIK = "nik"
        const val NAMA = "nama"
        const val ALAMAT = "alamat"
        const val LOKASI = "lokasi"
        const val KODE_POS = "kode_pos"
        const val TEMPAT_LAHIR = "tempat_lahir"
        const val TGL_LAHIR = "tgl_lahir"
        const val JENIS_KELAMIN = "jns_kelm"
        const val STATUS = "status"
        const val RT_RW = "rt_rw"
        const val GOLONGAN_DARAH = "goldar"
        const val AGAMA = "agama"
        const val PEKERJAAN = "pekerjaan"
        const val NO_KK = "no_kk"

        const val PREFIX = GeneratePdfContent.PERORANGAN

        val keys = listOf(
            NIK, NAMA, ALAMAT, LOKASI, KODE_POS,
            TEMPAT_LAHIR, TGL_LAHIR, JENIS_KELAMIN,
            STATUS, RT_RW, GOLONGAN_DARAH,
            AGAMA, PEKERJAAN, NO_KK
        )
    }

    private var _binding: FormYuridisNikBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormYuridisNikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys

        setupWatcher()
        setupDatePicker()
        setupGender()
        setupDefaultButton()
    }

    // ================= SETUP =================

    private fun setupWatcher() {
        bindText(binding.etNIK, NIK)
        bindText(binding.etNama, NAMA)
        bindText(binding.etAlamat, ALAMAT)
        bindText(binding.etLokasi, LOKASI)
        bindText(binding.etKodePos, KODE_POS)
        bindText(binding.etTempatLahir, TEMPAT_LAHIR)
//        bindText(binding.etTglLahir, TGL_LAHIR)
        bindText(binding.etStatus, STATUS)
//        bindText(binding.etRtRw, RT_RW)
//        bindText(binding.etGolDarah, GOLONGAN_DARAH)
//        bindText(binding.etAgama, AGAMA)
        bindText(binding.etPekerjaan, PEKERJAAN)
        bindText(binding.etNomorKK, NO_KK)
    }

    private fun bindText(view: android.widget.EditText, key: String) {
        view.addTextChangedListener(WatchChange {
            gatheredData[key] = view.text.toString()
        })
    }

    private fun setupDatePicker() {
//        binding.etTglLahir.setOnClickListener {
//            DatePickerDialog(
//                requireContext(),
//                { _, year, month, day ->
//                    calendar.set(year, month, day)
//                    binding.etTglLahir.setText(calendar.time.toSimpleDate())
//                    gatheredData[TGL_LAHIR] = binding.etTglLahir.text.toString()
//                },
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }
    }

    private fun setupGender() {
//        binding.radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
//            val value = when (checkedId) {
////                binding.radioLakiLaki.id -> "L"
//                binding.radioPerempuan.id -> "P"
//                else -> ""
//            }
//            gatheredData[JENIS_KELAMIN] = value
//        }
    }

    private fun setupDefaultButton() {
        binding.btnCompeleteEmpetyValue.setOnClickListener {

            setDefault(binding.etNIK, "-")
            setDefault(binding.etNama, "-")
            setDefault(binding.etAlamat, "-")
            setDefault(binding.etLokasi, "-")
            setDefault(binding.etKodePos, "0")
            setDefault(binding.etTempatLahir, "-")
//            setDefault(binding.etTglLahir, "-")
//            setDefault(binding.etRtRw, "0")
//            setDefault(binding.etAgama, "-")
            setDefault(binding.etStatus, "-")
//            setDefault(binding.etGolDarah, "-")
            setDefault(binding.etPekerjaan, "-")
            setDefault(binding.etNomorKK, "-")

//            if (binding.radioGroupGender.checkedRadioButtonId == -1) {
//                binding.radioLakiLaki.isChecked = true
//                gatheredData[JENIS_KELAMIN] = "L"
//            }
        }
    }

    private fun setDefault(view: android.widget.EditText, value: String) {
        if (view.text.isNullOrEmpty()) {
            view.setText(value)
        }
    }

    // ================= DATA =================

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data ?: return

        setText(binding.etNIK, data, NIK)
        setText(binding.etNama, data, NAMA)
        setText(binding.etAlamat, data, ALAMAT)
        setText(binding.etLokasi, data, LOKASI)
        setText(binding.etKodePos, data, KODE_POS)
        setText(binding.etTempatLahir, data, TEMPAT_LAHIR)
//        setText(binding.etTglLahir, data, TGL_LAHIR)
//        setText(binding.etRtRw, data, RT_RW)
//        setText(binding.etAgama, data, AGAMA)
        setText(binding.etStatus, data, STATUS)
//        setText(binding.etGolDarah, data, GOLONGAN_DARAH)
        setText(binding.etPekerjaan, data, PEKERJAAN)
        setText(binding.etNomorKK, data, NO_KK)

        setGender(data)
    }

    private fun setText(
        view: android.widget.EditText,
        data: Map<String, Any>,
        key: String
    ) {
        view.setText(data[key.addPrefix()].toString().noNull())
    }

    private fun setGender(data: Map<String, Any>) {
//        when (data[JENIS_KELAMIN.addPrefix()].toString().noNull()) {
//            "L" -> binding.radioLakiLaki.isChecked = true
//            "P" -> binding.radioPerempuan.isChecked = true
//        }
    }
}