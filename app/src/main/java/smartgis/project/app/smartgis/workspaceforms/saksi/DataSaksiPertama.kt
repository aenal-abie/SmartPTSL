package smartgis.project.app.smartgis.forms.workspaceforms.saksi

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.documents.Collections

class DataSaksiPertama : SaksiFormContainer() {
  override fun getFireBaseDocReference(workspaceId: String, email: String): DocumentReference =
    Collections.getWorkspaceYuridisDataSaksiPertama(workspaceId, email)

  override fun getKeyPrefix(): String = "saksi_pertama"

}