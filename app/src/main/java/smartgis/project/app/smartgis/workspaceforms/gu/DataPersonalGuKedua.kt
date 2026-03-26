package smartgis.project.app.smartgis.forms.workspaceforms.gu

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.documents.Collections

class DataPersonalGuKedua : BaseDataPersonalGu() {
  override fun getKeyPrefix(): String = BaseDataPersonalGu.PREFIX_GU2

  override fun getFirebaseDocReference(workspaceId: String, email: String): DocumentReference =
    Collections.getWorkspaceGuKedua(workspaceId, email)

}