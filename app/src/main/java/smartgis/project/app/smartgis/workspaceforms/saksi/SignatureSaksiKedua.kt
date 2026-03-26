package smartgis.project.app.smartgis.forms.workspaceforms.saksi

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.forms.signature.BaseSignatureFormContainer

class SignatureSaksiKedua : BaseSignatureFormContainer() {

  companion object {
    const val INFIX = "saksi2"
  }

  override fun getDocReference(workspaceId: String?, email: String?): DocumentReference =
    Collections.getWorkspaceYuridisDataSaksiKedua(workspaceId, email)

  override fun getInfix(): String {
    return INFIX
  }
}