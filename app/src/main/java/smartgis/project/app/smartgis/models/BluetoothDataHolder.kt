package smartgis.project.app.smartgis.models

data class BluetoothDataHolder(val name: String, val address: String) {
  override fun toString(): String {
    return "$name\n$address"
  }
}