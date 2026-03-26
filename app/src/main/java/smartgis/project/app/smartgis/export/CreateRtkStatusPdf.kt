package smartgis.project.app.smartgis.export

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.*
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.models.GnssStatusHolder
import smartgis.project.app.smartgis.models.TableItem
import smartgis.project.app.smartgis.utils.toBytes
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault


class CreateRtkStatusPdf(val context: Context, val data: List<TableItem>) {

  private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon).toBytes()

  fun createPDF(fileName: String): Boolean {
    try {
      val tmpFile = context.openFileOutput("tmp", Context.MODE_PRIVATE)
      val document = Document(PageSize.LEGAL)
      val pdfWriter = PdfWriter.getInstance(document, tmpFile)
      val event = PageHeaderFooter()
      pdfWriter.pageEvent = event
      document.open()
      addMetaData(document)
      addHeader(document, pdfWriter)
      addSpaceParagraphAtTop(document)
      addEmptyLine(Paragraph(""), 2)
      addContent(document)
      document.close()
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
    val createdFile = context.getFileStreamPath("tmp")
    if (createdFile.exists()) {
      createdFile.copyTo(File(fileName), true)
      createdFile.delete()
      return true
    }
    return false
  }

  @Throws(DocumentException::class)
  private fun addSpaceParagraphAtTop(document: Document) {
    val paragraph = Paragraph()
    val childParagraph = Paragraph("", FONT_FOOTER)
    paragraph.add(childParagraph)
    addEmptyLine(paragraph, 4)
    document.add(paragraph)
  }

  @Throws(DocumentException::class)
  private fun addContent(document: Document) {
    val reportBody = Paragraph()
    reportBody.font = FONT_BODY
    createTable(reportBody)
    document.add(reportBody)
  }

  private fun addEmptyLine(paragraph: Paragraph, number: Int) {
    for (i in 0 until number) {
      paragraph.add(Paragraph(" "))
    }
  }

  @Throws(BadElementException::class)
  private fun createTable(reportBody: Paragraph) {

    val columnWidths = floatArrayOf(
      2.8f,
      1.5f,
      6f,
      6f,
      4.5f,
      2f,
      2f,
      2f,
      2f,
      2f
    )
    val table = PdfPTable(columnWidths)

    table.widthPercentage = 100f //set table with 100% (full page)
    table.defaultCell.isUseAscender = true


    //Adding table headers
    table.addCell(createHeaderCell("NUB"))
    table.addCell(createHeaderCell("NO."))
    table.addCell(createHeaderCell(BaseExportableData.X.uppercase(getDefault())))
    table.addCell(createHeaderCell(BaseExportableData.Y.uppercase(getDefault())))
    table.addCell(createHeaderCell(GnssStatusHolder.STATUS.uppercase(getDefault())))
    table.addCell(createHeaderCell(GnssStatusHolder.HRMS.uppercase(getDefault())))
    table.addCell(createHeaderCell(GnssStatusHolder.VRMS.uppercase(getDefault())))
    table.addCell(createHeaderCell(GnssStatusHolder.HDOP.uppercase(getDefault())))
    table.addCell(createHeaderCell(GnssStatusHolder.VDOP.uppercase(getDefault())))
    table.addCell(createHeaderCell(GnssStatusHolder.PDOP.uppercase(getDefault())))

    //Adding data into table
    data.forEach {
      table.addCell(createItemCell(it.nub))
      table.addCell(createItemCell("${it.number}").apply {
        horizontalAlignment = Element.ALIGN_CENTER
      })
      table.addCell(createItemCell(it.x.toString()))
      table.addCell(createItemCell(it.y.toString()))
      table.addCell(createItemCell(it.status))
      table.addCell(createItemCell("%.3f".format(it.hrms)))
      table.addCell(createItemCell("%.3f".format(it.vrms)))
      table.addCell(createItemCell("%.2f".format(it.hdop)))
      table.addCell(createItemCell("%.2f".format(it.vdop)))
      table.addCell(createItemCell("%.2f".format(it.pdop)))
    }
    reportBody.add(table)
  }

  private fun createHeaderCell(title: String): PdfPCell {
    val cell = PdfPCell(Phrase(title, FONT_TABLE_HEADER))
    cell.horizontalAlignment = Element.ALIGN_CENTER
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.backgroundColor = BaseColor(0, 121, 107, 128)
    cell.fixedHeight = 30f
    return cell
  }

  private fun createItemCell(data: String): PdfPCell {
    val cell = PdfPCell(Phrase(data, TABLE_CELL_FONT))
    cell.fixedHeight = 28f
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.paddingLeft = 3f
    return cell
  }


  private fun addMetaData(document: Document) {
    document.addTitle("Laporan Status RTK")
    document.addSubject("Laporan Status RTK")
    document.addKeywords("Java, PDF, Android, SmartPTSL, GIS")
    document.addAuthor("Smart PTSL")
    document.addCreator("Smart PTSL")
    Log.i("PDF", "${document.top()}")
  }

  @Throws(DocumentException::class)

  private fun addHeader(document: Document, writer: PdfWriter?) {
    val header = PdfPTable(2)
    try {
      // set defaults
      header.setWidths(intArrayOf(2, 24))
      header.totalWidth = 527f
      header.isLockedWidth = true
      header.defaultCell.fixedHeight = 40f
      header.defaultCell.border = Rectangle.BOTTOM
      header.defaultCell.borderColor = BaseColor.LIGHT_GRAY

      // add image
      val logo = Image.getInstance(bitmap)
      header.addCell(logo)

      // add text
      val text = PdfPCell()
      text.paddingBottom = 15f
      text.paddingLeft = 10f
      text.border = Rectangle.BOTTOM
      text.borderColor = BaseColor.LIGHT_GRAY
      text.addElement(Phrase("Smart PTSL", Font(Font.FontFamily.HELVETICA, 12f)))
      text.addElement(
        Phrase(
          "Laporan Status RTK - ${data[0].zone}",
          Font(Font.FontFamily.HELVETICA, 9f)
        )
      )
      header.addCell(text)

      // write content
      header.writeSelectedRows(0, -1, 34f, document.top() - 10, writer?.directContent)
    } catch (de: DocumentException) {
      throw ExceptionConverter(de)
    } catch (e: MalformedURLException) {
      throw ExceptionConverter(e)
    } catch (e: IOException) {
      throw ExceptionConverter(e)
    }

  }

  internal inner class PageHeaderFooter : PdfPageEventHelper() {

    @SuppressLint("SimpleDateFormat")
    private val date = SimpleDateFormat("yyyy.MM.dd").format(Date())

    @SuppressLint("SimpleDateFormat")
    override fun onEndPage(writer: PdfWriter?, document: Document?) {
      addFooter(writer)
    }


    private fun addFooter(writer: PdfWriter?) {
      val footer = PdfPTable(3)
      try {
        // set defaults
        footer.setWidths(intArrayOf(24, 2, 1))
        footer.totalWidth = 527f
        footer.isLockedWidth = true
        footer.defaultCell.fixedHeight = 40f
        footer.defaultCell.border = Rectangle.TOP
        footer.defaultCell.borderColor = BaseColor.LIGHT_GRAY

        // add copyright
        footer.addCell(Phrase("Dibuat tanggal $date", FOOTER_BOLD))

        // add current page count
        footer.defaultCell.horizontalAlignment = Element.ALIGN_RIGHT
        footer.addCell(Phrase(String.format("%d", writer?.pageNumber), FONT_FOOTER))

        // add placeholder for total page count
        val totalPageCount = PdfPCell()
        totalPageCount.border = Rectangle.TOP
        totalPageCount.borderColor = BaseColor.LIGHT_GRAY
        footer.addCell(totalPageCount)

        // write page
        val canvas = writer?.directContent
        canvas?.beginMarkedContentSequence(PdfName.ARTIFACT)
        footer.writeSelectedRows(0, -1, 34f, 50f, canvas)
        canvas?.endMarkedContentSequence()
      } catch (de: DocumentException) {
        throw ExceptionConverter(de)
      }
    }
  }


  companion object {
    val FONT_TABLE_HEADER = Font(Font.FontFamily.HELVETICA, 9.5f, Font.BOLD).apply {
      color = BaseColor.WHITE
    }
    val FONT_BODY = Font(Font.FontFamily.HELVETICA, 12f)
    val TABLE_CELL_FONT = Font(Font.FontFamily.HELVETICA, 10f)
    val FONT_FOOTER = Font(Font.FontFamily.UNDEFINED, 8f)
    val FOOTER_BOLD = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
  }

}