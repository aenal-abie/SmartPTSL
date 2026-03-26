package smartgis.project.app.smartgis.forms.yuridis

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.adapter.FormFragmentHolder
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.forms.BaseFormContainer
import smartgis.project.app.smartgis.utils.currentUser

class YuridisFormActivity : BaseFormContainer() {

  override fun getFireBaseDocReference(areaId: String): DocumentReference =
    Collections.getUserAreaDetailYuridis(currentUser()?.email, areaId)

  override fun init(onInit: () -> Unit) {
    val yuridisGeneral = YuridisGeneral()
    yuridisGeneral.arguments = bundle
    forms = listOf(
      FormFragmentHolder("Yuridis", yuridisGeneral),
      FormFragmentHolder("Badan Hukum", BadanHukum()),
      FormFragmentHolder("Pernyataan", Pernyataan())
    )
    onInit()
  }
}