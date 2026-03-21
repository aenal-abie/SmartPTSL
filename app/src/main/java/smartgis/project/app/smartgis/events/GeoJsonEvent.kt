package smartgis.project.app.smartgis.events

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.json.JSONObject

@Parcelize
data class GeoJsonEvent(val json: @RawValue JSONObject) : Parcelable