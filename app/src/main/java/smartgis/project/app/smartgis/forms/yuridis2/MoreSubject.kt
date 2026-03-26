package smartgis.project.app.smartgis.forms.delinasi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.ItemSelected
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormDataTambahanBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.noNull

class MoreSubject : GatherableFormFragment() {

    private var _binding: FormDataTambahanBinding? = null
    private val binding get() = _binding!!

    private val statusPerkawinanItems = mutableListOf<String>()
    private val jenisKelaminItems = mutableListOf<String>()

    companion object {
        const val NOMOR_SHM = "nomor_shm_dan_su"
        const val STATUS_PERKAWINAN = "status_perkawinan"
        const val JENIS_KELAMIN = "jenis_kelamin"
        const val NAMA_IBU_KANDUNG = "nama_ibu_kandung"
        const val NOMOR_HANDPHONE = "nomor_handphone"
        const val NAMA_WAJIB_PAJAK = "nama_wajib_pajak"
        const val NOMOR_SPPT = "nomor_sppt"
        const val LUAS_SPPT = "luas_sppt"
        const val LUAS_PERMOHONAN = "luas_permohonan"
        const val NJOP_PER_M2 = "njop_per_m2"
        const val PREFIX = "more_subject"

        val keys = listOf(
            NOMOR_SHM,
            STATUS_PERKAWINAN,
            JENIS_KELAMIN,
            NAMA_IBU_KANDUNG,
            NOMOR_HANDPHONE,
            NAMA_WAJIB_PAJAK,
            NOMOR_SPPT,
            LUAS_SPPT,
            LUAS_PERMOHONAN,
            NJOP_PER_M2
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormDataTambahanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys

        statusPerkawinanItems.addAll(resources.getStringArray(R.array.area_status_items))
        jenisKelaminItems.addAll(resources.getStringArray(R.array.jenis_kelamin_items)) // ✅ FIX

        // 🔽 Binding Text
        binding.etshmSu.addTextChangedListener(WatchChange {
            gatheredData[NOMOR_SHM] = binding.etshmSu.text.toString()
        })

        binding.sstatusPerkawinan.onItemSelectedListener =
            ItemSelected { gatheredData[STATUS_PERKAWINAN] = statusPerkawinanItems[it] }

        binding.sjenisKelamin.onItemSelectedListener =
            ItemSelected { gatheredData[JENIS_KELAMIN] = jenisKelaminItems[it] }

        binding.etnamaIbu.addTextChangedListener(WatchChange {
            gatheredData[NAMA_IBU_KANDUNG] = binding.etnamaIbu.text.toString()
        })

        binding.etnoHp.addTextChangedListener(WatchChange {
            gatheredData[NOMOR_HANDPHONE] = binding.etnoHp.text.toString()
        })

        binding.etnamaWp.addTextChangedListener(WatchChange {
            gatheredData[NAMA_WAJIB_PAJAK] = binding.etnamaWp.text.toString()
        })

        binding.etnoSppt.addTextChangedListener(WatchChange {
            gatheredData[NOMOR_SPPT] = binding.etnoSppt.text.toString()
        })

        binding.etluasSppt.addTextChangedListener(WatchChange {
            gatheredData[LUAS_SPPT] = binding.etluasSppt.text.toString()
        })

        binding.etluasMohon.addTextChangedListener(WatchChange {
            gatheredData[LUAS_PERMOHONAN] = binding.etluasMohon.text.toString()
        })

        binding.etnjopPerM2.addTextChangedListener(WatchChange {
            gatheredData[NJOP_PER_M2] = binding.etnjopPerM2.text.toString()
        })
    }

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        Log.i("FORM", "More Subject $data")

        data?.apply {
            binding.etshmSu.setText(get(NOMOR_SHM.addPrefix()).toString().noNull())

            statusPerkawinanItems.withIndex()
                .find { it.value == get(STATUS_PERKAWINAN.addPrefix()).toString() }
                ?.let { binding.sstatusPerkawinan.setSelection(it.index) }

            jenisKelaminItems.withIndex()
                .find { it.value == get(JENIS_KELAMIN.addPrefix()).toString() }
                ?.let { binding.sjenisKelamin.setSelection(it.index) }

            binding.etnamaIbu.setText(get(NAMA_IBU_KANDUNG.addPrefix()).toString().noNull())
            binding.etnoHp.setText(get(NOMOR_HANDPHONE.addPrefix()).toString().noNull())
            binding.etnamaWp.setText(get(NAMA_WAJIB_PAJAK.addPrefix()).toString().noNull())
            binding.etnoSppt.setText(get(NOMOR_SPPT.addPrefix()).toString().noNull())
            binding.etluasSppt.setText(get(LUAS_SPPT.addPrefix()).toString().noNull())
            binding.etluasMohon.setText(get(LUAS_PERMOHONAN.addPrefix()).toString().noNull())
            binding.etnjopPerM2.setText(get(NJOP_PER_M2.addPrefix()).toString().noNull())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 🔥 wajib
    }
}