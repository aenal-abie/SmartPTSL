package smartgis.project.app.smartgis.forms.workspaceforms.gu

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.forms.signature.BaseSignatureFormContainer

class SignatureGuPertama : BaseSignatureFormContainer() {

  companion object {
    const val INFIX = BaseDataPersonalGu.PREFIX_GU1
  }

  override fun getDocReference(workspaceId: String?, email: String?): DocumentReference =
    Collections.getWorkspaceGuPertama(workspaceId, email)

  override fun getInfix(): String {
    return INFIX
  }

}