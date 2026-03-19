package smartgis.project.app.smartgis.documents

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object Collections {

  fun getUserLocation(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/location")

  fun trackUserLocation(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/traking")

  fun getUserWorkspace(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/workspaces")

  fun getUserDrawnAreas(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/areas")

  fun getUserDrawnPolyAreas(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/polyline_areas")

  fun getUserWorkspacePoints(email: String?, workspaceId: String?) =
    FirebaseFirestore.getInstance().collection("smartgis/$email/workspace_points/$workspaceId/data")

  private fun getUserAreaDetails(email: String?) =
    FirebaseFirestore.getInstance().collection("smartgis/$email/area_details")

  fun getUserAreaRtkData(email: String?, areaId: String): CollectionReference =
    getUserAreaDetails(email).document(areaId).collection("rtk_status")

  fun getUserAreaDetailDelinasi(email: String?, areaId: String?): DocumentReference =
    getUserAreaDetails(email).document("$areaId/data/delinasi")

  fun getUserAreaPengtan(email: String?, areaId: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/area_details/$areaId/data")

  fun getUserAreaDetailPengtan(email: String?, areaId: String?): DocumentReference =
    getUserAreaDetails(email).document("$areaId/data/pengtan")

  fun getUserAreaDetailYuridis(email: String?, areaId: String?): DocumentReference =
    getUserAreaDetails(email).document("$areaId/data/yuridis")

  fun getUserAreaDetailYuridisPTSL(
    email: String?,
    areaId: String?,
    type: String
  ): DocumentReference =
    getUserAreaDetails(email).document("$areaId/yuridis_ptsl/yuridis_ptsl_$type")

  fun getImageAreaDetailYuridisPTSL(
    email: String?,
    areaId: String?,
    type: String
  ) = getUserAreaDetails(email).document("$areaId")
    .collection("yuridis_ptsl/yuridis_ptsl_$type/images")

  fun getUserAreaDetailYuridisPTSLCollections(email: String?, areaId: String): CollectionReference =
    getUserAreaDetails(email).document(areaId).collection("yuridis_ptsl")

  fun getUserAreaImages(email: String?, areaId: String) =
    getUserAreaDetails(email).document(areaId).collection("images")

  fun getUserAreaSignature(email: String?, areaId: String) =
    getUserAreaDetails(email).document("$areaId/images/signature")

  fun getBuktiFoto() =
    FirebaseFirestore.getInstance().collection("bukti_foto")

  fun getBuktiPenguasaan() =
    FirebaseFirestore.getInstance().collection("bukti_penguasaan")

  fun getAnnouncements() =
    FirebaseFirestore.getInstance().collection("announcements")

  fun getRtkData() = FirebaseFirestore.getInstance().collection("rtk_data")

  private fun getWorkspaceData(workspaceId: String?, email: String?) =
    FirebaseFirestore.getInstance().collection("workspace_data/$email/$workspaceId/")

  fun getWorkspaceDoc(workspaceId: String, email: String?) =
    FirebaseFirestore.getInstance().collection("smartgis/$email/workspaces").document(workspaceId)

  fun getWorkspaceYuridisDataSaksiPertama(workspaceId: String?, email: String?) =
    getWorkspaceData(workspaceId, email).document("saksi_pertama")

  fun getWorkspaceYuridisDataSaksiKedua(workspaceId: String?, email: String?) =
    getWorkspaceData(workspaceId, email).document("saksi_kedua")

  fun getWorkspaceGuPertama(workspaceId: String?, email: String?) =
    getWorkspaceData(workspaceId, email).document("gu_pertama")

  fun getWorkspaceGuKedua(workspaceId: String?, email: String?) =
    getWorkspaceData(workspaceId, email).document("gu_kedua")

  fun workspaceCountTracker() =
    FirebaseFirestore.getInstance().collection("workspace_count_tracker")

  fun getUserPurchasedItem(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/purchased_items")

  fun getUserAreaDetailYuridisII(email: String?, areaId: String?): DocumentReference =
    getUserAreaDetails(email).document("$areaId/data/yuridis2")

  fun getUserPolylineAreaDetail(email: String?, areaId: String?): DocumentReference =
    getUserAreaDetails(email).document("$areaId/data/line")

  fun getUserAreaDetailBapeda(email: String?, areaId: String?): DocumentReference =
    getUserAreaDetails(email).document("$areaId/data/bapeda_mataram")

  fun getUserAreaDetailip4t(email: String?, areaId: String?): DocumentReference =
    getUserAreaDetails(email).document("$areaId/data/ip4t")

  fun getUserWms(email: String?): DocumentReference =
    getUserAreaDetails(email).document("wms")


  //PANUTAN

  fun getPermohonanSurveyor(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/coba_permohonan")

  fun getPanutan(): Query =
    FirebaseFirestore.getInstance().collection("panutan")

  fun getPanutanPelengkap(panutan_id: String?) =
    FirebaseFirestore.getInstance().collection("panutan_pelengkap")

  fun getPanutanPelengkapReview(panutan_id: String?) =
    getPanutanPelengkap(panutan_id).document("$panutan_id")
      .collection("review")

  fun setToken(email: String) =
    FirebaseFirestore.getInstance().collection("fcm_token").document(email)

  fun getProfileReference(email: String?): DocumentReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/profile").document("photo")

  fun getUserWorkspacePemohon(email: String?): CollectionReference =
    FirebaseFirestore.getInstance().collection("smartgis/$email/workspaces_pemohon")

}