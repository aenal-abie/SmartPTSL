package smartgis.project.app.smartgis.utils

import android.os.Environment

const val MB_TILES_FOLDER = "MB_TILES_FOLDER"
const val EXCEL_FOLDER = "EXCEL_FOLDER"
const val EXCEL_FILE = "EXCEL_FILE"
const val USER_AGREE_TO_PRIVACY_POLICY = "USER_AGREE_TO_PRIVACY_POLICY"
const val USER_DONASI = "SHOW_DONASI"
const val STORAGE_PERMISSION_REQUEST = 2
val BASE_STORAGE_PATH: String? = "${Environment.getExternalStorageDirectory().path}/SmartPTSL"
val BASE_DOWNLOAD_STORAGE_PATH: String? = "${Environment.DIRECTORY_DOWNLOADS}/SmartPTSL"
val BASE_EXPORT_PATH = "$BASE_STORAGE_PATH/Export"
val BASE_DOC_PATH = "$BASE_STORAGE_PATH/YURIDIS"
const val NTRIP_MODE = "NTRIP"