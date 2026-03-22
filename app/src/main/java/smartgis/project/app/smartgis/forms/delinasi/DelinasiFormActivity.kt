package smartgis.project.app.smartgis.forms.delinasi

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.adapter.FormFragmentHolder
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.forms.BaseFormContainer
import smartgis.project.app.smartgis.utils.currentUser

class DelinasiFormActivity : BaseFormContainer() {
    override fun init(onInit: () -> Unit) {
        val delinasiGeneral = DelinasiGeneral()
        val areaPosition = AreaPosition()
//    val building = Building()
        delinasiGeneral.arguments = bundle
        areaPosition.arguments = bundle
//    building.arguments = bundle

        forms = listOf(
            FormFragmentHolder("Data Fisik", delinasiGeneral),
            FormFragmentHolder("Identitas Subjek", SubjectIdentity()),
            FormFragmentHolder("Letak Tanah", areaPosition),
//      FormFragmentHolder("Bangunan", building)
        )
        onInit()
    }


    override fun getFireBaseDocReference(areaId: String): DocumentReference =
        Collections.getUserAreaDetailDelinasi(currentUser()?.email, areaId)

}