package smartgis.project.app.smartgis.forms.yuridis_ptsl

import android.R.attr.padding
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.databases.BerkasYuridisHelper
import smartgis.project.app.smartgis.export.CreateRtkStatusPdf
import smartgis.project.app.smartgis.utils.currentUser
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateYuridisPdf(
    private val context: Context,
    private val id: String,
    private val docType: String,
    private val data: MutableMap<String, Any>?,
    private val form: String,
    private val workspaceName: String,
    private val nis: String
) {

    companion object {
        val FONT_TABLE_HEADER = Font(Font.FontFamily.HELVETICA, 9.5f, Font.BOLD).apply {
            color = BaseColor.WHITE
        }
        val FONT_BODY = Font(Font.FontFamily.HELVETICA, 12f)
        val TABLE_CELL_FONT = Font(Font.FontFamily.HELVETICA, 10f)
        val FONT_FOOTER = Font(Font.FontFamily.UNDEFINED, 8f)
        val FOOTER_BOLD = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
    }

    fun createPDF(fileName: String): Boolean {
        val tmpFileName = "tmp_${System.currentTimeMillis()}"

        return try {
            context.openFileOutput(tmpFileName, Context.MODE_PRIVATE).use { output ->
                val document = Document(PageSize.A4)
                val writer = PdfWriter.getInstance(document, output)

                writer.pageEvent = PageHeaderFooter()

                document.open()
                addMetaData(document)
                addHeader(document, writer)
                addSpaceTop(document)
                addContent(document)
                addImages(document)
                document.close()
            }

            val createdFile = context.getFileStreamPath(tmpFileName)
            if (createdFile.exists()) {
                createdFile.copyTo(File(fileName), overwrite = true)
                createdFile.delete()
                true
            } else {
                false
            }

        } catch (e: Exception) {
            Log.e("PDF_ERROR", "Error creating PDF", e)
            false
        }
    }

    // ================= HEADER =================

    private fun addHeader(document: Document, writer: PdfWriter) {
        try {
            val table = PdfPTable(2)
            table.setWidths(intArrayOf(2, 24))
            table.totalWidth = 527f
            table.isLockedWidth = true

            val logoBytes = context.resources.openRawResource(R.drawable.icon).readBytes()
            val logo = Image.getInstance(logoBytes)
            logo.scaleToFit(40f, 40f)

            val logoCell = PdfPCell(logo)
            logoCell.border = Rectangle.BOTTOM
            logoCell.borderColor = BaseColor.LIGHT_GRAY
            table.addCell(logoCell)

            val textCell = PdfPCell().apply {
                paddingLeft = 10f
                paddingBottom = 10f
                border = Rectangle.BOTTOM
                borderColor = BaseColor.LIGHT_GRAY

                addElement(Phrase("Smart PTSL", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)))
                addElement(Phrase("Pengambilan Data Yuridis", Font(Font.FontFamily.HELVETICA, 9f)))
            }

            table.addCell(textCell)

            table.writeSelectedRows(
                0,
                -1,
                document.leftMargin(),
                document.top() + 10,
                writer.directContent
            )

        } catch (e: Exception) {
            Log.e("PDF_HEADER", "Error adding header", e)
        }
    }

    // ================= CONTENT =================

    private fun addContent(document: Document) {
        val paragraph = Paragraph().apply {
            font = FONT_BODY
        }

        addDescriptionTable(paragraph)

        try {
            if (data != null) {
                val generator = GeneratePdfContent(context, docType, paragraph, data)
                document.add(generator.reportBody)
            } else {
                document.add(paragraph)
            }
        } catch (e: Exception) {
            Log.e("PDF_CONTENT", "Error adding content", e)
        }
    }

    private fun addDescriptionTable(paragraph: Paragraph) {
        val table = PdfPTable(floatArrayOf(1f, 2.8f)).apply {
            widthPercentage = 100f
        }

        val user = currentUser()
        val userInfo = user?.let {
            "${it.displayName}/${it.email}"
        } ?: "Unknown User"

        fun addRow(label: String, value: String) {
            table.addCell(createCellNoBorder(label))
            table.addCell(createCellNoBorder(value))
        }

        addRow("AKUN", userInfo)
        addRow("WORKSPACE", workspaceName.uppercase())
        addRow("FORM", form.uppercase())
        addRow("NIS", nis)
        addRow("", "")

        paragraph.add(table)
    }

    // ================= IMAGE =================

    private fun addImages(document: Document) {
        val helper = BerkasYuridisHelper()
//        val list = helper.getAllByParent(id, docType)

//        list.forEach {
//            try {
//                val file = File(it.pathImg)
//                if (!file.exists()) return@forEach
//
//                val image = Image.getInstance(it.pathImg)
//
//                val maxWidth = document.pageSize.width - document.leftMargin() - document.rightMargin()
//                val scalePercent = (maxWidth / image.width) * 100
//
//                image.scalePercent(scalePercent)
//                image.spacingBefore = 10f
//
//                document.add(image)
//
//            } catch (e: Exception) {
//                Log.e("PDF_IMAGE", "Failed load image: ${it.pathImg}", e)
//            }
//        }
    }

    // ================= FOOTER =================

    internal inner class PageHeaderFooter : PdfPageEventHelper() {

        private val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())

        override fun onEndPage(writer: PdfWriter, document: Document) {
            addFooter(writer)
        }

        private fun addFooter(writer: PdfWriter) {
            try {
                val table = PdfPTable(3)
                table.setWidths(intArrayOf(24, 2, 1))
                table.totalWidth = 527f

                table.defaultCell.border = Rectangle.TOP
                table.defaultCell.borderColor = BaseColor.LIGHT_GRAY

                table.addCell(Phrase("Dibuat tanggal $date", FOOTER_BOLD))

                table.defaultCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(Phrase("${writer.pageNumber}", FONT_FOOTER))

                table.addCell(PdfPCell().apply {
                    border = Rectangle.TOP
                    borderColor = BaseColor.LIGHT_GRAY
                })

                table.writeSelectedRows(0, -1, 34f, 50f, writer.directContent)

            } catch (e: Exception) {
                Log.e("PDF_FOOTER", "Error footer", e)
            }
        }
    }

    // ================= UTIL =================

    private fun createCellNoBorder(text: String): PdfPCell {
        return PdfPCell(Phrase(text, CreateRtkStatusPdf.TABLE_CELL_FONT)).apply {
            border = Rectangle.NO_BORDER
//            padding = 4f
        }
    }

    private fun addSpaceTop(document: Document) {
        val paragraph = Paragraph()
        repeat(4) {
            paragraph.add(Paragraph(" "))
        }
        document.add(paragraph)
    }

    private fun addMetaData(document: Document) {
        document.addTitle("Pengambilan Data Yuridis")
        document.addSubject("Pengambilan Data Yuridis")
        document.addAuthor("Smart PTSL")
        document.addCreator("Smart PTSL")
    }
}