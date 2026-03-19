package smartgis.project.app.smartgis.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageHolder(val path: String, val what: String) : Parcelable