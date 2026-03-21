package smartgis.project.app.smartgis.controllers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
//import kotlinx.android.synthetic.main.activity_import.view.*
import org.json.JSONArray
import org.json.JSONObject
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.utils.BASE_STORAGE_PATH
import smartgis.project.app.smartgis.utils.gone
import smartgis.project.app.smartgis.utils.show
import java.io.File


class ImportGeoJSONController(private val context: Context) {

  private var jsonObject: JSONObject? = null
  private var dialogInterface: DialogInterface? = null

  fun handle(file: File, fileName: String, handler: (JSONArray) -> Unit) {
//    val dialog = AlertDialog.Builder(context)
//    dialog.setCancelable(false)
//    val importView: View =
//      LayoutInflater.from(context).inflate(R.layout.activity_import_geo_json, null, false)
//    dialog.setView(importView)
//    dialog.setView(importView)
//    importView.tvImportLocation.text = BASE_STORAGE_PATH
//    importView.tvImportLocation.text = fileName
//    importView.loadingContainer.show()
//    importView.btnImport.isEnabled = true
//    importView.loadingContainer.gone()
//    file.bufferedReader().readLines()
//    val jsonObject1 = JSONObject(file.readText(Charsets.UTF_8))
//    jsonObject = jsonObject1
//    importView.tvDesc.text =
//      "Jumlah area yang akan diimport: ${jsonObject1.getJSONArray("features")?.length()}"
//    if (jsonObject1.getJSONArray("features").length() > 500) importView.tvNote.show()
//    else importView.tvNote.gone()
//    importView.btnCancel.setOnClickListener { dialogInterface?.dismiss() }
//    importView.btnImport.setOnClickListener {
//      dialogInterface?.dismiss()
//      jsonObject?.apply {
//        val features = this.getJSONArray("features")
//        features.apply {
//          handler(this)
//        }
//      }
//    }
//    dialogInterface = dialog.show()
  }

}