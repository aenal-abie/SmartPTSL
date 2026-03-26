package smartgis.project.app.smartgis.forms.yuridis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import org.greenrobot.eventbus.EventBus
import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.ItemSelected
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormYuridisGeneralBinding
import smartgis.project.app.smartgis.events.JenisEvent
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.noNull
import smartgis.project.app.smartgis.utils.year
import java.util.*

class YuridisGeneral : GatherableFormFragment() {

    private var _binding: FormYuridisGeneralBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val NUB = "nub"
        const val AGAMA = "agama"
        const val JALAN_BLOK = "jalan_blok"
        const val NJOP = "njop"
        const val UTARA = "utara"
        const val TIMUR = "timur"
        const val SELATAN = "selatan"
        const val BARAT = "barat"
        const val JENIS = "jenis"
        const val DIKUASAI_SEJAK = "dikuasai_sejak"
        const val PREFIX = "yuridis"

        val keys = listOf(
            NUB, AGAMA, JALAN_BLOK, NJOP,
            UTARA, TIMUR, SELATAN, BARAT,
            JENIS, DIKUASAI_SEJAK
        )
    }

    private val religions = mutableListOf<String>()
    private val dikuasaiSejakYears = mutableListOf<Int>()
    private val initialCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormYuridisGeneralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys

        religions.addAll(resources.getStringArray(R.array.religion_items))
        dikuasaiSejakYears.addAll(1900..initialCalendar.year())

        binding.sDikuasaiSejak.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dikuasaiSejakYears)

        val workspace = arguments?.getParcelable<Workspace>(Workspace.INTENT)
        val dataSize = arguments?.getInt(MainActivity.DATA_SIZE)

        // 🔽 TextWatcher
        binding.etNub.addTextChangedListener(WatchChange {
            gatheredData[NUB] = binding.etNub.text.toString()
        })

        binding.sReligions.onItemSelectedListener =
            ItemSelected { gatheredData[AGAMA] = religions[it] }

        binding.etJalanBlok.addTextChangedListener(WatchChange {
            gatheredData[JALAN_BLOK] = binding.etJalanBlok.text.toString()
        })

        binding.etNjop.addTextChangedListener(WatchChange {
            gatheredData[NJOP] = binding.etNjop.text.toString()
        })

        binding.etUtara.addTextChangedListener(WatchChange {
            gatheredData[UTARA] = binding.etUtara.text.toString()
        })

        binding.etTimur.addTextChangedListener(WatchChange {
            gatheredData[TIMUR] = binding.etTimur.text.toString()
        })

        binding.etSelatan.addTextChangedListener(WatchChange {
            gatheredData[SELATAN] = binding.etSelatan.text.toString()
        })

        binding.etBarat.addTextChangedListener(WatchChange {
            gatheredData[BARAT] = binding.etBarat.text.toString()
        })

        binding.sDikuasaiSejak.onItemSelectedListener =
            ItemSelected { gatheredData[DIKUASAI_SEJAK] = dikuasaiSejakYears[it] }

        binding.sDikuasaiSejak.setSelection(70)

        binding.rgJenis.setOnCheckedChangeListener { _, checkedId ->
            val selected = view.findViewById<RadioButton>(checkedId)?.text?.toString()
            if (selected != null) {
                gatheredData[JENIS] = selected
                EventBus.getDefault().post(JenisEvent(selected))
            }
        }

        // 🔽 Generate NUB
        binding.etNub.setText(
            String.format(
                "${workspace?.getFormattedRw()}${workspace?.getFormattedRt()}-%03d",
                dataSize ?: 0
            )
        )
    }

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data?.apply {

            binding.etNub.setText(get(NUB.addPrefix()).toString().noNull())

            religions.withIndex()
                .find { it.value == get(AGAMA.addPrefix()).toString() }
                ?.let { binding.sReligions.setSelection(it.index) }

            val tahun = get(DIKUASAI_SEJAK.addPrefix()).toString().toIntOrNull()
            tahun?.let {
                dikuasaiSejakYears.withIndex()
                    .find { it.value == tahun }
                    ?.let { binding.sDikuasaiSejak.setSelection(it.index) }
            }

            binding.etJalanBlok.setText(get(JALAN_BLOK.addPrefix()).toString().noNull())
            binding.etNjop.setText(get(NJOP.addPrefix()).toString().noNull())
            binding.etUtara.setText(get(UTARA.addPrefix()).toString().noNull())
            binding.etTimur.setText(get(TIMUR.addPrefix()).toString().noNull())
            binding.etSelatan.setText(get(SELATAN.addPrefix()).toString().noNull())
            binding.etBarat.setText(get(BARAT.addPrefix()).toString().noNull())

            when (get(JENIS.addPrefix())) {
                getString(R.string.perorangan) -> binding.rgJenis.check(R.id.rbPerorangan)
                getString(R.string.badan_hukum) -> binding.rgJenis.check(R.id.rbBadanHukum)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 🔥 wajib
    }
}