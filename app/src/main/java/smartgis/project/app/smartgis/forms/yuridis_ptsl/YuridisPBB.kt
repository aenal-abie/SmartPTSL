package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormPbbBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.noNull

class YuridisPBB : GatherableFormFragment() {

    companion object {
        const val JENIS = "jenis"
        const val SPPT = "sppt"
        const val SPPT_TAHUN = "sppt_tahun"
        const val LUAS = "luas"
        const val NJOP = "njop"
        const val PREFIX = "pbb"

        val keys = listOf(JENIS, SPPT, SPPT_TAHUN, LUAS, NJOP)
    }

    private var _binding: FormPbbBinding? = null
    private val binding get() = _binding!!

    private val jenisList = mutableListOf<String>()
    private val tahunList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormPbbBinding.inflate(inflater, container, false)
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

        tahunList.apply {
            clear()
            addAll(generateYears(2010, 10))
        }

        binding.spJenisAlasHak.setItems(jenisList)
        binding.msSpptTahun.setItems(tahunList)

        binding.spJenisAlasHak.setOnItemSelectedListener { _, position, _, _ ->
            gatheredData[JENIS] = jenisList.getOrElse(position) { "" }
        }

        binding.msSpptTahun.setOnItemSelectedListener { _, position, _, _ ->
            gatheredData[SPPT_TAHUN] = tahunList.getOrElse(position) { "" }
        }
    }

    private fun setupWatcher() {
        bindText(binding.etSppt, SPPT)
        bindText(binding.etLuas, LUAS)
        bindText(binding.etNjop, NJOP)
    }

    private fun bindText(view: android.widget.EditText, key: String) {
        view.addTextChangedListener(WatchChange {
            gatheredData[key] = view.text.toString()
        })
    }

    private fun generateYears(start: Int, count: Int): List<String> {
        return (start until start + count).map { it.toString() }
    }

    // ================= DATA =================

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data ?: return

        setSpinner(binding.spJenisAlasHak, jenisList, data, JENIS)
        setSpinner(binding.msSpptTahun, tahunList, data, SPPT_TAHUN)

        setText(binding.etSppt, data, SPPT)
        setText(binding.etNjop, data, NJOP)
        setText(binding.etLuas, data, LUAS)
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