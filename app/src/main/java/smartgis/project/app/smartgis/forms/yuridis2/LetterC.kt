package smartgis.project.app.smartgis.forms.delinasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import smartgis.project.app.smartgis.contracts.WatchChange
import smartgis.project.app.smartgis.databinding.FormLetterCBinding
import smartgis.project.app.smartgis.forms.GatherableFormFragment
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.noNull

class LetterC : GatherableFormFragment() {

    private var _binding: FormLetterCBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val NAMA_LETTER_C = "nama_letter_c"
        const val NOMOR_LETTER_C = "nomor_letter_c"
        const val KELAS_LETTER_C = "kelas_letter_c"
        const val NOMOR_PERSIL = "nomor_persil" // 🔥 FIX TYPO (percil → persil)
        const val LUAS_LETTER_C = "luas_letter_c"
        const val PREFIX = "letter_c"

        val keys = listOf(
            NAMA_LETTER_C,
            NOMOR_LETTER_C,
            KELAS_LETTER_C,
            LUAS_LETTER_C,
            NOMOR_PERSIL
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FormLetterCBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        required = keys

        val workspace = arguments?.getParcelable<Workspace>(Workspace.INTENT)

        // 🔽 Binding input
        binding.etnamaLetterC.addTextChangedListener(WatchChange {
            gatheredData[NAMA_LETTER_C] = binding.etnamaLetterC.text.toString()
        })

        binding.etnoLetterC.addTextChangedListener(WatchChange {
            gatheredData[NOMOR_LETTER_C] = binding.etnoLetterC.text.toString()
        })

        binding.etpersilLetterC.addTextChangedListener(WatchChange {
            gatheredData[NOMOR_PERSIL] = binding.etpersilLetterC.text.toString()
        })

        binding.etkelasLetterC.addTextChangedListener(WatchChange {
            gatheredData[KELAS_LETTER_C] = binding.etkelasLetterC.text.toString()
        })

        binding.etluasLetterC.addTextChangedListener(WatchChange {
            gatheredData[LUAS_LETTER_C] = binding.etluasLetterC.text.toString()
        })
    }

    override fun getKeyPrefix(): String = PREFIX

    override fun populateForm(data: Map<String, Any>?) {
        data?.apply {
            binding.etnamaLetterC.setText(get(NAMA_LETTER_C.addPrefix()).toString().noNull())
            binding.etnoLetterC.setText(get(NOMOR_LETTER_C.addPrefix()).toString().noNull())
            binding.etpersilLetterC.setText(get(NOMOR_PERSIL.addPrefix()).toString().noNull())
            binding.etkelasLetterC.setText(get(KELAS_LETTER_C.addPrefix()).toString().noNull())
            binding.etluasLetterC.setText(get(LUAS_LETTER_C.addPrefix()).toString().noNull())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 🔥 wajib
    }
}