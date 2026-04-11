package smartgis.project.app.smartgis.export

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
//import kotlinx.android.synthetic.main.activity_export.*
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.models.ImageHolder
import smartgis.project.app.smartgis.models.RtkStatusHolder
import smartgis.project.app.smartgis.utils.currentUser

open class BaseExportableAreaDetail : BaseExportableData() {

  protected val delinasiHolders = mutableListOf<DataAndReferenceHolder>()
  protected val pemilikSignatureHolders = mutableListOf<DataAndReferenceHolder>()
  protected val yuridisHolders = mutableListOf<DataAndReferenceHolder>()
  protected val yuridis2Holders = mutableListOf<DataAndReferenceHolder>()
  protected val dataHolders = mutableListOf<RtkStatusHolder>()
  protected val bapendaHolders = mutableListOf<DataAndReferenceHolder>()
  protected val foto = mutableListOf<ImageHolder>()
  protected val ip4tdaHolders = mutableListOf<DataAndReferenceHolder>()
  protected val areas = mutableListOf<DocumentSnapshot>()
  protected val saksi1 = mutableMapOf<String, Any>()
  protected val saksi2 = mutableMapOf<String, Any>()
  protected val dataGuPertama = mutableMapOf<String, Any>()
  protected val dataGuKedua = mutableMapOf<String, Any>()
  private var delinasiCounter = 0
  private var statusCounter = 0
  private var yuridisCounter = 0
  private var yuridis2Counter = 0
  private var bapendaCounter = 0
  private var ip4tCounter = 0

  override fun loadData(dataLoaded: () -> Unit) {
      Log.d("cek workspace", workspace?.id.toString());
    delinasiHolders.clear()
    dataHolders.clear()
    yuridisHolders.clear()
    delinasiCounter = 0
    yuridisCounter = 0
    yuridis2Counter = 0
    statusCounter = 0
    bapendaCounter = 0
    showLoading()
    Collections.getUserDrawnAreas(currentUser()?.email)
      .whereEqualTo("workspace_id", workspace?.id)
      .addSnapshotListener(this) { doc, _ ->
          exportBinding.llDescContainer.text = "Jumlah area yang akan diexport: ${doc?.size()}"
        doc?.documentChanges?.forEach {
          val snapshot = it.document

          Collections.getUserAreaRtkData(currentUser()?.email, snapshot.reference.id)
            .get()
            .addOnSuccessListener(this) { querySnapshot ->
              statusCounter++
              val statusItem =
                querySnapshot.documents.map { documentSnapshot -> documentSnapshot.data }
              dataHolders.add(RtkStatusHolder(snapshot.reference.id, statusItem))
              if (delinasiCounter == doc.size() && yuridisCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }.addOnFailureListener {
              statusCounter++
              if (delinasiCounter == doc.size() && yuridisCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }

          Collections.getUserAreaDetailDelinasi(currentUser()?.email, snapshot.reference.id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
              delinasiCounter++
              delinasiHolders.add(
                DataAndReferenceHolder(
                  snapshot.reference.id,
                  documentSnapshot.data
                )
              )
              if (delinasiCounter == doc.size() && yuridisCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }.addOnFailureListener {
              delinasiCounter++
              if (delinasiCounter == doc.size() && yuridisCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }

          Collections.getUserAreaDetailYuridis(currentUser()?.email, snapshot.reference.id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
              yuridisCounter++
              yuridisHolders.add(
                DataAndReferenceHolder(
                  snapshot.reference.id,
                  documentSnapshot.data
                )
              )
              if (delinasiCounter == doc.size() && yuridisCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }
            .addOnFailureListener {
              yuridisCounter++
              if (delinasiCounter == doc.size() && yuridisCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }

          Collections.getUserAreaDetailYuridisII(currentUser()?.email, snapshot.reference.id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
              yuridis2Counter++
              yuridis2Holders.add(
                DataAndReferenceHolder(
                  snapshot.reference.id,
                  documentSnapshot.data
                )
              )
              if (delinasiCounter == doc.size() && yuridis2Counter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }
            .addOnFailureListener {
              yuridis2Counter++
              if (delinasiCounter == doc.size() && yuridis2Counter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }

          Collections.getUserAreaSignature(currentUser()?.email.toString(), snapshot.reference.id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
              pemilikSignatureHolders.add(
                DataAndReferenceHolder(snapshot.reference.id, documentSnapshot.data)
              )
            }

          Collections.getUserAreaDetailBapeda(currentUser()?.email, snapshot.reference.id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
              bapendaCounter++
              bapendaHolders.add(
                DataAndReferenceHolder(
                  snapshot.reference.id,
                  documentSnapshot.data
                )
              )
              if (delinasiCounter == doc.size() && bapendaCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }
            .addOnFailureListener {
              bapendaCounter++
              if (delinasiCounter == doc.size() && bapendaCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }

          Collections.getUserAreaDetailip4t(currentUser()?.email, snapshot.reference.id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
              ip4tCounter++
              ip4tdaHolders.add(
                DataAndReferenceHolder(
                  snapshot.reference.id,
                  documentSnapshot.data
                )
              )
              if (delinasiCounter == doc.size() && ip4tCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }
            .addOnFailureListener {
              ip4tCounter++
              if (delinasiCounter == doc.size() && ip4tCounter == doc.size() && statusCounter == doc.size()) dataLoaded()
            }


          Collections.getUserAreaImages(
            currentUser()?.email, snapshot.reference.id
          )
            ?.addSnapshotListener(this) { querySnapshot, _ ->
            }


          areas.add(snapshot)
        }

        Collections.getWorkspaceYuridisDataSaksiPertama(workspace?.id, currentUser()?.email).get()
          .addOnSuccessListener { documentSnapshot ->
            documentSnapshot?.data?.let { it1 -> saksi1.putAll(it1) }
          }

        Collections.getWorkspaceYuridisDataSaksiKedua(workspace?.id, currentUser()?.email).get()
          .addOnSuccessListener { documentSnapshot ->
            documentSnapshot?.data?.let { it1 -> saksi2.putAll(it1) }
          }

        Collections.getWorkspaceGuPertama(workspace?.id, currentUser()?.email).get()
          .addOnSuccessListener { documentSnapshot ->
            documentSnapshot?.data?.let { it1 -> dataGuPertama.putAll(it1) }
          }

        Collections.getWorkspaceGuKedua(workspace?.id, currentUser()?.email).get()
          .addOnSuccessListener { documentSnapshot ->
            documentSnapshot?.data?.let { it1 ->
              dataGuKedua.putAll(it1)
            }
          }
      }
  }

//  override fun onPurchasesSuccess() {
//  }
//
//  override fun onPurchasesError() {
//  }

  override fun onSaveClick() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getExportPath(): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}