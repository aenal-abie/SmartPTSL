package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormPphBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.noNull

class YuridisPPH : GatherableFormFragment() {

    companion object {
        const val STATUS = "status"
        const val TANGGAL = "tanggal"
        const val NOMOR = "nomor"
        const val NILAI = "nilai"
        const val PREFIX = "pph"

        val keys = listOf(STATUS, TANGGAL, NOMOR, NILAI)
    }

    private var _binding: FormPphBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormPphBinding.inflate(inflater, container, false)
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
        setupRadio()
    }

    // ================= SETUP =================

    private fun setupWatcher() {
        bindText(binding.etTanggal, TANGGAL)
        bindText(binding.etNomor, NOMOR)
        bindText(binding.etNilai, NILAI)
    }

    private fun bindText(view: android.widget.EditText, key: String) {
        view.addTextChangedListener(WatchChange {
            gatheredData[key] = view.text.toString()
        })
    }

    private fun setupRadio() {
//        binding.radioGroupStatus.setOnCheckedChangeListener { _, checkedId ->
//            val value = when (checkedId) {
//                binding.rbLunas.id -> "lunas"
//                binding.rbTerhutang.id -> "terhutang"
//                binding.rbNihil.id -> "nihil"
//                else -> ""
//            }
//            gatheredData[STATUS] = value
//        }
    }

    // ================= DATA =================

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data ?: return

        setText(binding.etNomor, data, NOMOR)
        setText(binding.etTanggal, data, TANGGAL)
        setText(binding.etNilai, data, NILAI)

        setStatus(data)
    }

    private fun setText(
        view: android.widget.EditText,
        data: Map<String, Any>,
        key: String
    ) {
        view.setText(data[key.addPrefix()].toString().noNull())
    }

    private fun setStatus(data: Map<String, Any>) {
        when (data[STATUS.addPrefix()].toString().noNull()) {
            "lunas" -> binding.rbLunas.isChecked = true
            "terhutang" -> binding.rbTerhutang.isChecked = true
            "nihil" -> binding.rbNihil.isChecked = true
        }
    }
}