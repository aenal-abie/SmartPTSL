package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.content.Context
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import smartgis.project.app.smartgis.export.CreateRtkStatusPdf
import smartgis.project.app.smartgis.utils.noNull
import smartgis.project.app.smartgis.utils.prefix


class GeneratePdfContent(
  val context: Context,
  val type: String,
  val reportBody: Paragraph,
  val data: MutableMap<String, Any>
) {

  init {
    when (type) {
      PERORANGAN -> generateDataPerorangan()
      ALAS_HAK -> generateDataAlasHak()
      BPHTB -> generateDataBPHTB()
      PPH -> generateDataPPH()
      PBB -> generateDataPBB()
      AKTA_JUAL_BELI -> generateDataAktaJualBeli()
      AKTA_WAKAF -> generateDataAktaWakap()
    }
  }

  @Throws(BadElementException::class)
  fun generateDataPerorangan() {

    val columnWidths = floatArrayOf(
      1.5f,
      2.8f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers

    table.addCell(createItemCell("NIK"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.NIK.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("NAMA LENGKAP"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.NAMA.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("ALAMAT"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.ALAMAT.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("LOKASI"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.LOKASI.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("KODE POS"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.KODE_POS.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("TEMPAT LAHIT"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.TEMPAT_LAHIR.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("TANGGAL LAHIR"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.TGL_LAHIR.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("JENIS KELAMIN"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.JENIS_KELAMIN.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("STATUS"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.STATUS.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("RT.RW"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.RT_RW.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("GOLONGAN DARAH"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.GOLONGAN_DARAH.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("PEKERJAAN"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.PEKERJAAN.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    table.addCell(createItemCell("NO KK"))
    table.addCell(
      createItemCell(
        data.get(YuridisNIK.NO_KK.addPrefix(YuridisNIK.PREFIX)).toString().noNull()
      )
    )
    reportBody.add(table)


  }

  @Throws(BadElementException::class)
  fun generateDataAlasHak() {
    val columnWidths = floatArrayOf(
      1.5f,
      2.8f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers
    table.addCell(createItemCell("ALAS HAK"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.ALAS_HAK.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("PEMBUAT"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.ALAS_HAK.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("TANGGAL"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.TANGGAL.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NO PERSIL"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.NO_PERSIL.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("DESA/KELURAHAN"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.DESA_KELURAHAN.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("ALAMAT"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.ALAMAT.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NOMOR"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.NOMOR.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("KELAS"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.KELAS.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("LUAS"))
    table.addCell(
      createItemCell(
        data.get(YuridisAlasHak.LUAS.addPrefix(YuridisAlasHak.PREFIX)).toString().noNull()
      )
    )
    // add to table dont forget
    reportBody.add(table)
  }

  @Throws(BadElementException::class)
  fun generateDataBPHTB() {

    val columnWidths = floatArrayOf(
      1.5f,
      2.8f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers
    table.addCell(createItemCell("STATUS"))
    table.addCell(
      createItemCell(
        data.get(YuridisBPHTB.STATUS.addPrefix(YuridisBPHTB.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NOMOR OBJEK PAJAK (NOP)"))
    table.addCell(
      createItemCell(
        data.get(YuridisBPHTB.NOP.addPrefix(YuridisBPHTB.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NOMOR BUKTI PEMBAYARAN"))
    table.addCell(
      createItemCell(
        data.get(YuridisBPHTB.NOMOR_BUKTI.addPrefix(YuridisBPHTB.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NILAI BPHTB (RP.)"))
    table.addCell(
      createItemCell(
        data.get(YuridisBPHTB.NILAI.addPrefix(YuridisBPHTB.PREFIX)).toString().noNull()
      )
    )

    // add to table dont forget
    reportBody.add(table)

  }

  fun generateDataPPH() {
    val columnWidths = floatArrayOf(
      1.5f,
      2.8f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers
    table.addCell(createItemCell("STATUS"))
    table.addCell(
      createItemCell(
        data.get(YuridisPPH.STATUS.addPrefix(YuridisPPH.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NOMOR"))
    table.addCell(
      createItemCell(
        data.get(YuridisPPH.NOMOR.addPrefix(YuridisPPH.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("TANGGAL"))
    table.addCell(
      createItemCell(
        data.get(YuridisPPH.TANGGAL.addPrefix(YuridisPPH.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NILAI"))
    table.addCell(
      createItemCell(
        data.get(YuridisPPH.NILAI.addPrefix(YuridisPPH.PREFIX)).toString().noNull()
      )
    )

    // add to table dont forget
    reportBody.add(table)

  }

  fun generateDataPBB() {
    val columnWidths = floatArrayOf(
      1.5f,
      2.8f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers
    table.addCell(createItemCell("ALAS HAK"))
    table.addCell(
      createItemCell(
        data.get(YuridisPBB.JENIS.addPrefix(YuridisPBB.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("SPPT (NOP)/TAHUN"))
    table.addCell(
      createItemCell(
        data.get(YuridisPBB.SPPT.addPrefix(YuridisPBB.PREFIX)).toString().noNull() + "/" +
            data.get(YuridisPBB.SPPT_TAHUN.addPrefix(YuridisPBB.PREFIX)).toString().noNull()
      )
    )


    table.addCell(createItemCell("LUAS"))
    table.addCell(
      createItemCell(
        data.get(YuridisPBB.LUAS.addPrefix(YuridisPBB.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NJOP PER M2 (RP)"))
    table.addCell(
      createItemCell(
        data.get(YuridisPBB.NJOP.addPrefix(YuridisPBB.PREFIX)).toString().noNull()
      )
    )

    // add to table dont forget
    reportBody.add(table)

  }

  fun generateDataAktaJualBeli() {
    val columnWidths = floatArrayOf(
      1.5f,
      2.8f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers
    table.addCell(createItemCell("ALAS HAK"))
    table.addCell(
      createItemCell(
        data.get(YuridisJualbeli.JENIS.addPrefix(YuridisJualbeli.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("PEMBUAT AKTA"))
    table.addCell(
      createItemCell(
        data.get(YuridisJualbeli.PEMBUAT.addPrefix(YuridisJualbeli.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("TANGGAL AKTA"))
    table.addCell(
      createItemCell(
        data.get(YuridisJualbeli.TANGGAL.addPrefix(YuridisJualbeli.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NOMOR AKTA"))
    table.addCell(
      createItemCell(
        data.get(YuridisJualbeli.NOMOR.addPrefix(YuridisJualbeli.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("MATA UANG"))
    table.addCell(
      createItemCell(
        data.get(YuridisJualbeli.MATA_UANG.addPrefix(YuridisJualbeli.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NILAI KURS"))
    table.addCell(
      createItemCell(
        data.get(YuridisJualbeli.KURS.addPrefix(YuridisJualbeli.PREFIX)).toString().noNull()
      )
    )



    table.addCell(createItemCell("NILAI"))
    table.addCell(
      createItemCell(
        data.get(YuridisJualbeli.NILAI.addPrefix(YuridisJualbeli.PREFIX)).toString().noNull()
      )
    )



    // add to table dont forget
    reportBody.add(table)

  }

  fun generateDataAktaWakap() {
    val columnWidths = floatArrayOf(
      1.5f,
      2.8f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers
    table.addCell(createItemCell("ALAS HAK"))
    table.addCell(
      createItemCell(
        data.get(YuridisWakaf.JENIS.addPrefix(YuridisWakaf.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("PEMBUAT AKTA"))
    table.addCell(
      createItemCell(
        data.get(YuridisWakaf.PEMBUAT.addPrefix(YuridisWakaf.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("TANGGAL AKTA"))
    table.addCell(
      createItemCell(
        data.get(YuridisWakaf.TANGGAL.addPrefix(YuridisWakaf.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NOMOR AKTA"))
    table.addCell(
      createItemCell(
        data.get(YuridisWakaf.NOMOR.addPrefix(YuridisWakaf.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("MATA UANG"))
    table.addCell(
      createItemCell(
        data.get(YuridisWakaf.MATA_UANG.addPrefix(YuridisWakaf.PREFIX)).toString().noNull()
      )
    )

    table.addCell(createItemCell("NILAI KURS"))
    table.addCell(
      createItemCell(
        data.get(YuridisWakaf.KURS.addPrefix(YuridisWakaf.PREFIX)).toString().noNull()
      )
    )



    table.addCell(createItemCell("NILAI"))
    table.addCell(
      createItemCell(
        data.get(YuridisWakaf.NILAI.addPrefix(YuridisWakaf.PREFIX)).toString().noNull()
      )
    )



    // add to table dont forget
    reportBody.add(table)

  }

  private fun createHeaderCell(title: String): PdfPCell {
    val cell = PdfPCell(Phrase(title, CreateRtkStatusPdf.FONT_TABLE_HEADER))
    cell.horizontalAlignment = Element.ALIGN_CENTER
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.backgroundColor = BaseColor(0, 0, 0, 0)
    cell.fixedHeight = 30f
    return cell
  }

  private fun createItemCell(data: String): PdfPCell {
    val cell = PdfPCell(Phrase(data, CreateRtkStatusPdf.TABLE_CELL_FONT))
    cell.fixedHeight = 28f
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.paddingLeft = 3f
    return cell
  }

  private fun String.addPrefix(prefixVal: String) = prefix(prefixVal)

  companion object {
    const val PERORANGAN = "perorangan"
    const val PERSIL = "persil"
    const val ALAS_HAK = "alas_hak"
    const val BPHTB = "bphtb"
    const val PPH = "pph"
    const val PBB = "pbb"
    const val AKTA_JUAL_BELI = "akta_jual_beli"
    const val AKTA_WAKAF = "akta_wakaf"
  }
}
