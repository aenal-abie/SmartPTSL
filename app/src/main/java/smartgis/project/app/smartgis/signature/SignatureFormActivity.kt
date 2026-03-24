package smartgis.project.app.smartgis.forms.signature

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.forms.BaseFormContainer.Companion.AREA_ID

class SignatureFormActivity : BaseSignatureFormContainer() {

  companion object {
    const val INFIX = "pemilik"
  }

  override fun getDocReference(workspaceId: String?, email: String?): DocumentReference =
    Collections.getUserAreaSignature(email, intent.getStringExtra(AREA_ID)!!)

  override fun getInfix(): String {
    return INFIX
  }

}