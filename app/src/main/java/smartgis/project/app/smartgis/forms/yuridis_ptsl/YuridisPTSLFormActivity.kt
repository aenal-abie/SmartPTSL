package smartgis.project.app.smartgis.forms.yuridis_ptsl

import com.google.firebase.firestore.DocumentReference
import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.adapter.FormFragmentHolder
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.forms.BaseFormContainer
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.AKTA_JUAL_BELI
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.AKTA_WAKAF
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.ALAS_HAK
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.BPHTB
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.PBB
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.PERORANGAN
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.PPH
import smartgis.project.app.smartgis.utils.currentUser

class YuridisPTSLFormActivity : BaseFormContainer() {

  private var jenis = PERORANGAN


  override fun getFireBaseDocReference(areaId: String): DocumentReference =
    Collections.getUserAreaDetailYuridisPTSL(currentUser()?.email, areaId, jenis)

  override fun init(onInit: () -> Unit) {
    checkedKey = false
    val yuridisAlasHak = YuridisAlasHak()
    val yuridisBPHTB = YuridisBPHTB()
    val yuridisPPH = YuridisPPH()
    val yuridisPBB = YuridisPBB()
    val yuridisJualbeli = YuridisJualbeli()
    val yuridisWakaf = YuridisWakaf()
    val yuridisNIK = YuridisNIK()
    yuridisAlasHak.arguments = bundle
    yuridisBPHTB.arguments = bundle
    yuridisPPH.arguments = bundle
    yuridisPBB.arguments = bundle
    yuridisJualbeli.arguments = bundle
    yuridisWakaf.arguments = bundle
    yuridisNIK.arguments = bundle
    when {
      bundle.getInt(MainActivity.AREA, 0) == 0 -> {
        jenis = PERORANGAN
        forms = listOf(
          FormFragmentHolder("Perorangan", yuridisNIK)
        )
      }
      bundle.getInt(MainActivity.AREA, 0) == 1 -> {
        finish()
      }
      bundle.getInt(MainActivity.AREA, 0) == 2 -> {
        jenis = ALAS_HAK
        forms = listOf(
          FormFragmentHolder("Bukti ALas Hak", yuridisAlasHak)
        )
      }
      bundle.getInt(MainActivity.AREA, 0) == 3 -> {
        jenis = BPHTB
        forms = listOf(
          FormFragmentHolder("BPHTB", yuridisBPHTB)
        )
      }
      bundle.getInt(MainActivity.AREA, 0) == 4 -> {
        jenis = PPH
        forms = listOf(
          FormFragmentHolder("Surat Setoran Pajak/PPH", yuridisPPH)
        )
      }
      bundle.getInt(MainActivity.AREA, 0) == 5 -> {
        jenis = PBB
        forms = listOf(
          FormFragmentHolder("Pajak Bumi dan Bangunan", yuridisPBB)
        )
      }
      bundle.getInt(MainActivity.AREA, 0) == 6 -> {
        jenis = AKTA_JUAL_BELI
        forms = listOf(
          FormFragmentHolder("Akta Jual Beli", yuridisJualbeli)
        )
      }
      bundle.getInt(MainActivity.AREA, 0) == 7 -> {
        jenis = AKTA_WAKAF
        forms = listOf(
          FormFragmentHolder("Akta Ikrar Wakaf", yuridisWakaf)
        )
      }
    }
    onInit()
  }
}