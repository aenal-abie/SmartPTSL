package smartgis.project.app.smartgis.forms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import smartgis.project.app.smartgis.contracts.GatherableData
import smartgis.project.app.smartgis.utils.prefix

open class GatherableFormFragment : Fragment(), GatherableData {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    EventBus.getDefault().register(this)
  }

  override fun getData(): MutableMap<String, Any> {
    if (getKeyPrefix().isNotEmpty()) {
      val tmp = mutableMapOf<String, Any>()
      gatheredData.forEach {
        tmp["${getKeyPrefix()}_${it.key}"] = it.value
      }
      return tmp
    }
    return gatheredData
  }

  protected val gatheredData = mutableMapOf<String, Any>()
  protected var required = listOf<String>()

  open fun isValid(): Boolean =
    nonEmptyFormKeys().size == required.size

  open fun getKeyPrefix(): String = ""

  private fun nonEmptyFormKeys(): List<String> =
    gatheredData.filter { !it.value.toString().trim().isEmpty() }.keys.toList()

  override fun onStart() {
    super.onStart()
    if (!EventBus.getDefault().isRegistered(this))
      EventBus.getDefault().register(this)
  }

  override fun onStop() {
    EventBus.getDefault().unregister(this)
    super.onStop()
  }

  protected fun String.addPrefix() = prefix(getKeyPrefix())

  @Subscribe
  fun onFormData(data: FormDataEvent) {
    populateForm(data.data)
  }

  open fun populateForm(data: Map<String, Any>?) {}

}

data class FormDataEvent(val data: Map<String, Any>?)