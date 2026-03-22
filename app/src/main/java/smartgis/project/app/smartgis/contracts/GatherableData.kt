package smartgis.project.app.smartgis.contracts

interface GatherableData {
  fun getData(): MutableMap<String, Any>
}