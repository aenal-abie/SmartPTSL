package smartgis.project.app.smartgis.forms.yuridis2

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.ItemSelected
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormElemenKadasterBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.noNull

class ElemenKadaster : GatherableFormFragment() {

    private var _binding: FormElemenKadasterBinding? = null
    private val binding get() = _binding!!

    private val spenunjukBatasItems = mutableListOf<String>()
    private val tandaItems = mutableListOf<String>()
    private val petugasItems = mutableListOf<String>()
    private val pengukuranItems = mutableListOf<String>()
    private val petaDasarItems = mutableListOf<String>()
    private val persetujuanItems = mutableListOf<String>()

    companion object {
        const val NUB = "nub"
        const val NAMA = "nama"
        const val PENUNJUK_BATAS = "penunjuk_batas"
        const val TANDA_UTARA = "utara"
        const val TANDA_SELATAN = "selatan"
        const val TANDA_TIMUR = "timur"
        const val TANDA_BARAT = "barat"
        const val PETUGAS = "petugas"
        const val NAMA_PETUGAS = "nama_petugas"
        const val PENGUKURAN = "pengukuran"
        const val PETA_DASAR = "peta_dasar"

        // ✅ FIX TYPO
        const val PERS_UTARA = "persetujuan_batas_utara"
        const val PERS_TIMUR = "persetujuan_batas_timur"
        const val PERS_BARAT = "persetujuan_batas_barat"
        const val PERS_SELATAN = "persetujuan_batas_selatan"

        const val PREFIX = "elemen_kadaster"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FormElemenKadasterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spenunjukBatasItems.addAll(resources.getStringArray(R.array.penunjuk_items))
        tandaItems.addAll(resources.getStringArray(R.array.tanda_batas_items))
        petugasItems.addAll(resources.getStringArray(R.array.petugas_penetapan_items))
        pengukuranItems.addAll(resources.getStringArray(R.array.teknik_pengukuran_items))
        petaDasarItems.addAll(resources.getStringArray(R.array.ketelitian_peta_dasar_items))
        persetujuanItems.addAll(resources.getStringArray(R.array.persetujuan_batas_items))

        val workspace = arguments?.getParcelable<Workspace>(Workspace.INTENT)
        val dataSize = arguments?.getInt(MainActivity.DATA_SIZE)

        // 🔽 binding data
        binding.etNub1.addTextChangedListener(WatchChange { gatheredData[NUB] = binding.etNub1.text.toString() })
        binding.etName1.addTextChangedListener(WatchChange { gatheredData[NAMA] = binding.etName1.text.toString() })

        binding.spenunjukbatas.onItemSelectedListener =
            ItemSelected { gatheredData[PENUNJUK_BATAS] = spenunjukBatasItems[it] }

        binding.sbatasBarat.onItemSelectedListener =
            ItemSelected { gatheredData[TANDA_BARAT] = tandaItems[it] }

        binding.sbatasUtara.onItemSelectedListener =
            ItemSelected { gatheredData[TANDA_UTARA] = tandaItems[it] }

        binding.sbatasTimur.onItemSelectedListener =
            ItemSelected { gatheredData[TANDA_TIMUR] = tandaItems[it] }

        binding.sbatasSelatan.onItemSelectedListener =
            ItemSelected { gatheredData[TANDA_SELATAN] = tandaItems[it] }

        binding.spetugasPenetapan.onItemSelectedListener =
            ItemSelected { gatheredData[PETUGAS] = petugasItems[it] }

        binding.etnamaPetugas.addTextChangedListener(WatchChange {
            gatheredData[NAMA_PETUGAS] = binding.etnamaPetugas.text.toString()
        })

        binding.spengukuran.onItemSelectedListener =
            ItemSelected { gatheredData[PENGUKURAN] = pengukuranItems[it] }

        binding.spetadasar.onItemSelectedListener =
            ItemSelected { gatheredData[PETA_DASAR] = petaDasarItems[it] }

        // 🔽 generate NUB
        binding.etNub1.setText(
            String.format(
                "${workspace?.getFormattedRw()}${workspace?.getFormattedRt()}-%03d",
                dataSize ?: 0
            )
        )
    }

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        Log.i("FORM", "Elemen Kadaster $data")

        data?.apply {
            binding.etNub1.setText(get(NUB.addPrefix()).toString().noNull())
            binding.etName1.setText(get(NAMA.addPrefix()).toString().noNull())
            binding.etnamaPetugas.setText(get(NAMA_PETUGAS.addPrefix()).toString().noNull())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}