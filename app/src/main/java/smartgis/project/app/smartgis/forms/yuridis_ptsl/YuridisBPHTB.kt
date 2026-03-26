package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormBphtbBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.noNull

class YuridisBPHTB : GatherableFormFragment() {

    companion object {
        const val STATUS = "status"
        const val NOP = "nop"
        const val NOMOR_BUKTI = "nomor_bukti"
        const val NILAI = "nilai"
        const val PREFIX = "bphtb"

        val keys = listOf(STATUS, NOP, NOMOR_BUKTI, NILAI)
    }

    private var _binding: FormBphtbBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormBphtbBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys
        setupRadioGroup()
        setupInputWatcher()
        setupDefaultButton()
    }

    // ================= SETUP =================

    private fun setupRadioGroup() {
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

    private fun setupInputWatcher() {
        bindText(binding.etNop, NOP)
        bindText(binding.etNomorBuktiPembayaran, NOMOR_BUKTI)
        bindText(binding.etNilaiBphtb, NILAI)
    }

    private fun bindText(view: android.widget.EditText, key: String) {
        view.addTextChangedListener(WatchChange {
            gatheredData[key] = view.text.toString()
        })
    }

    private fun setupDefaultButton() {
        binding.btnCompeleteEmpetyValue.setOnClickListener {

            setDefaultIfEmpty(binding.etNop)
            setDefaultIfEmpty(binding.etNomorBuktiPembayaran)
            setDefaultIfEmpty(binding.etNilaiBphtb)

//            if (binding.radioGroupStatus.checkedRadioButtonId == -1) {
//                binding.rbNihil.isChecked = true
//                gatheredData[STATUS] = "nihil"
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

        setText(binding.etNop, data, NOP)
        setText(binding.etNomorBuktiPembayaran, data, NOMOR_BUKTI)
        setText(binding.etNilaiBphtb, data, NILAI)
        setRadioValue(data)
    }

    private fun setText(
        view: android.widget.EditText,
        data: Map<String, Any>,
        key: String
    ) {
        view.setText(data[key.addPrefix()].toString().noNull())
    }

    private fun setRadioValue(data: Map<String, Any>) {
        when (data[STATUS.addPrefix()].toString().noNull()) {
            "lunas" -> binding.rbLunas.isChecked = true
            "terhutang" -> binding.rbTerhutang.isChecked = true
            "nihil" -> binding.rbNihil.isChecked = true
        }
    }
}