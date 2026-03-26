package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormBuktiAlasHakBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.noNull

class YuridisAlasHak : GatherableFormFragment() {

    companion object {
        const val ALAS_HAK = "alas_hak"
        const val PEMBUAT = "pembuat"
        const val TANGGAL = "tanggal"
        const val NO_PERSIL = "nk_persil"
        const val DESA_KELURAHAN = "desa_kelurahan"
        const val ALAMAT = "alamat"
        const val NOMOR = "nomor"
        const val KELAS = "kelas"
        const val LUAS = "luas"

        const val PREFIX = GeneratePdfContent.ALAS_HAK

        val keys = listOf(
            ALAS_HAK, PEMBUAT, TANGGAL, NO_PERSIL,
            DESA_KELURAHAN, ALAMAT, NOMOR, KELAS, LUAS
        )
    }

    private var _binding: FormBuktiAlasHakBinding? = null
    private val binding get() = _binding!!

    private val alasHakList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormBuktiAlasHakBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys
        setupSpinner()
        setupInputWatcher()
        setupDefaultButton()
    }

    // ================= SETUP =================

    private fun setupSpinner() {
        alasHakList.clear()
        alasHakList.addAll(resources.getStringArray(R.array.jenis_alas_hak))

        binding.spJenisAlasHak.setItems(alasHakList)
//
//        binding.spJenisAlasHak.setOnItemSelectedListener { _, position, _, _ ->
//            gatheredData[ALAS_HAK] = alasHakList.getOrElse(position) { "" }
//        }
    }

    private fun setupInputWatcher() {
        bindText(binding.etPembuat, PEMBUAT)
        bindText(binding.etTanggalBuat, TANGGAL)
        bindText(binding.etNoPersil, NO_PERSIL)
        bindText(binding.etDesaKelurahan, DESA_KELURAHAN)
        bindText(binding.etAlamat, ALAMAT)
        bindText(binding.etNomor, NOMOR)
        bindText(binding.etKelas, KELAS)
        bindText(binding.etLuas, LUAS)
    }

    private fun bindText(view: android.widget.EditText, key: String) {
        view.addTextChangedListener(WatchChange {
            gatheredData[key] = view.text.toString()
        })
    }

    private fun setupDefaultButton() {
        binding.btnCompeleteEmpetyValue.setOnClickListener {
            setDefaultIfEmpty(binding.etPembuat)
            setDefaultIfEmpty(binding.etTanggalBuat)
            setDefaultIfEmpty(binding.etNoPersil)
            setDefaultIfEmpty(binding.etDesaKelurahan)
            setDefaultIfEmpty(binding.etAlamat)
            setDefaultIfEmpty(binding.etNomor)
            setDefaultIfEmpty(binding.etKelas)
            setDefaultIfEmpty(binding.etLuas)

//            if (binding.spJenisAlasHak.selectedIndex < 0) {
//                binding.spJenisAlasHak.selectedIndex = 0
//                gatheredData[ALAS_HAK] = alasHakList.firstOrNull() ?: ""
//            }
        }
    }

    private fun setDefaultIfEmpty(view: android.widget.EditText) {
        if (view.text.isNullOrEmpty()) {
            view.setText("-")
        }
    }

    // ================= DATA =================

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data ?: return

        setSpinnerValue(data)
        setText(binding.etPembuat, data, PEMBUAT)
        setText(binding.etTanggalBuat, data, TANGGAL)
        setText(binding.etNoPersil, data, NO_PERSIL)
        setText(binding.etDesaKelurahan, data, DESA_KELURAHAN)
        setText(binding.etAlamat, data, ALAMAT)
        setText(binding.etNomor, data, NOMOR)
        setText(binding.etKelas, data, KELAS)
        setText(binding.etLuas, data, LUAS)
    }

    private fun setSpinnerValue(data: Map<String, Any>) {
        val value = data[ALAS_HAK.addPrefix()].toString()
        val index = alasHakList.indexOf(value)

        if (index >= 0) {
//            binding.spJenisAlasHak.selectedIndex = index
            gatheredData[ALAS_HAK] = value
        }
    }

    private fun setText(
        view: android.widget.EditText,
        data: Map<String, Any>,
        key: String
    ) {
        view.setText(data[key.addPrefix()].toString().noNull())
    }
}