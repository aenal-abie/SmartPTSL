package smartgis.project.app.smartgis.controllers

import android.content.Context
import android.util.Log
//import org.apache.poi.hssf.usermodel.HSSFCell
//import org.apache.poi.hssf.usermodel.HSSFRow
//import org.apache.poi.hssf.usermodel.HSSFWorkbook
//import org.apache.poi.ss.usermodel.Cell
//import org.apache.poi.ss.usermodel.Row
import org.json.JSONArray
import org.json.JSONObject
import smartgis.project.app.smartgis.utils.EXCEL_FILE
import smartgis.project.app.smartgis.utils.appPreference
import java.io.File
import java.io.FileInputStream


class ImportExcelController(private val context: Context) {

  private var jsonObject: JSONObject? = null

  fun handle(handler: (JSONArray) -> Unit) {
//    try {
//      val location = context.appPreference().getString(EXCEL_FILE, "")
//      val file = File(location)
//      val fileInputStream = FileInputStream(file)
//      val myWorkBook = HSSFWorkbook(fileInputStream)
//      val mySheet = myWorkBook.getSheetAt(0)
//      var rowIter: Iterator<Row> = mySheet.rowIterator()
//      var rowno = 0;
//      val features = mutableListOf<Any>()
//      while (rowIter.hasNext()) {
//        val properties = mutableListOf<Any>()
//        val myRow = rowIter.next() as HSSFRow
//        if (rowno != 0) {
//          var cellIter: Iterator<Cell> = myRow.cellIterator()
//          var colno = 0;
//          while (cellIter.hasNext()) {
//            var myCell: HSSFCell = cellIter.next() as HSSFCell
//            colno++;
//            properties.add(myCell.toString())
//          }
//        }
//        rowno++;
//        features.add(
//          mapOf(
//            "properties" to properties
//          )
//        )
//      }
//    } catch (e: Exception) {
//
//    }
  }

}

data class ExcelData(var nub: String, var nama: String, var kluster: String)