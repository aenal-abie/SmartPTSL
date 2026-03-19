package smartgis.project.app.smartgis.models.response

data class PengtanResponse(
  val pemilikNama: String = "",
  val pemilikTempatLahir: String = "",
  val pemilikTglLahir: String = "",
  val pemilikAlamat: String = "",
  val pemilikPekerjaan: String = "",
  val pemilikNIK: String = "",

  val penguasanNama: String = "",
  val penguasanTempatLahir: String = "",
  val penguasanTglLahir: String = "",
  val penguasanAlamat: String = "",
  val penguasanPekerjaan: String = "",
  val penguasanNIK: String = "",

  val tanahNUB: String = "",
  val tanahNIB: String = "",
  val tanahLuas: String = "",
  val tanahStatus: String = "",
  val tanahRT: String = "",
  val tanahRW: String = "",
  val tanahAlasHak: String = "",

  val alasPembuat: String = "",
  val alasTanggal: String = "",
  val alasNoPersil: String = "",
  val alasDesa: String = "",
  val alasAlamat: String = "",
  val alasNomor: String = "",
  val alasKelas: String = "",
  val alasLuas: String = "",

  val bangunanJenis: MutableList<String> = mutableListOf(),
  val bangunanJumlah: MutableList<Int> = mutableListOf(),

  val tanamanJenis: MutableList<String> = mutableListOf(),
  val tanamanJumlah: MutableList<Int> = mutableListOf(),
  val tanamanUmur: MutableList<Int> = mutableListOf(),

  val lainJenis: MutableList<String> = mutableListOf(),
  val lainJumlah: MutableList<Int> = mutableListOf(),

  val ruangHM: String = "",
  val ruangLuas: String = "",
  val ruangHMSRS: String = "",

  val descFeducida: String = "",
  val descKeterangan: String = "",
)
