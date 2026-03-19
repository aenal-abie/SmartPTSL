package smartgis.project.app.smartgis.documents

import com.google.firebase.storage.FirebaseStorage

object Storages {
  fun getFile(filePath: String) = FirebaseStorage.getInstance().reference.child(filePath)
  fun getImageReference(filename: String) = getFile("images/$filename")
  fun getSignatureReference(filename: String) = getFile("signatures/$filename")
  fun getProfileReference(filename: String) = getFile("profile/$filename")
}