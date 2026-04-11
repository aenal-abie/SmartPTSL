package smartgis.project.app.smartgis.export

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import com.google.gson.Gson
//import kotlinx.android.synthetic.main.activity_export.*
//import org.apache.poi.util.IOUtils
import org.gdal.ogr.ogr
//import org.jetbrains.anko.design.longSnackbar
//import org.jetbrains.anko.design.snackbar
//import org.jetbrains.anko.toast
import org.zeroturnaround.zip.FileSource
import org.zeroturnaround.zip.ZipUtil
import org.zeroturnaround.zip.commons.IOUtils
import smartgis.project.app.smartgis.MainActivity.Companion.TTD_PEMILIK_URL
import smartgis.project.app.smartgis.MainActivity.Companion.TTD_SAKSI1_URL
import smartgis.project.app.smartgis.MainActivity.Companion.TTD_SAKSI2_URL
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.forms.delinasi.*
import smartgis.project.app.smartgis.forms.workspaceforms.saksi.SaksiFormContainer
import smartgis.project.app.smartgis.forms.yuridis.BadanHukum
import smartgis.project.app.smartgis.forms.yuridis.Pernyataan
import smartgis.project.app.smartgis.forms.yuridis.YuridisGeneral
import smartgis.project.app.smartgis.forms.yuridis2.ElemenKadaster
//import smartgis.project.app.smartgis.forms.workspaceforms.saksi.SaksiFormContainer
//import smartgis.project.app.smartgis.forms.yuridis.BadanHukum
//import smartgis.project.app.smartgis.forms.yuridis.Pernyataan
//import smartgis.project.app.smartgis.forms.yuridis.YuridisGeneral
//import smartgis.project.app.smartgis.forms.yuridis2.ElemenKadaster
import smartgis.project.app.smartgis.models.Area
import smartgis.project.app.smartgis.models.RtkStatusHolder
import smartgis.project.app.smartgis.utils.*
import java.io.*
import kotlin.collections.forEach


class ExportAreaDetailToShp : BaseExportableAreaDetail() {

  private val nonEmptyAreaWithstatus = mutableListOf<RtkStatusHolder>()

  override fun onSaveClick() {
    exportBinding.loadingContainer.show()
    val features = mutableListOf<Any>()
    val validPolygons = areas.filter { it.toObject(Area::class.java)?.points?.size!! > 2 }
    validPolygons.forEach { snapshot ->
      val properties: MutableMap<String, Any> = mutableMapOf()

      val delinasiDataRef = delinasiHolders.find { holder -> holder.id == snapshot.id }?.values
      val nub =
        delinasiDataRef?.get(DelinasiGeneral.YURI_FILE_NO.prefix(DelinasiGeneral.PREFIX)).toString()
      val kluster =
        delinasiDataRef?.get(DelinasiGeneral.CLUSTER.prefix(DelinasiGeneral.PREFIX)).toString()
      val noHak =
        delinasiDataRef?.get(DelinasiGeneral.HAK.prefix(DelinasiGeneral.PREFIX)).toString()
      val jenisHak =
        delinasiDataRef?.get(DelinasiGeneral.JENIS_HAK.prefix(DelinasiGeneral.PREFIX)).toString()
      val nama =
        delinasiDataRef?.get(SubjectIdentity.NAMA.prefix(SubjectIdentity.PREFIX)).toString()
      val nik = delinasiDataRef?.get(SubjectIdentity.NIK.prefix(SubjectIdentity.PREFIX)).toString()
      val tmpLahir =
        delinasiDataRef?.get(SubjectIdentity.TEMPAT_LAHIR.prefix(SubjectIdentity.PREFIX)).toString()
      val tglLahir =
        delinasiDataRef?.get(SubjectIdentity.TANGGAL_LAHIR.prefix(SubjectIdentity.PREFIX))
          .toString()
      val alamat =
        delinasiDataRef?.get(SubjectIdentity.ALAMAT.prefix(SubjectIdentity.PREFIX)).toString()
      val pekerjaan =
        delinasiDataRef?.get(SubjectIdentity.PEKERJAAN.prefix(SubjectIdentity.PREFIX)).toString()
      val kelurahan =
        delinasiDataRef?.get(AreaPosition.KELURAHAN.prefix(AreaPosition.PREFIX)).toString()
      val dusun = delinasiDataRef?.get(AreaPosition.DUSUN.prefix(AreaPosition.PREFIX)).toString()
      val rt = delinasiDataRef?.get(AreaPosition.RT.prefix(AreaPosition.PREFIX)).toString()
      val rw = delinasiDataRef?.get(AreaPosition.RW.prefix(AreaPosition.PREFIX)).toString()
      val blok =
        delinasiDataRef?.get(AreaPosition.ET_PBBB_NO.prefix(AreaPosition.PREFIX)).toString()
      val statusTanah =
        delinasiDataRef?.get(AreaPosition.AREA_STATUS.prefix(AreaPosition.PREFIX)).toString()
      val statusPenggunaan =
        delinasiDataRef?.get(AreaPosition.STATUS_PENGGUNAAN.prefix(AreaPosition.PREFIX)).toString()
      val buktiPS =
        delinasiDataRef?.get(AreaPosition.BUKTI_PENGUASAAN.prefix(AreaPosition.PREFIX)).toString()
      val shatNo =
        delinasiDataRef?.get(AreaPosition.SHAT_NO.prefix(AreaPosition.PREFIX)).toString()
      val luas =
        delinasiDataRef?.get(AreaPosition.LUAS_TANAH.prefix(AreaPosition.PREFIX)).toString()
      val hak = delinasiDataRef?.get(AreaPosition.KETERANGAN.prefix(AreaPosition.PREFIX)).toString()

      val nub1 = delinasiDataRef?.get(ElemenKadaster.NUB.prefix(ElemenKadaster.PREFIX)).toString()
      val nama1 = delinasiDataRef?.get(ElemenKadaster.NAMA.prefix(ElemenKadaster.PREFIX)).toString()
      val penunjuk_batas =
        delinasiDataRef?.get(ElemenKadaster.PENUNJUK_BATAS.prefix(ElemenKadaster.PREFIX)).toString()
      val tanda_batas_utara =
        delinasiDataRef?.get(ElemenKadaster.TANDA_UTARA.prefix(ElemenKadaster.PREFIX)).toString()
      val tanda_batas_timur =
        delinasiDataRef?.get(ElemenKadaster.TANDA_TIMUR.prefix(ElemenKadaster.PREFIX)).toString()
      val tanda_batas_barat =
        delinasiDataRef?.get(ElemenKadaster.TANDA_BARAT.prefix(ElemenKadaster.PREFIX)).toString()
      val tanda_batas_selatan =
        delinasiDataRef?.get(ElemenKadaster.TANDA_SELATAN.prefix(ElemenKadaster.PREFIX)).toString()
      val pers_utara =
        delinasiDataRef?.get(ElemenKadaster.PERS_UTARA.prefix(ElemenKadaster.PREFIX)).toString()
      val pers_barat =
        delinasiDataRef?.get(ElemenKadaster.PERS_BARAT.prefix(ElemenKadaster.PREFIX)).toString()
      val pers_timur =
        delinasiDataRef?.get(ElemenKadaster.PERS_TIMUR.prefix(ElemenKadaster.PREFIX)).toString()
      val pers_selatan =
        delinasiDataRef?.get(ElemenKadaster.PERS_SELATAN.prefix(ElemenKadaster.PREFIX)).toString()
      val petugas_batas =
        delinasiDataRef?.get(ElemenKadaster.PETUGAS.prefix(ElemenKadaster.PREFIX)).toString()
      val nama_petugas =
        delinasiDataRef?.get(ElemenKadaster.NAMA_PETUGAS.prefix(ElemenKadaster.PREFIX)).toString()
      val pengukuran =
        delinasiDataRef?.get(ElemenKadaster.PENGUKURAN.prefix(ElemenKadaster.PREFIX)).toString()
      val peta_dasar =
        delinasiDataRef?.get(ElemenKadaster.PETA_DASAR.prefix(ElemenKadaster.PREFIX)).toString()

      /**
       * Data Fisik
       */
      properties["polygon_id"] = snapshot.reference.id
      properties["NUB"] = nub
      properties["NAMA"] = nama
      properties["HAK"] = noHak
      properties["jnsHak"] = jenisHak
      properties["nik"] = nik
      properties["klaster"] = kluster
      properties["tmp_lahir"] = tmpLahir
      properties["tgl_lahir"] = tglLahir
      properties["alamat"] = alamat
      properties["pekerjaan"] = pekerjaan
      properties["kelurahan"] = kelurahan
      properties["dusun"] = dusun
      properties["rt"] = rt
      properties["rw"] = rw
      properties["blok_pbb"] = blok
      properties["stts_tanah"] = statusTanah
      properties["stts_guna"] = statusPenggunaan
      properties["bkti_kuasa"] = buktiPS
      properties["shatNo"] = shatNo
      properties["luas"] = luas
      properties["hak"] = hak
      properties["ttd_pmlk"] =
          pemilikSignatureHolders.find { it.id == snapshot.id }?.values?.get(TTD_PEMILIK_URL)
          .toString()
      /**
       * Data Yuridis
       */
      val yuridisDataRef = yuridisHolders.find { holder -> holder.id == snapshot.id }?.values
      val agama = yuridisDataRef?.get(YuridisGeneral.AGAMA.prefix(YuridisGeneral.PREFIX)).toString()
      val jalan =
        yuridisDataRef?.get(YuridisGeneral.JALAN_BLOK.prefix(YuridisGeneral.PREFIX)).toString()
      val njop = yuridisDataRef?.get(YuridisGeneral.NJOP.prefix(YuridisGeneral.PREFIX)).toString()
      val kuasaiSejak =
        yuridisDataRef?.get(YuridisGeneral.DIKUASAI_SEJAK.prefix(YuridisGeneral.PREFIX)).toString()
      val batasTimur =
        yuridisDataRef?.get(YuridisGeneral.TIMUR.prefix(YuridisGeneral.PREFIX)).toString()
      val batasBarat =
        yuridisDataRef?.get(YuridisGeneral.BARAT.prefix(YuridisGeneral.PREFIX)).toString()
      val batasUtara =
        yuridisDataRef?.get(YuridisGeneral.UTARA.prefix(YuridisGeneral.PREFIX)).toString()
      val batasSelatan =
        yuridisDataRef?.get(YuridisGeneral.SELATAN.prefix(YuridisGeneral.PREFIX)).toString()
      val jenis = yuridisDataRef?.get(YuridisGeneral.JENIS.prefix(YuridisGeneral.PREFIX)).toString()
      val badanHukum =
        yuridisDataRef?.get(BadanHukum.BADAN_HUKUM.prefix(BadanHukum.PREFIX)).toString()
      val noAkta =
        yuridisDataRef?.get(BadanHukum.NO_AKTA_PENDIRIAN.prefix(BadanHukum.PREFIX)).toString()
      val tglAkta =
        yuridisDataRef?.get(BadanHukum.TANGGAL_AKTA_PENDIRIAN.prefix(BadanHukum.PREFIX)).toString()
      val peryataan =
        yuridisDataRef?.get(Pernyataan.PERNYATAAN.prefix(Pernyataan.PREFIX)).toString()

      /**
       * Data Yuridis 2
       */
      val no_shm = delinasiDataRef?.get(MoreSubject.NOMOR_SHM.prefix(MoreSubject.PREFIX)).toString()
      val status_perkawinan =
        delinasiDataRef?.get(MoreSubject.STATUS_PERKAWINAN.prefix(MoreSubject.PREFIX)).toString()
      val jenis_kelamin =
        delinasiDataRef?.get(MoreSubject.JENIS_KELAMIN.prefix(MoreSubject.PREFIX)).toString()
      val nama_ibu_kandung =
        delinasiDataRef?.get(MoreSubject.NAMA_IBU_KANDUNG.prefix(MoreSubject.PREFIX)).toString()
      val no_hp =
        delinasiDataRef?.get(MoreSubject.NOMOR_HANDPHONE.prefix(MoreSubject.PREFIX)).toString()
      val nama_wajib_pajak =
        delinasiDataRef?.get(MoreSubject.NAMA_WAJIB_PAJAK.prefix(MoreSubject.PREFIX)).toString()
      val no_sppt =
        delinasiDataRef?.get(MoreSubject.NOMOR_SPPT.prefix(MoreSubject.PREFIX)).toString()
      val luas_sppt =
        delinasiDataRef?.get(MoreSubject.LUAS_SPPT.prefix(MoreSubject.PREFIX)).toString()
      val luas_mohon =
        delinasiDataRef?.get(MoreSubject.LUAS_PERMOHONAN.prefix(MoreSubject.PREFIX)).toString()
      val njop_per_m2 =
        delinasiDataRef?.get(MoreSubject.NJOP_PER_M2.prefix(MoreSubject.PREFIX)).toString()

      val nama_letter_c =
        delinasiDataRef?.get(LetterC.NAMA_LETTER_C.prefix(LetterC.PREFIX)).toString()
      val nomor_letter_c =
        delinasiDataRef?.get(LetterC.NOMOR_LETTER_C.prefix(LetterC.PREFIX)).toString()
      val kelas_letter_c =
        delinasiDataRef?.get(LetterC.KELAS_LETTER_C.prefix(LetterC.PREFIX)).toString()
      val luas_letter_c =
        delinasiDataRef?.get(LetterC.LUAS_LETTER_C.prefix(LetterC.PREFIX)).toString()
      val nomor_persil =
        delinasiDataRef?.get(LetterC.NOMOR_PERSIL.prefix(LetterC.PREFIX)).toString()

      val saksi1Nama = saksi1[SaksiFormContainer.NAMA.prefix(
        SaksiFormContainer.SAKSI_PERTAMA
      )].toString()
      val saksi1NIK = saksi1[SaksiFormContainer.NIK.prefix(
        SaksiFormContainer.SAKSI_PERTAMA
      )].toString()
      val saksi1Agama = saksi1[SaksiFormContainer.AGAMA.prefix(
        SaksiFormContainer.SAKSI_PERTAMA
      )].toString()
      val saksi1TanggalLahir = saksi1[SaksiFormContainer.USIA.prefix(
        SaksiFormContainer.SAKSI_PERTAMA
      )].toString()
      val saksi1Pekerjaan = saksi1[SaksiFormContainer.PEKERJAAN.prefix(
        SaksiFormContainer.SAKSI_PERTAMA
      )].toString()
      val saksi1Alamat = saksi1[SaksiFormContainer.ALAMAT.prefix(
        SaksiFormContainer.SAKSI_PERTAMA
      )].toString()

      val saksi2Nama = saksi2[SaksiFormContainer.NAMA.prefix(
        SaksiFormContainer.SAKSI_KEDUA
      )].toString()
      val saksi2NIK = saksi2[SaksiFormContainer.NIK.prefix(
        SaksiFormContainer.SAKSI_KEDUA
      )].toString()
      val saksi2Agama = saksi2[SaksiFormContainer.AGAMA.prefix(
        SaksiFormContainer.SAKSI_KEDUA
      )].toString()
      val saksi2TanggalLahir = saksi2[SaksiFormContainer.USIA.prefix(
        SaksiFormContainer.SAKSI_KEDUA
      )].toString()
      val saksi2Pekerjaan = saksi2[SaksiFormContainer.PEKERJAAN.prefix(
        SaksiFormContainer.SAKSI_KEDUA
      )].toString()
      val saksi2Alamat = saksi2[SaksiFormContainer.ALAMAT.prefix(
        SaksiFormContainer.SAKSI_KEDUA
      )].toString()

      properties["agama"] = agama
      properties["jalan"] = jalan
      properties["njop"] = njop
      properties["kuasai_sjk"] = kuasaiSejak
      properties["bts_timur"] = batasTimur
      properties["bts_barat"] = batasBarat
      properties["bt_selatan"] = batasSelatan
      properties["bts_utara"] = batasUtara
      properties["jenis"] = jenis
      properties["bdn_hukum"] = badanHukum
      properties["no_akta"] = noAkta
      properties["tgl_akta"] = tglAkta
      properties["peryataan"] = peryataan
      properties["sks1_nama"] = saksi1Nama
      properties["sks1_nik"] = saksi1NIK
      properties["sks1_agama"] = saksi1Agama
      properties["sks1_tgl"] = saksi1TanggalLahir
      properties["sks1_kerja"] = saksi1Pekerjaan
      properties["sks1_almt"] = saksi1Alamat
      properties["ttd_sks1"] = saksi1[TTD_SAKSI1_URL].toString()
      properties["ttd_sks2"] = saksi2[TTD_SAKSI2_URL].toString()
      properties["sks2_nama"] = saksi2Nama
      properties["sks2_nik"] = saksi2NIK
      properties["sks2_agama"] = saksi2Agama
      properties["sks2_tgl"] = saksi2TanggalLahir
      properties["sks2_kerja"] = saksi2Pekerjaan
      properties["sks2_almt"] = saksi2Alamat


      /**
       * Yuridis 2
       */
      properties["nomor_shm"] = no_shm
      properties["sts_kawinan"] = status_perkawinan
      properties["jk"] = jenis_kelamin
      properties["nama_ibu"] = nama_ibu_kandung
      properties["nomor_hp"] = no_hp
      properties["nama_wp"] = nama_wajib_pajak
      properties["no_sppt"] = no_sppt
      properties["luas_sppt"] = luas_sppt
      properties["luas_permohonan"] = luas_mohon
      properties["njop_per_m2"] = njop_per_m2
      properties["nama_letter_c"] = nama_letter_c
      properties["nomor_letter_c"] = nomor_letter_c
      properties["kelas_letter_c"] = kelas_letter_c
      properties["luas_letter_c"] = luas_letter_c
      properties["nomor_persil"] = nomor_persil

      properties["nub1"] = nub1
      properties["nama1"] = nama1
      properties["penunjuk_batas"] = penunjuk_batas
      properties["tanda_utara"] = tanda_batas_utara
      properties["tanda_barat"] = tanda_batas_barat
      properties["tanda_timur"] = tanda_batas_timur
      properties["tanda_selatan"] = tanda_batas_selatan
      properties["persetujuan_utara"] = pers_utara
      properties["persetujuan_timur"] = pers_timur
      properties["persetujuan_barat"] = pers_barat
      properties["persetujuan_selatan"] = pers_selatan
      properties["petugas"] = petugas_batas
      properties["nama_petugas"] = nama_petugas
      properties["pengukuran"] = pengukuran
      properties["peta_dasar"] = peta_dasar

      /**
       * DATA gu
       */
      properties.putAll(dataGuPertama)
      properties.putAll(dataGuKedua)


      properties["stroke"] = "#FFFF00"
      properties["fill"] = "#FFFF00"
      snapshot.data?.get("syc")?.apply {
        if (this as Boolean) properties["fill"] = "#FF0000"
      }

      val coordinates = listOf(snapshot.toObject(Area::class.java)?.closedPoints()?.map { latLng ->
        listOf(latLng.longitude, latLng.latitude)
      })

      features.add(
        mapOf(
          "geometry" to mapOf(
            "coordinates" to coordinates,
            "type" to "Polygon"
          ),
          "properties" to properties,
          "type" to "Feature"
        )
      )
    }


    val data = mapOf("type" to "FeatureCollection", "features" to features)
    val json = Gson().toJson(data)

    if (jsonToSHP(json) == 0) {
//      exportBinding.container.longSnackbar("Area telah diexport", "Salin") {
//        copyText(getExportPath())
////        toast("${getExportPath()} telah diexport ke file SHP")
//      }
        exportBinding.btnExport.disable()
    } else {
//        exportBinding.container.snackbar("Area gagal diexport")
    }
      exportBinding.loadingContainer.gone()
  }

  override fun getExportPath(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      "${BASE_DOWNLOAD_EXPORT_DIR}/" +
          workspace?.name?.replace(" ", "_") + "_polygon.zip"
    } else {
      "$BASE_EXPORT_DIR/" + workspace?.name?.replace(" ", "_") + "_polygon.zip"
    }


  companion object {
    @SuppressLint("SdCardPath")
    val BASE_EXPORT_DIR = "$BASE_EXPORT_PATH/SHP/POLYGON"
    val BASE_DOWNLOAD_EXPORT_DIR = "$BASE_DOWNLOAD_STORAGE_PATH/SHP/POLYGON"
  }

  private fun jsonToSHP(json: String): Int {
    nonEmptyAreaWithstatus.clear()
    nonEmptyAreaWithstatus.addAll(dataHolders.filter { it.statusses.isNotEmpty() }.toMutableList())
    var workspaceName = workspace?.name?.replace(" ", "_")
    val tempFile = createTempDir(workspaceName.toString(), null, null)
    val tempDir = tempFile.path.toString()
    val tempJsonFile = "${workspaceName}.json"
    File("${filesDir}/${tempJsonFile}").delete()
    val f = OutputStreamWriter(openFileOutput(tempJsonFile, Context.MODE_PRIVATE))
    f.write(json)
    f.close()
    val jsonFile = getFileStreamPath(tempJsonFile)
    return if (jsonFile.exists()) {  // check if file exist
      if (convertToSHP(jsonFile.toString(), tempDir, workspaceName) == 0) {
        jsonFile.delete()
        0
      } else {
        jsonFile.delete()
        -2
      }
    } else {
      -1
    }
  }

  private fun convertToSHP(fInput: String, fTempDir: String, fOutputFilename: String?): Int {
    ogr.RegisterAll()
    val ds = ogr.Open(fInput, 0)
    if (ds == null) {
      return -1
    } else {
      val dv = ogr.GetDriverByName("ESRI Shapefile") ?: return -1
      val newds = dv.CopyDataSource(ds, fTempDir)
      val wgs1984 = getString(R.string.wgs_84)
      ds.delete()
      newds.SyncToDisk()
      newds.delete()
      val fileProject = "wgs_1984.prj"
      File("${fTempDir}/${fileProject}").writeText(wgs1984)
      val files: ArrayList<FileSource> = ArrayList()
      files.apply {
        this.add(FileSource("smartptsl_polygon.shp", File("${fTempDir}/${fOutputFilename}.shp")))
        this.add(FileSource("smartptsl_polygon.shx", File("${fTempDir}/${fOutputFilename}.shx")))
        this.add(FileSource("smartptsl_polygon.dbf", File("${fTempDir}/${fOutputFilename}.dbf")))
        this.add(FileSource("smartptsl_polygon.prj", File("${fTempDir}/${fileProject}")))
      }
      val array = files.toTypedArray()
      ZipUtil.pack(array, openFileOutput("tmp", Context.MODE_PRIVATE))
      val createdFile = getFileStreamPath("tmp")
      saveFileSHP(createdFile)
      File(fTempDir).deleteRecursively()
    }
    return 0
  }

  private fun saveFileSHP(createdFile: File) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      saveStreamToStorage(createdFile.inputStream(), File(getExportPath()).name)
    } else {
      createdFile.copyTo(File(getExportPath()), true)
    }
    createdFile.delete()
  }


  @RequiresApi(Build.VERSION_CODES.Q)
  private fun saveStreamToStorage(
    inputStream: FileInputStream,
    fileName: String
  ): Boolean = try {
    contentResolver.apply {
      val contentValues = contentValuesOf(
        Pair(
          MediaStore.MediaColumns.MIME_TYPE,
          "application/zip"
        ),
        Pair(
          MediaStore.MediaColumns.RELATIVE_PATH, BASE_DOWNLOAD_EXPORT_DIR
        ),
        Pair(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
      )
      val uri: Uri = insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        ?: throw IOException("Failed to create new MediaStore record.")
      val outputStream: OutputStream =
        openOutputStream(uri) ?: throw IOException("Failed to get output stream.")
      IOUtils.copy(inputStream, outputStream)
      outputStream.close()
    }

    true
  } catch (e: Exception) {
    false
  }
}