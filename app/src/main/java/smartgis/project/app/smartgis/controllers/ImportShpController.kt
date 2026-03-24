package smartgis.project.app.smartgis.controllers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.zeroturnaround.zip.ZipUtil
import smartgis.project.app.smartgis.databinding.ActivityImportBinding
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

        val binding = ActivityImportBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.tvImportLocation.text = fileName
        binding.loadingContainer.show()
        binding.btnImport.isEnabled = false

        // 🔽 Extract ZIP
        val tmpDir = context.getDir("tmp-extract", Context.MODE_PRIVATE)

        if (!tmpDir.exists()) tmpDir.mkdirs()
        tmpDir.listFiles()?.forEach { it.delete() }

        ZipUtil.unpack(file, tmpDir)

        val shpFile = tmpDir.listFiles()?.firstOrNull { it.extension == "shp" }
        val dbfFile = tmpDir.listFiles()?.firstOrNull { it.extension == "dbf" }

        if (shpFile == null || dbfFile == null) {
            binding.loadingContainer.gone()
            binding.tvDesc.text = "File shp/dbf tidak ditemukan"
            dialog.show()
            return
        }

        try {
            val json = Gson().toJson(transformShpDbfToGeoJson(shpFile, dbfFile))
            jsonObject = JSONObject(json)

            val features = jsonObject!!.getJSONArray("features")

            binding.tvDesc.text =
                "Jumlah area yang akan diimport: ${features.length()}"

            if (features.length() > 500) {
                binding.tvNote.show()
            } else {
                binding.tvNote.gone()
            }

            binding.btnImport.isEnabled = true

        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvDesc.text = "Gagal membaca file"
        }

        binding.loadingContainer.gone()

        // Actions
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnImport.setOnClickListener {
            dialog.dismiss()

            jsonObject?.let {
                val features = it.getJSONArray("features")
                val size = features.length()

                if (active) {
                    features.put(size, "1")
                }

                handler(features)
            }
        }

        dialogInterface = dialog
        dialog.show()
    }
}