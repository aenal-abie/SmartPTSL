package smartgis.project.app.smartgis.forms.workspaceforms.gu

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.documents.Collections

class DataPersonalGuPertama : BaseDataPersonalGu() {
  override fun getKeyPrefix(): String = BaseDataPersonalGu.PREFIX_GU1

  override fun getFirebaseDocReference(workspaceId: String, email: String): DocumentReference =
    Collections.getWorkspaceGuPertama(workspaceId, email)

}