package smartgis.project.app.smartgis.databases

//import io.realm.Realm
//import io.realm.RealmResults
//import io.realm.kotlin.createObject
//import io.realm.kotlin.where
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.ALAS_HAK
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.BPHTB
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.PERORANGAN
import smartgis.project.app.smartgis.forms.yuridis_ptsl.GeneratePdfContent.Companion.PERSIL
import java.util.*

class BerkasYuridisHelper {

//  private val realm = Realm.getDefaultInstance()

//  fun saveFile(path: String, parentID: String, typeDoc: String) {
//    realm.executeTransaction {
//      val berkas = realm.createObject<BerkasYuridis>(UUID.randomUUID().toString())
//      berkas.pathImg = path
//      berkas.parentId = parentID
//      berkas.typeDoc = typeDoc
//    }
//  }

//  fun getAllByParent(parentID: String, typeDoc: String): RealmResults<BerkasYuridis> {
//    return realm.where<BerkasYuridis>().equalTo("parentId", parentID).equalTo("typeDoc", typeDoc)
//      .findAll()
//  }
//
//  fun documentIsExist(parentID: String, typeDoc: String): Boolean {
//    return realm.where<BerkasYuridis>().equalTo("parentId", parentID).equalTo("typeDoc", typeDoc)
//      .count() > 0
//  }
//
//  fun getComplateByParent(parentID: String): Boolean {
//    val typeDoc =
//      listOf(PERORANGAN, PERSIL, BPHTB, ALAS_HAK)
//    typeDoc.forEach { doc ->
//      var count: Long =
//        realm.where<BerkasYuridis>().equalTo("parentId", parentID).equalTo("typeDoc", doc)
//          .count()
//      if (count < 1)
//        return false
//    }
//    return true
//  }
//
//  fun getAll(): RealmResults<BerkasYuridis> {
//    return realm.where<BerkasYuridis>().findAll()
//  }

}