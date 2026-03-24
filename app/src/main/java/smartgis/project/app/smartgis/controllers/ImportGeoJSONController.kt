package smartgis.project.app.smartgis.controllers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import org.json.JSONArray
import org.json.JSONObject
import smartgis.project.app.smartgis.databinding.ActivityImportGeoJsonBinding
import smartgis.project.app.smartgis.utils.BASE_STORAGE_PATH
import smartgis.project.app.smartgis.utils.gone
import smartgis.project.app.smartgis.utils.show
import java.io.File

class ImportGeoJSONController(private val context: Context) {

    private var jsonObject: JSONObject? = null
    private var dialogInterface: DialogInterface? = null

    fun handle(file: File, fileName: String, handler: (JSONArray) -> Unit) {

        val binding = ActivityImportGeoJsonBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        // Set data
        binding.tvImportLocation.text = BASE_STORAGE_PATH
        binding.tvImportLocation.text = fileName

        binding.loadingContainer.show()

        val jsonObject1 = JSONObject(file.readText(Charsets.UTF_8))
        jsonObject = jsonObject1

        val features = jsonObject1.getJSONArray("features")

        binding.tvDesc.text =
            "Jumlah area yang akan diimport: ${features.length()}"

        if (features.length() > 500) {
            binding.tvNote.show()
        } else {
            binding.tvNote.gone()
        }

        binding.loadingContainer.gone()
        binding.btnImport.isEnabled = true

        // Action
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnImport.setOnClickListener {
            dialog.dismiss()
            handler(features)
        }

        dialogInterface = dialog
        dialog.show()
    }
}