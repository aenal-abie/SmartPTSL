package smartgis.project.app.smartgis.controllers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import com.google.gson.Gson
//import kotlinx.android.synthetic.main.activity_import.view.*
import org.json.JSONArray
import org.json.JSONObject
import org.zeroturnaround.zip.ZipUtil
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.utils.gone
import smartgis.project.app.smartgis.utils.show
import smartgis.project.app.smartgis.utils.shp.ShpToGeoJson.transformShpDbfToGeoJson
import java.io.File


class ImportShpController(private val context: Context) {

  private var jsonObject: JSONObject? = null
  private var dialogInterface: DialogInterface? = null
  private var active: Boolean = false

  @SuppressLint("SetTextI18n")
  fun handle(file: File, fileName: String, handler: (JSONArray) -> Unit) {
//    val dialog = AlertDialog.Builder(context)
//    dialog.setCancelable(false)
//    val importView: View =
//      LayoutInflater.from(context).inflate(R.layout.activity_import, null, false)
//    dialog.setView(importView)
//    dialog.setView(importView)
//    importView.tvImportLocation.text = fileName
//    val tmpDir = context.getDir("tmp-extract", Context.MODE_PRIVATE)
//    if (!tmpDir.exists()) tmpDir.createNewFile()
//    tmpDir.listFiles().forEach { it.delete() }
//    ZipUtil.unpack(file, tmpDir)
//    val shpFile = tmpDir.listFiles().first { it.extension == "shp" }
//    val dbfFile = tmpDir.listFiles().first { it.extension == "dbf" }
//
//    val json = Gson().toJson(transformShpDbfToGeoJson(shpFile, dbfFile))
//    jsonObject = JSONObject(json)
//    jsonObject?.apply {
//      importView.tvDesc.text =
//        "Jumlah area yang akan diimport: ${this.getJSONArray("features")?.length()}"
//      importView.btnImport.isEnabled = true
//      importView.loadingContainer.gone()
//      if (getJSONArray("features").length() > 500) importView.tvNote.show()
//      else importView.tvNote.gone()
//    }
//    importView.btnCancel.setOnClickListener { dialogInterface?.dismiss() }
//    importView.btnImport.setOnClickListener {
//      dialogInterface?.dismiss()
//      jsonObject?.apply {
//        var features = this.getJSONArray("features")
//        val size = this.getJSONArray("features").length()
//        when {
//          active -> features.put(size, "1")
//        }
//        features.apply {
//          handler(this)
//        }
//      }
//    }
//    dialogInterface = dialog.show()
  }
}