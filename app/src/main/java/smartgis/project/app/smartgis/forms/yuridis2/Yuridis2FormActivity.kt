package smartgis.project.app.smartgis.forms.yuridis2

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.adapter.FormFragmentHolder
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.forms.BaseFormContainer
import smartgis.project.app.smartgis.forms.delinasi.LetterC
import smartgis.project.app.smartgis.forms.delinasi.MoreSubject

import smartgis.project.app.smartgis.utils.currentUser

class Yuridis2FormActivity : BaseFormContainer() {

  override fun getFireBaseDocReference(areaId: String): DocumentReference =
    Collections.getUserAreaDetailYuridisII(currentUser()?.email, areaId)

  override fun init(onInit: () -> Unit) {
    val elemenkadaster = ElemenKadaster()
    elemenkadaster.arguments = bundle

    forms = listOf(
      FormFragmentHolder("Letter C", LetterC()),
      FormFragmentHolder("Data Yuridis II", MoreSubject()),
      FormFragmentHolder("Elemen Kadaster", elemenkadaster)
    )
    onInit()
  }
}