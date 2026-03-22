package smartgis.project.app.smartgis.forms.delinasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import kotlinx.android.synthetic.main.form_delinasi.etNoHak
//import kotlinx.android.synthetic.main.form_delinasi.etYuriFileNo
//import kotlinx.android.synthetic.main.form_delinasi.sCluster
//import kotlinx.android.synthetic.main.form_delinasi.sJenisHak
import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.ItemSelected
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormAreaPositionBinding
import smartgis.project.app.smartgis.databinding.FormDelinasiBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.noNull

class DelinasiGeneral : GatherableFormFragment() {

    companion object {
        const val YURI_FILE_NO = "no_berkas_yuri"
        const val CLUSTER = "klaster"
        const val HAK = "hak"
        const val JENIS_HAK = "jenis_hak"
        const val PREFIX = "fisik"
        val keys = listOf(YURI_FILE_NO, CLUSTER)
    }

    private val clusterItems = mutableListOf<String>()
    private val jenisHakItems = mutableListOf<String>()
    private lateinit var binding: FormDelinasiBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FormDelinasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        required = keys

        clusterItems.addAll(resources.getStringArray(R.array.cluster_items))
        jenisHakItems.addAll(resources.getStringArray(R.array.bukti_penguasaan_items))

        val workspace = arguments?.getParcelable<Workspace>(Workspace.INTENT)
        val dataSize = arguments?.getInt(MainActivity.DATA_SIZE)

        binding.etYuriFileNo.addTextChangedListener(WatchChange {
            gatheredData[YURI_FILE_NO] = binding.etYuriFileNo.text.toString()
        })

        binding.etNoHak.addTextChangedListener(WatchChange {
            gatheredData[HAK] = binding.etNoHak.text.toString()
        })

        binding.sCluster.onItemSelectedListener = ItemSelected {
            clusterItems[it].apply { gatheredData[CLUSTER] = this }
        }

        binding.etYuriFileNo.setText(
            String.format(
                "${workspace?.getFormattedRw()}${workspace?.getFormattedRt()}-%03d",
                dataSize
            )
        )
        binding.sJenisHak.onItemSelectedListener = ItemSelected {
            jenisHakItems.get(it).apply { gatheredData[JENIS_HAK] = this }
        }
    }

    override fun getKeyPrefix(): String {
        return PREFIX
    }

    override fun populateForm(data: Map<String, Any>?) {
        data?.apply {
            get(YURI_FILE_NO.addPrefix())?.apply {
                binding.etYuriFileNo?.setText(toString().noNull())
            }
            get(HAK.addPrefix())?.apply {
                binding.etNoHak?.setText(toString().noNull())
            }
            clusterItems.withIndex()
                .find { indexedValue -> indexedValue.value == get(CLUSTER.addPrefix()).toString() }
                ?.index?.let { index -> binding.sCluster?.setSelection(index) }
            jenisHakItems.withIndex().find { it.value == get(JENIS_HAK.addPrefix()) }
                ?.apply { binding.sJenisHak?.setSelection(index) }
        }
    }

}