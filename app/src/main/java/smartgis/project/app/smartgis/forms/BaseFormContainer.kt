package smartgis.project.app.smartgis.forms

import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
// import kotlinx.android.synthetic.main.form_container.*
import org.greenrobot.eventbus.EventBus
// import org.jetbrains.anko.alert
// import org.jetbrains.anko.design.snackbar
// import org.jetbrains.anko.okButton
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.MainActivity.Companion.AREA
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.adapter.FormFragmentHolder
import smartgis.project.app.smartgis.adapter.ViewPagerFormFragmentAdapter
import smartgis.project.app.smartgis.databinding.ActivityMainBinding
import smartgis.project.app.smartgis.databinding.FormContainerBinding
import smartgis.project.app.smartgis.models.Workspace

abstract class BaseFormContainer : LoginRequiredActivity() {

    protected var forms = listOf<FormFragmentHolder>()
    private var areaId: String = ""
    protected var bundle: Bundle = Bundle()

    abstract fun init(onInit: () -> Unit)

    private lateinit var binding: FormContainerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FormContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        areaId = intent.getStringExtra(AREA_ID)!!
        bundle.putParcelable(
            Workspace.INTENT,
            intent.getParcelableExtra<Workspace>(Workspace.INTENT)
        )
        bundle.putInt(MainActivity.DATA_SIZE, intent.getIntExtra(MainActivity.DATA_SIZE, 0))
        bundle.putInt(AREA, intent.getIntExtra(AREA, 0))
        init {
            val adapter = ViewPagerFormFragmentAdapter(supportFragmentManager, forms)
            binding.formContainer.offscreenPageLimit = forms.size
            binding.formContainer.adapter = adapter

            changeTitle(0)
            toggleButton(0)
        }

        binding.formContainer.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                changeTitle(p0)
                toggleButton(p0)
            }
        })

        binding.btnNext.setOnClickListener {
            when (binding.btnNext.text) {
                DONE -> {
                    if (isFormValid()) sendData()
//                    else alert(
//                        getString(R.string.can_not_send_data),
//                        getString(R.string.attention)
//                    ) { okButton { } }.show()
                }
                NEXT -> binding.formContainer.currentItem++
            }
        }
        binding.btnBack.setOnClickListener { binding.formContainer.currentItem-- }
    }

    private fun sendData() {
        val data = mutableMapOf<String, Any>()
        forms.map { it.fragment.getData() }.forEach { data.putAll(it) }
        Log.i(localClassName, data.toString())
        getFireBaseDocReference(areaId)
            .set(data, SetOptions.merge()).apply {
                addOnSuccessListener { Log.i(localClassName, "Data successfully merged!") }
                addOnFailureListener { Log.i(localClassName, "error merge ${it.localizedMessage}") }
            }.also {
//            binding.container.snackbar(getString(R.string.success_save_data))
            }
    }

    abstract fun getFireBaseDocReference(areaId: String): DocumentReference

    var loaded = false
    override fun onStart() {
        super.onStart()
        getFireBaseDocReference(areaId).addSnapshotListener(this) { doc, _ ->
            if (loaded) {
                EventBus.getDefault().post(FormDataEvent(doc?.data))
                finish()
            }
            Log.i(localClassName, "Got ${doc?.data}")
            loaded = true
        }
        getFireBaseDocReference(areaId).get(Source.CACHE).addOnSuccessListener {
            EventBus.getDefault().post(FormDataEvent(it?.data))
        }
    }

    protected var checkedKey = true

    private fun isFormValid(): Boolean = forms.filter {
        it.fragment.isValid()
    }.size >= forms.size || checkedKey

    companion object {
        const val DONE = "Selesai"
        const val NEXT = "Berikutnya"
        const val AREA_ID = "AREA_ID"
        const val DOC_TYPE = "TYPE DOCUMENT"
    }

    private fun toggleButton(position: Int) {
        if ((position + 1) == forms.size) {
            binding.btnNext.text = DONE
            binding.btnBack.isEnabled = false
        } else {
            binding.btnNext.isEnabled = true
            binding.btnNext.text = NEXT
        }
        binding.btnBack.isEnabled = position != 0
    }

    private fun changeTitle(position: Int) {
        binding.titleContainer.text = forms[position].title
    }

}