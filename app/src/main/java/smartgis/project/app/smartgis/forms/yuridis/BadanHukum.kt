package smartgis.project.app.smartgis.forms.yuridis

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormBadanHukumBinding
import smartgis.project.app.smartgis.events.JenisEvent
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.utils.*
import java.util.*

class BadanHukum : GatherableFormFragment() {

    private var _binding: FormBadanHukumBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val BADAN_HUKUM = "badan_hukum"
        const val NO_AKTA_PENDIRIAN = "no_akta_pendirian"
        const val TANGGAL_AKTA_PENDIRIAN = "tanggal_akta_pendirian"
        const val PREFIX = "badan_hukum"

        val keys = listOf(BADAN_HUKUM, NO_AKTA_PENDIRIAN, TANGGAL_AKTA_PENDIRIAN)
    }

    private var jenisEvent: String? = null
    private val initialCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormBadanHukumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys

        binding.etTanggalAktaPendirian.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                binding.etTanggalAktaPendirian.setText(
                    initialCalendar.year(year).month(month).day(dayOfMonth).time.toSimpleDate()
                )
            }, initialCalendar.year(), initialCalendar.month(), initialCalendar.day()).show()
        }

        binding.etBadanHukum.addTextChangedListener(WatchChange {
            gatheredData[BADAN_HUKUM] = binding.etBadanHukum.text.toString()
        })

        binding.etNoAktaPendirian.addTextChangedListener(WatchChange {
            gatheredData[NO_AKTA_PENDIRIAN] = binding.etNoAktaPendirian.text.toString()
        })

        binding.etTanggalAktaPendirian.addTextChangedListener(WatchChange {
            gatheredData[TANGGAL_AKTA_PENDIRIAN] =
                binding.etTanggalAktaPendirian.text.toString()
        })
    }

    @Subscribe
    fun onJenisSelected(jenisKepemilikan: JenisEvent) {
        jenisEvent = jenisKepemilikan.jenis

        when (jenisEvent) {
            getString(R.string.perorangan) -> binding.badanHukumContainer.gone()
            getString(R.string.badan_hukum) -> binding.badanHukumContainer.show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onStop()
    }

    override fun isValid(): Boolean {
        return if (jenisEvent == getString(R.string.badan_hukum)) {
            super.isValid()
        } else {
            true
        }
    }

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        Log.i("FORM", " $data".addPrefix())

        data?.apply {
            binding.etBadanHukum.setText(get(BADAN_HUKUM.addPrefix()).toString().noNull())
            binding.etNoAktaPendirian.setText(get(NO_AKTA_PENDIRIAN.addPrefix()).toString().noNull())
            binding.etTanggalAktaPendirian.setText(
                get(TANGGAL_AKTA_PENDIRIAN.addPrefix()).toString().noNull()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 🔥 penting untuk hindari memory leak
    }
}