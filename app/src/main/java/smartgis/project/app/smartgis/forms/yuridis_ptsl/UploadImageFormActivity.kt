package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
//import id.zelory.compressor.Compressor
//import pl.aprilapps.easyphotopicker.DefaultCallback
//import pl.aprilapps.easyphotopicker.EasyImage
//import pub.devrel.easypermissions.EasyPermissions
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.databases.BerkasYuridisHelper
import smartgis.project.app.smartgis.databinding.ActivityUploadYuridisDocBinding
//import smartgis.project.app.smartgis.adapter.ImageArrayLocalAdapter
//import smartgis.project.app.smartgis.databinding.ActivityUploadYuridisDocBinding
//import smartgis.project.app.smartgis.databases.BerkasYuridisHelper
import smartgis.project.app.smartgis.decorators.GridItemDecoration
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.documents.Storages
import smartgis.project.app.smartgis.forms.BaseFormContainer.Companion.AREA_ID
import smartgis.project.app.smartgis.forms.BaseFormContainer.Companion.DOC_TYPE
import smartgis.project.app.smartgis.forms.delinasi.DelinasiGeneral
import smartgis.project.app.smartgis.models.ImageHolder
import smartgis.project.app.smartgis.utils.currentUser
import java.io.File
import java.util.*

class UploadImageFormActivity : LoginRequiredActivity()
//    EasyPermissions.PermissionCallbacks
{

    companion object {
        private const val TAG = "UploadImageForm"
        private const val STORAGE_PERMISSION_REQUEST = 100
        private const val REQUEST_TAKE_PHOTO = 1

        const val FORM = "FORM"
        const val WORSKPACE_NAME = "WORSKPACE_NAME"
        const val PATH = "path"
        const val TIME_STAMP = "created_at"
    }

    private lateinit var binding: ActivityUploadYuridisDocBinding

    private var collectionReference: CollectionReference? = null
    private val berkasHelper = BerkasYuridisHelper()

    private var areaId: String = ""
    private var nis: String = ""
    private var docType: String = ""
    private var form: String = ""
    private var workspaceName: String = ""
    private var data: MutableMap<String, Any>? = null

    private val dataAdapter = mutableListOf<ImageHolder>()
//    private val adapter = ImageArrayLocalAdapter(dataAdapter) {
//        startActivity(Intent(this, SeePictureDoc::class.java).apply {
//            putExtra(SeePictureDoc.IMAGE_HOLDER, dataAdapter[it])
//        })
//    }

    // ================= LIFECYCLE =================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadYuridisDocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupActions()
    }

    override fun onStart() {
        super.onStart()
        initIntentData()
        updateData()
        loadData()
        setCollectionReference()
    }

    // ================= SETUP =================

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        binding.rvImages.layoutManager = GridLayoutManager(this, 2)
//        binding.rvImages.adapter = adapter
        binding.rvImages.addItemDecoration(GridItemDecoration(10, 2))
    }

    private fun setupActions() {

        binding.fabAddNewImage.setOnClickListener {
//            requestPermission {
//                EasyImage.openChooserWithGallery(
//                    this,
//                    getString(R.string.choose_image),
//                    REQUEST_TAKE_PHOTO
//                )
//            }
        }

        binding.fabAddNewPdf.setOnClickListener {
            generatePdf()
        }
    }

    // ================= PERMISSION =================

    private fun requestPermission(onGranted: () -> Unit) {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

//        if (EasyPermissions.hasPermissions(this, *perms)) {
//            onGranted()
//        } else {
//            EasyPermissions.requestPermissions(
//                this,
//                "Butuh izin penyimpanan",
//                STORAGE_PERMISSION_REQUEST,
//                *perms
//            )
//        }
    }

//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}
//
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        Toast.makeText(this, "Permission ditolak", Toast.LENGTH_SHORT).show()
//    }

    // ================= IMAGE PICKER =================

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        EasyImage.handleActivityResult(
//            requestCode,
//            resultCode,
//            data,
//            this,
//            object : DefaultCallback() {
//
//                override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {
//                    if (imageFile == null) return
//
//                    processImage(imageFile)
//                }
//
//                override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
//                    Log.e(TAG, "Image picker error", e)
//                }
//            }
//        )
    }

    private fun processImage(imageFile: File) {
        try {
            val idFile = UUID.randomUUID().toString()
            val compressedDir = File("$filesDir/images").apply { mkdirs() }

//            val compressedFile = Compressor(this)
//                .setQuality(70)
//                .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                .setDestinationDirectoryPath(compressedDir.absolutePath)
//                .compressToFile(imageFile)

//            saveFileToStorage("$idFile.jpg", compressedFile)

        } catch (e: Exception) {
            Log.e(TAG, "Error compress image", e)
        }
    }

    // ================= FIREBASE =================

    private fun saveFileToStorage(fileName: String, file: File) {

        val ref = Storages.getImageReference(fileName)

        ref.putFile(Uri.fromFile(file))
            .addOnProgressListener {
//                binding.progressContainer.visibility = View.VISIBLE
//                val progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
//                binding.uploadProgress.progress = progress
            }
            .addOnSuccessListener { snapshot ->

                collectionReference?.add(
                    mapOf(
                        PATH to snapshot.metadata?.path,
                        TIME_STAMP to FieldValue.serverTimestamp()
                    )
                )

//                berkasHelper.saveFile(file.absolutePath, areaId, docType)
                updateData()
                hideProgress()

            }
            .addOnFailureListener {
                Log.e(TAG, "Upload error", it)
                showMessage("Upload gagal")
                hideProgress()
            }
    }

    // ================= DATA =================

    private fun updateData() {
        dataAdapter.clear()
//        berkasHelper.getAllByParent(areaId, docType).forEach {
////            dataAdapter.add(ImageHolder(it.pathImg, it.id))
//        }
//        adapter.notifyDataSetChanged()
    }

    private fun loadData() {
        val email = currentUser()?.email ?: return

        Collections.getUserAreaDetailYuridisPTSL(email, areaId, docType)
            .get()
            .addOnSuccessListener {
                data = it.data as? MutableMap<String, Any>
            }

        Collections.getUserAreaDetailDelinasi(email, areaId)
            .get()
            .addOnSuccessListener {
                nis = it.getString("${DelinasiGeneral.PREFIX}_${DelinasiGeneral.YURI_FILE_NO}") ?: ""
            }
    }

    private fun setCollectionReference() {
        val email = currentUser()?.email ?: return
        collectionReference =
            Collections.getImageAreaDetailYuridisPTSL(email, areaId, docType)
    }

    private fun initIntentData() {
        areaId = intent.getStringExtra(AREA_ID) ?: ""
        docType = intent.getStringExtra(DOC_TYPE) ?: ""
        form = intent.getStringExtra(FORM) ?: ""
        workspaceName = intent.getStringExtra(WORSKPACE_NAME) ?: ""
    }

    // ================= PDF =================

    private fun generatePdf() {
        Thread {
            val success = CreateYuridisPdf(
                this,
                areaId,
                docType,
                data,
                form,
                workspaceName,
                nis
            ).createPDF("$filesDir/${nis}_$docType.pdf")

            runOnUiThread {
                showMessage(
                    if (success) "PDF berhasil dibuat"
                    else "Gagal membuat PDF"
                )
            }
        }.start()
    }

    // ================= UTIL =================

    private fun hideProgress() {
//        binding.progressContainer.visibility = View.GONE
//        binding.uploadProgress.progress = 0
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}