package smartgis.project.app.smartgis.forms.workspaceforms.saksi

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.documents.Collections

class DataSaksiKedua : SaksiFormContainer() {
  override fun getKeyPrefix(): String = "saksi_kedua"

  override fun getFireBaseDocReference(workspaceId: String, email: String): DocumentReference =
    Collections.getWorkspaceYuridisDataSaksiKedua(workspaceId, email)

}