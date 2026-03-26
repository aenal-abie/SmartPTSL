package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormAktaIkrarWakapBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.noNull

class YuridisWakaf : GatherableFormFragment() {

    companion object {
        const val JENIS = "jenis"
        const val PEMBUAT = "pembuat"
        const val TANGGAL = "tanggal"
        const val NOMOR = "nomor"
        const val MATA_UANG = "mata_uang"
        const val MATA_UANG_SINGKAT = "mata_uang_singkat"
        const val KURS = "kurs"
        const val NILAI = "nilai"
        const val PREFIX = "akta_wakaf"

        val keys = listOf(
            JENIS, PEMBUAT, TANGGAL, NOMOR,
            MATA_UANG, MATA_UANG_SINGKAT, KURS, NILAI
        )
    }

    private var _binding: FormAktaIkrarWakapBinding? = null
    private val binding get() = _binding!!

    private val jenisList = mutableListOf<String>()
    private val mataUangList = mutableListOf<String>()
    private val mataUangSingkatList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormAktaIkrarWakapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys

        setupDropdown()
        setupWatcher()
    }

    // ================= SETUP =================

    private fun setupDropdown() {
        jenisList.apply {
            clear()
            addAll(resources.getStringArray(R.array.jenis_alas_hak))
        }

        mataUangList.apply {
            clear()
            addAll(resources.getStringArray(R.array.mata_uang))
        }

        mataUangSingkatList.apply {
            clear()
            addAll(resources.getStringArray(R.array.mata_uang_singkat))
        }

        binding.msJenisAlasHak.setItems(jenisList)
        binding.msMataUang.setItems(mataUangList)

        binding.msJenisAlasHak.setOnItemSelectedListener { _, position, _, _ ->
            gatheredData[JENIS] = jenisList.getOrElse(position) { "" }
        }

        binding.msMataUang.setOnItemSelectedListener { _, position, _, _ ->
            gatheredData[MATA_UANG] = mataUangList.getOrElse(position) { "" }
            gatheredData[MATA_UANG_SINGKAT] = mataUangSingkatList.getOrElse(position) { "" }
        }
    }

    private fun setupWatcher() {
        bindText(binding.etPembuatAkta, PEMBUAT)
        bindText(binding.etTanggalAkta, TANGGAL)
        bindText(binding.etNomorAkta, NOMOR)
        bindText(binding.etNilaiKurs, KURS)
        bindText(binding.etNilai, NILAI)
    }

    private fun bindText(view: android.widget.EditText, key: String) {
        view.addTextChangedListener(WatchChange {
            gatheredData[key] = view.text.toString()
        })
    }

    // ================= DATA =================

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data ?: return

        setSpinner(binding.msJenisAlasHak, jenisList, data, JENIS)
        setSpinner(binding.msMataUang, mataUangList, data, MATA_UANG)

        setText(binding.etPembuatAkta, data, PEMBUAT)
        setText(binding.etTanggalAkta, data, TANGGAL)
        setText(binding.etNomorAkta, data, NOMOR)
        setText(binding.etNilaiKurs, data, KURS)
        setText(binding.etNilai, data, NILAI)
    }

    private fun setSpinner(
        spinner: com.jaredrummler.materialspinner.MaterialSpinner,
        list: List<String>,
        data: Map<String, Any>,
        key: String
    ) {
        val value = data[key.addPrefix()].toString()
        val index = list.indexOf(value)

        if (index >= 0) {
            spinner.selectedIndex = index
            gatheredData[key] = value
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