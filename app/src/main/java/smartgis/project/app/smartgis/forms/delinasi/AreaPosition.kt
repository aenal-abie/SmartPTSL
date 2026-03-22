package smartgis.project.app.smartgis.forms.delinasi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.ItemSelected
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormAreaPositionBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.noNull

class AreaPosition : GatherableFormFragment() {

    private val areaStatusItems = mutableListOf<String>()
    private val statusPenggunaanItems = mutableListOf<String>()
    private val buktiPenguasaanItems = mutableListOf<String>()
    private lateinit var binding: FormAreaPositionBinding

    companion object {
        const val KELURAHAN = "kelurahan"
        const val DUSUN = "dusun"
        const val RT = "rt"
        const val RW = "rw"
        const val ET_PBBB_NO = "no_blok_pbbb"
        const val AREA_STATUS = "status_tanah"
        const val STATUS_PENGGUNAAN = "status_penggunaan"
        const val SHAT_NO = "shat_no"
        const val BUKTI_PENGUASAAN = "bukti_penguasaan"
        const val LUAS_TANAH = "luas_tanah"
        const val KETERANGAN = "keterangan"
        const val PREFIX = "area_position"
        val keys = listOf(
            KELURAHAN,
            DUSUN,
            RT,
            RW,
            ET_PBBB_NO,
            AREA_STATUS,
            STATUS_PENGGUNAAN,
            BUKTI_PENGUASAAN,
            LUAS_TANAH,
            KETERANGAN
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FormAreaPositionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        required = keys

        areaStatusItems.addAll(resources.getStringArray(R.array.area_status_items))
        statusPenggunaanItems.addAll(resources.getStringArray(R.array.status_penggunaan_items))
        buktiPenguasaanItems.addAll(resources.getStringArray(R.array.bukti_penguasaan_items))
        val workspace = arguments?.getParcelable<Workspace>(Workspace.INTENT)

        binding.etKelurahan.addTextChangedListener(WatchChange {
            gatheredData[KELURAHAN] = binding.etKelurahan.text.toString()
        })
        binding.etDusun.addTextChangedListener(WatchChange {
            gatheredData[DUSUN] = binding.etDusun.text.toString()
        })
        binding.etRt.addTextChangedListener(WatchChange {
            gatheredData[RT] = binding.etRt.text.toString()
        })
        binding.etRw.addTextChangedListener(WatchChange {
            gatheredData[RW] = binding.etRw.text.toString()
        })
        binding.etPbbbBlockNo.addTextChangedListener(WatchChange {
            gatheredData[ET_PBBB_NO] = binding.etPbbbBlockNo.text.toString()
        })
        binding.etLuasTanah.addTextChangedListener(WatchChange {
            gatheredData[AREA_STATUS] = binding.etLuasTanah.text.toString()
        })
        binding.sAreaStatus.onItemSelectedListener = ItemSelected {
            areaStatusItems[it].apply { gatheredData[AREA_STATUS] = this }
        }
        binding.sStatusPenggunaan.onItemSelectedListener = ItemSelected {
            statusPenggunaanItems[it].apply {
                gatheredData[STATUS_PENGGUNAAN] = this
            }
        }
        binding.sBuktiPenguasaan.onItemSelectedListener = ItemSelected {
            if (it == 6) {
                binding.shatNoLabel.visibility = View.VISIBLE
                binding.etShatNo.visibility = View.VISIBLE
            } else {
                gatheredData[SHAT_NO] = ""
                binding.shatNoLabel.visibility = View.GONE
                binding.etShatNo.visibility = View.GONE
            }
            buktiPenguasaanItems.get(it).apply { gatheredData[BUKTI_PENGUASAAN] = this }
        }
        binding.etLuasTanah.addTextChangedListener(WatchChange {
            gatheredData[LUAS_TANAH] = binding.etLuasTanah.text.toString()
        })
        binding.etDescription.addTextChangedListener(WatchChange {
            gatheredData[KETERANGAN] = binding.etDescription.text.toString()
        })

        binding.etShatNo.addTextChangedListener(WatchChange {
            gatheredData[SHAT_NO] = binding.etShatNo.text.toString()
        })

        binding.etRt.setText(workspace?.getFormattedRt().toString())
        binding.etRw.setText(workspace?.getFormattedRw().toString())
        binding.etLuasTanah.setText(arguments?.getInt(MainActivity.AREA).toString())

    }

    override fun getKeyPrefix(): String {
        return PREFIX
    }

    override fun populateForm(data: Map<String, Any>?) {
        Log.i("FORM", "area position $data")
        data?.apply {
            binding.etKelurahan?.setText(get(KELURAHAN.addPrefix()).toString().noNull())
            binding.etDusun?.setText(get(DUSUN.addPrefix()).toString().noNull())
            binding.etRt?.setText(get(RT.addPrefix()).toString().noNull())
            binding.etRw?.setText(get(RW.addPrefix()).toString().noNull())
            binding.etPbbbBlockNo?.setText(get(ET_PBBB_NO.addPrefix()).toString().noNull())
            binding.etLuasTanah?.setText(get(LUAS_TANAH.addPrefix()).toString().noNull())
            areaStatusItems.withIndex()
                .find { indexedValue -> indexedValue.value == get(AREA_STATUS.addPrefix()).toString() }
                ?.apply { binding.sAreaStatus?.setSelection(index) }
            statusPenggunaanItems.withIndex()
                .find { it.value == get(STATUS_PENGGUNAAN.addPrefix()).toString() }
                ?.apply { binding.sStatusPenggunaan?.setSelection(index) }
            buktiPenguasaanItems.withIndex().find { it.value == get(BUKTI_PENGUASAAN.addPrefix()) }
                ?.apply { binding.sBuktiPenguasaan?.setSelection(index) }
            binding.etLuasTanah?.setText(get(LUAS_TANAH.addPrefix()).toString().noNull())
            binding.etShatNo?.setText(get(SHAT_NO.addPrefix()).toString().noNull())
            binding.etDescription?.setText(get(KETERANGAN.addPrefix()).toString().noNull())
        }
    }

}