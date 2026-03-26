package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.net.Uri
import android.os.Bundle
import android.util.Log
//import com.smartptsl.panutan.ui.GlideApp
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.databinding.SeePictureBinding
import smartgis.project.app.smartgis.models.ImageHolder
import java.io.File

class SeePictureDoc : LoginRequiredActivity() {

    companion object {
        const val IMAGE_HOLDER = "IMAGE_HOLDER"
        private const val TAG = "SeePictureDoc"
    }

    private lateinit var binding: SeePictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SeePictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadImage()
    }

    private fun loadImage() {
        val data = intent.getParcelableExtra<ImageHolder>(IMAGE_HOLDER)

        if (data == null) {
            Log.e(TAG, "ImageHolder is null")
            showError("Data gambar tidak ditemukan")
            return
        }

        binding.tvTitle.text = data.what ?: "-"

        val path = data.path
        if (path.isNullOrEmpty()) {
            Log.e(TAG, "Path kosong")
            showError("Path gambar kosong")
            return
        }

        val file = File(path)
        if (!file.exists()) {
            Log.e(TAG, "File tidak ditemukan: $path")
            showError("File gambar tidak ditemukan")
            return
        }

        val imageUri = Uri.fromFile(file)

//        GlideApp.with(this)
//            .load(imageUri)
//            .placeholder(R.drawable.ic_image_placeholder) // optional
//            .error(R.drawable.ic_broken_image) // optional
//            .into(binding.ivContainer)
    }

    private fun showError(message: String) {
        binding.tvTitle.text = message
//        binding.ivContainer.setImageResource(R.drawable.ic_broken_image)
    }
}