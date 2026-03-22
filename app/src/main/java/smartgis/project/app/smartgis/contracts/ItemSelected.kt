package smartgis.project.app.smartgis.contracts

import android.view.View
import android.widget.AdapterView

class ItemSelected(private val onSelected: (Int) -> Unit) : AdapterView.OnItemSelectedListener {
  override fun onNothingSelected(parent: AdapterView<*>?) {

  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    onSelected(position)
  }

}