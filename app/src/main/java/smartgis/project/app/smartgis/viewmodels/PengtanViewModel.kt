package smartgis.project.app.smartgis.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
//import smartgis.project.app.smartgis.forms.pengtan.model.BuildingData
//import smartgis.project.app.smartgis.forms.pengtan.model.OtherData
//import smartgis.project.app.smartgis.forms.pengtan.model.PlantData
import smartgis.project.app.smartgis.models.response.PengtanResponse
import smartgis.project.app.smartgis.repository.base.IFeatureRepository
import smartgis.project.app.smartgis.state.ResponseState
import javax.inject.Inject

@HiltViewModel
class PengtanViewModel @Inject constructor(private val pengtanRepository: IFeatureRepository) :
  ViewModel() {
  val pemilikNama = MutableLiveData<String>()
  val pemilikTempatLahir = MutableLiveData<String>()
  val pemilikTglLahir = MutableLiveData<String>()
  val pemilikAlamat = MutableLiveData<String>()
  val pemilikPekerjaan = MutableLiveData<String>()
  val pemilikNIK = MutableLiveData<String>()

  val sebagaiPenguasa = MutableLiveData<Boolean>()

  val penguasanNama = MutableLiveData<String>()
  val penguasanTempatLahir = MutableLiveData<String>()
  val penguasanTglLahir = MutableLiveData<String>()
  val penguasanAlamat = MutableLiveData<String>()
  val penguasanPekerjaan = MutableLiveData<String>()
  val penguasanNIK = MutableLiveData<String>()

  val tanahNUB = MutableLiveData<String>()
  val tanahNIB = MutableLiveData<String>()
  val tanahLuas = MutableLiveData<String>()
  val tanahRT = MutableLiveData<String>()
  val tanahRW = MutableLiveData<String>()
  val tanahStatus = MutableLiveData<String>()
  val tanahAlasHak = MutableLiveData<String>()
  val tidakTerdaftar = MutableLiveData(false)

  val alasPembuat = MutableLiveData<String>()
  val alasTanggal = MutableLiveData<String>()
  val alasNoPersil = MutableLiveData<String>()
  val alasDesa = MutableLiveData<String>()
  val alasAlamat = MutableLiveData<String>()
  val alasNomor = MutableLiveData<String>()
  val alasKelas = MutableLiveData<String>()
  val alasLuas = MutableLiveData<String>()

  val ruangHM = MutableLiveData<String>()
  val ruangLuas = MutableLiveData<String>()
  val ruangHMSRS = MutableLiveData<String>()

//  val bangunanList: MutableList<BuildingData> = mutableListOf()
//  val bangunan = MutableLiveData<MutableList<BuildingData>>()
  val bangunanJenis = MutableLiveData("")
  val bangunanJumlah = MutableLiveData("")

//  val tanamanList: MutableList<PlantData> = mutableListOf()
//  val tanaman = MutableLiveData<MutableList<PlantData>>()
  val tanamanJenis = MutableLiveData("")
  val tanamanUmur = MutableLiveData("")
  val tanamanJumlah = MutableLiveData("")

//  val lainDataList: MutableList<OtherData> = mutableListOf()
//  val lain = MutableLiveData<MutableList<OtherData>>()
  val lainJenis = MutableLiveData<String>()
  val lainJumlah = MutableLiveData<String>()

  val descFeducida = MutableLiveData<String>()
  val descKeterangan = MutableLiveData<String>()

//  fun addBangunan() {
//    bangunanList.add(
//      BuildingData(
//        jenis = bangunanJenis.value ?: "",
//        jumlah = bangunanJumlah.value?.toInt() ?: 0
//      )
//    )
//    bangunan.postValue(bangunanList)
//    bangunanJenis.postValue("")
//    bangunanJumlah.postValue("")
//  }

//  fun addTanaman() {
//    tanamanList.add(
//      PlantData(
//        jenis = tanamanJenis.value ?: "",
//        jumlah = tanamanJumlah.value?.toInt() ?: 0,
//        umur = tanamanUmur.value?.toInt() ?: 0
//      )
//    )
//    tanaman.postValue(tanamanList)
//    tanamanJenis.postValue("")
//    tanamanJumlah.postValue("")
//    tanamanUmur.postValue("")
//  }

//  fun addOther() {
//    lainDataList.add(
//      OtherData(
//        jenis = lainJenis.value ?: "",
//        jumlah = lainJumlah.value?.toInt() ?: 0,
//      )
//    )
//    lain.postValue(lainDataList)
//    lainJenis.postValue("")
//    lainJumlah.postValue("")
//  }
//
//  fun deleteBuilding(index: Int) {
//    bangunanList.removeAt(index)
//    bangunan.postValue(bangunanList)
//  }
//
//  fun deletePlant(index: Int) {
//    bangunanList.removeAt(index)
//    bangunan.postValue(bangunanList)
//  }
//
//  fun deleteOther(index: Int) {
//    lainDataList.removeAt(index)
//    lain.postValue(lainDataList)
//  }

  fun loadData(email: String, areaId: String) {
    viewModelScope.launch {
      pengtanRepository.getPengtan(email, areaId).collect { result ->
        if (result is ResponseState.Success) {
          mapToDataResponse(result.data)
        }

      }
    }
  }

  fun mapToDataResponse(data: PengtanResponse) {
    pemilikNama.postValue(data.pemilikNama)
    pemilikPekerjaan.postValue(data.pemilikPekerjaan)
    pemilikAlamat.postValue(data.pemilikAlamat)
    pemilikTempatLahir.postValue(data.pemilikTempatLahir)
    pemilikTglLahir.postValue(data.pemilikTglLahir)
    pemilikNIK.postValue(data.pemilikNIK)

    penguasanNama.postValue(data.penguasanNama)
    penguasanPekerjaan.postValue(data.penguasanPekerjaan)
    penguasanAlamat.postValue(data.penguasanAlamat)
    penguasanTempatLahir.postValue(data.penguasanTempatLahir)
    penguasanTglLahir.postValue(data.penguasanTglLahir)
    penguasanNIK.postValue(data.penguasanNIK)

    tanahAlasHak.value = data.tanahAlasHak
    tanahNIB.postValue(data.tanahNIB)
    tanahNUB.postValue(data.tanahNUB)
    tanahRT.postValue(data.tanahRT)
    tanahRW.postValue(data.tanahRW)
    tanahLuas.postValue(data.tanahLuas)
    tanahStatus.postValue(data.tanahStatus)

    alasPembuat.postValue(data.alasPembuat)
    alasTanggal.postValue(data.alasTanggal)
    alasNoPersil.postValue(data.alasNoPersil)
    alasDesa.postValue(data.alasDesa)
    alasAlamat.postValue(data.alasAlamat)
    alasNomor.postValue(data.alasNomor)
    alasKelas.postValue(data.alasKelas)
    alasLuas.postValue(data.alasLuas)

    ruangHM.postValue(data.ruangHM)
    ruangLuas.postValue(data.ruangLuas)
    ruangHMSRS.postValue(data.ruangHMSRS)

    descFeducida.postValue(data.descFeducida)
    descKeterangan.postValue(data.descKeterangan)

//    bangunanList.addAll(
//      data.bangunanJenis.mapIndexed { index, s ->
//        BuildingData(jenis = s, jumlah = data.bangunanJumlah[index])
//      }
//    )
//    bangunan.postValue(bangunanList)
//
//
//    tanamanList.addAll(
//      data.tanamanJenis.mapIndexed { index, s ->
//        PlantData(jenis = s, jumlah = data.tanamanJumlah[index], umur = data.tanamanUmur[index])
//      }
//    )
//    tanaman.postValue(tanamanList)
//
//    lainDataList.addAll(
//      data.lainJenis.mapIndexed { index, s ->
//        OtherData(jenis = s, jumlah = data.lainJumlah[index])
//      }
//    )
//    lain.postValue(lainDataList)

  }

}