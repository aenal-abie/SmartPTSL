package smartgis.project.app.smartgis.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
//import com.loopj.android.http.JsonHttpResponseHandler
//import cz.msebera.android.httpclient.Header
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import smartgis.project.app.smartgis.data.repositories.entity.wms.Feature
import smartgis.project.app.smartgis.data.repositories.entity.wms.WMSKantah
import smartgis.project.app.smartgis.data.repositories.service.Result
import smartgis.project.app.smartgis.decorators.ShapeWMSDecorator
import smartgis.project.app.smartgis.decorators.WMSProperties
//import smartgis.project.app.smartgis.http.Http
import smartgis.project.app.smartgis.repository.base.IFeatureRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val featureRepository: IFeatureRepository) :
  ViewModel() {

  var checkSatgasPangtan: Boolean = false
  var isWMSActive = false

  private val _wmsShapeDecorator = MutableLiveData<ShapeWMSDecorator>()
  val wmsShapeDecorator: LiveData<ShapeWMSDecorator> = _wmsShapeDecorator

  fun newClickWMS(coordinate: LatLng?) {
    viewModelScope.launch {
      featureRepository.getPolygon(coordinate).let {
        if (it is Result.Success) {
          it
        }

      }
    }
  }

  fun clickWms(coordinate: LatLng?, kantahId: String) {
    var url = ""
    if (kantahId != "38")
      url = "http://180.178.109.123:8080/geoserver/smartportal/wms" +
          "?service=WMS" +
          "&version=1.1.0" +
          "&request=GetFeatureInfo" +
          "&QUERY_LAYERS=smartportal:portal" +
          "&LAYERS=smartportal:portal" +
          "&width=101&height=101" +
          "&srs=EPSG:4326" +
          "&cql_filter=kantah_id='${kantahId}'" +
          "&FEATURE_COUNT=50&X=0&Y=0" +
          "&info_format=application/json" +
          "&BBOX=${coordinate?.longitude},${coordinate?.latitude}," +
          "${coordinate?.longitude?.plus(0.00006)}" +
          ",${coordinate?.latitude?.plus(0.00006)}"
    else
      url = "http://103.144.75.131:8080/geoserver/smartptsl/wms" +
          "?service=WMS" +
          "&version=1.1.0" +
          "&request=GetFeatureInfo" +
          "&QUERY_LAYERS=smartptsl:portal" +
          "&LAYERS=smartptsl:portal" +
          "&width=101&height=101" +
          "&srs=EPSG:4326" +
          "&cql_filter=kantah_id='${kantahId}'" +
          "&FEATURE_COUNT=50&X=0&Y=0" +
          "&info_format=application/json" +
          "&BBOX=${coordinate?.longitude},${coordinate?.latitude}," +
          "${coordinate?.longitude?.plus(0.00006)}" +
          ",${coordinate?.latitude?.plus(0.00006)}"
//    val client = Http.client
//    client.get(
//      url,
//      object : JsonHttpResponseHandler() {
//        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray?) {
//          super.onSuccess(statusCode, headers, response)
//        }
//
//
//        override fun onFailure(
//          statusCode: Int,
//          headers: Array<out Header>?,
//          throwable: Throwable?,
//          errorResponse: JSONArray?
//        ) {
//          super.onFailure(statusCode, headers, throwable, errorResponse)
//        }
//
//        override fun onSuccess(
//          statusCode: Int,
//          headers: Array<out Header>?,
//          response: JSONObject?
//        ) {
//
//          val gsonBuilder = GsonBuilder().create()
//          val data = gsonBuilder.fromJson(response.toString(), WMSKantah::class.java)
//          if (data.numberReturned!! > 0) {
//            mapDataToLocal(data.features?.get(0))
//          }
//
//        }
//
//        override fun onSuccess(
//          statusCode: Int,
//          headers: Array<out Header>?,
//          responseString: String?
//        ) {
//
//        }
//
//        override fun onFailure(
//          statusCode: Int,
//          headers: Array<out Header>?,
//          throwable: Throwable?,
//          errorResponse: JSONObject?
//        ) {
//
//        }
//
//        override fun onFailure(
//          statusCode: Int,
//          headers: Array<out Header>?,
//          responseString: String?,
//          throwable: Throwable?
//        ) {
//
//        }
//      })

  }

  private fun mapDataToLocal(features: Feature?) {
    val newLatLong: MutableList<LatLng> = mutableListOf()

    var wmsProperties: WMSProperties
    features?.let { feature ->
      feature.geometry?.coordinates?.get(0)?.get(0)?.forEach {
        newLatLong.add(LatLng(it[1], it[0]))
      }

      feature.properties.let {
        wmsProperties = WMSProperties(
          nama = it?.nama ?: "",
          nib = it?.nib ?: "",
          no_hak = it?.noHak ?: "",
          tipe_hak = it?.tipeHak ?: "",
          keterangan = it?.keterangan ?: "",
        )
      }
      _wmsShapeDecorator.postValue(ShapeWMSDecorator(properties = wmsProperties, newLatLong))
    }
  }
}