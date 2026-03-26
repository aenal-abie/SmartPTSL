package smartgis.project.app.smartgis.forms.yuridis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormPernyataanBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.noNull

class Pernyataan : GatherableFormFragment() {

    private var _binding: FormPernyataanBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val PERNYATAAN = "pernyataan"
        const val PREFIX = "pernyataan"
        val keys = listOf(PERNYATAAN)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormPernyataanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys

        binding.etStatement.addTextChangedListener(WatchChange {
            gatheredData[PERNYATAAN] = binding.etStatement.text.toString()
        })
    }

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data?.apply {
            binding.etStatement.setText(
                get(PERNYATAAN.addPrefix()).toString().noNull()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 🔥 wajib untuk fragment
    }
}