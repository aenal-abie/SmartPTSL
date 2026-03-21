package smartgis.project.app.smartgis

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import smartgis.project.app.smartgis.command.Actionable
import smartgis.project.app.smartgis.command.PolygonUndoSnapp
import smartgis.project.app.smartgis.controllers.ImportGeoJSONController
import smartgis.project.app.smartgis.controllers.ImportShpController
import smartgis.project.app.smartgis.databinding.ActivityMainBinding
import smartgis.project.app.smartgis.databinding.ActivityWelcomeLoginBinding
import smartgis.project.app.smartgis.decorators.PolygonDecorator
import smartgis.project.app.smartgis.decorators.PolylineDecorator
import smartgis.project.app.smartgis.decorators.ShapeImportedDecorator
import smartgis.project.app.smartgis.decorators.ShapeWMSDecorator
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.events.*;
import smartgis.project.app.smartgis.models.Area
import smartgis.project.app.smartgis.models.GnssStatusHolder
import smartgis.project.app.smartgis.models.ReferenceToGnssStatusHolder
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.CircleFromLatLng
import smartgis.project.app.smartgis.utils.ClusterColorMapping
import smartgis.project.app.smartgis.utils.GpsUtils
import smartgis.project.app.smartgis.utils.LineColorMapping
import smartgis.project.app.smartgis.utils.SimpleLocation
import smartgis.project.app.smartgis.utils.appPreference
import smartgis.project.app.smartgis.utils.computeAreaByCoordinate
import smartgis.project.app.smartgis.utils.currentUser
import smartgis.project.app.smartgis.utils.distanceTo
import smartgis.project.app.smartgis.utils.geometry.Vector2
import smartgis.project.app.smartgis.utils.getCenter
import smartgis.project.app.smartgis.utils.gone
import smartgis.project.app.smartgis.utils.rColor
import smartgis.project.app.smartgis.utils.shape.defaultCircle
import smartgis.project.app.smartgis.utils.shape.defaultIconGenerator
import smartgis.project.app.smartgis.utils.show
import smartgis.project.app.smartgis.utils.timeStamp
import smartgis.project.app.smartgis.utils.toTm3
import smartgis.project.app.smartgis.utils.waktu
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity :  LoginRequiredActivity(),
    OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnPolygonClickListener,
    GoogleMap.OnMapClickListener,
    //GoogleMap.OnMarkerDragListener,
    GoogleMap.OnPolylineClickListener {
    private var map: GoogleMap? = null
    private lateinit var kantah: String
    private lateinit var gpsPosition: LatLng
    private var accuracy: Float = 0.0f
    private var altitude: Double = 0.0
//    private lateinit var mInterstitialAd: InterstitialAd
    private val circleMarkers: MutableList<Marker> = mutableListOf()
    private val pointMarkerSession: MutableList<LatLng> = mutableListOf()
    private val pointWithRtkStatusSession: MutableList<MutableMap<String, Any>> = mutableListOf()
    private val pointTakenWithGnssStatusses = mutableSetOf<ReferenceToGnssStatusHolder>()
    private val localPolygonDbReference = mutableListOf<PolygonDecorator>()
    private val localPolylineDbReference = mutableListOf<PolylineDecorator>()
    private var markerStatus = mutableMapOf<Int, String>()
    private var workspaceIncrementCounter = 0
    private var addSPenMode = false
    private var editing = false
    private var doneClicked = false
    private var isGPS: Boolean = false
    private lateinit var locationHelper: SimpleLocation
    private var tileOverlay: TileOverlay? = null
    private lateinit var tileProvider: TileProvider
    private var showPermissionDeniedDialog = false
    private lateinit var workspace: Workspace
//    private var logShower: ShowData? = null
    private var dialogInterface: DialogInterface? = null
    private var activatingShape: Boolean = false
    private var polygonDisplay: Polygon? = null
    private var polylineDisplay: Polyline? = null
    private val selectedCircle = mutableSetOf<Marker>()
    private val selectedPointWithStatus = mutableSetOf<Marker>()
    private var firstSelectedPolygon: Polygon? = null
    private var lastCircleLocation: LatLng? = null
    private val selectedPolygon = mutableSetOf<Polygon>()
    private val selectedPolyline = mutableSetOf<Polyline>()
    private val selectedPolygonCircle = mutableSetOf<Marker>()
    private var clickedImportedShp: ShapeImportedDecorator? = null
    private var computedArea: Int = 0
    private var nub = ""
    private var draggingPolygonMarker: Polygon? = null
    private var circleRtkPosition: Circle? = null
    private val shpImported = mutableListOf<ShapeImportedDecorator>()
    private val wmsActived = mutableListOf<ShapeWMSDecorator>()
    private val polygonUndoStack = mutableListOf<Actionable>()
    private val lastTwoMarker = mutableSetOf<LatLng>()
    private val addedDistanceLabel = mutableSetOf<Marker>()
    private var importShpController: ImportShpController? = null
    private var importGeoJsonController: ImportGeoJSONController? = null
    private val changedDistanceCircle = mutableSetOf<CircleFromLatLng>()
    private var pausingRtk = false
    private var isMode = 1
    private var isRtkConnected = false
    private var state: NetworkInfo.State? = null
    private var connectionStateDisposable: Disposable? = null
    private var globalIndefiniteSnackbar: Snackbar? = null
    private var isCreatingSuratPernyataan: Boolean = false
    private var gnssStatusHolder = GnssStatusHolder()
    private var isProFeaturePurchased: Boolean = false
    private lateinit var clusterColorMaps: ClusterColorMapping
    private lateinit var lineColorMaps: LineColorMapping
    private var tetanggaBerbatasan: Int = 0
    private var tetanggaBerbatasanSelected: String = ""
    private var isPolyline: Boolean = false
//    private lateinit var rewardedAd: RewardedAd
    private var pointsWorspace: Int = 30
    private var bidangWorkspace: Int = 0
//    private lateinit var billingContainer: BillingProcessor
    private var lastLocation = LatLng(-7.7827188, 110.343225)
//    private val viewModel by viewModels<MainViewModel>()
    private lateinit var  wmsTileProvider1: TileProvider
    private lateinit var wmsTileProvider2: TileProvider
    private lateinit var wmsTileProvider3: TileProvider
    private  val tileOverlays = mutableListOf<TileOverlay>()

    companion object {
        private val TAG = MainActivity::class.java.name
        private const val ASK_LOCATION_PERMISSIONS_REQUEST_CODE = 11
        private const val ASK_STORAGE_PERMISSIONS_REQUEST_CODE = 22
        private const val ASK_BLUETOOTH_PERMISSIONS_REQUEST_CODE = 33
        private const val SEARCH_REQUEST_CODE = 2
        private const val UPLOAD_SHP_CODE = 1001
        private const val SELECT_MBTILE = 3001
        private const val UPLOAD_GEOJSON_CODE = 1002
        private const val DONE_CLICKED = 3
        private const val NORMAL_STROKE_WIDTH = 2F
        private const val TETANGGA_UTARA = 1
        private const val TETANGGA_SELATAN = 2
        private const val TETANGGA_BARAT = 3
        private const val TETANGGA_TIMUR = 4
        private const val POLYGON_MODE = 1
        private const val POINT_MODE = 2
        private const val POLYLINE_MODE = 3
        const val DATA_SIZE = "DATA_SIZE"
        const val AREA = "AREA"
//        val TTD_PEMILIK_URL =
//            BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureFormActivity.INFIX)
//        val TTD_SAKSI2_URL =
//            BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureSaksiKedua.INFIX)
//        val TTD_SAKSI1_URL =
//            BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureSaksiPertama.INFIX)
//        val updateIntervalInMilliseconds = 5 * 1 * 1000.toLong()
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        loadAds()
//        registerEvent()
        clusterColorMaps = ClusterColorMapping(this)
        lineColorMaps = LineColorMapping(this)
        askLocationForPermissions()
//        listenConnectionState()
//        importShpController = ImportShpController(this)
//        importGeoJsonController = ImportGeoJSONController(this)

        workspace = intent.getParcelableExtra(Workspace.INTENT)!!
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        setSupportActionBar(toolbar)
        supportActionBar?.subtitle = workspace.name
        mapFragment.getMapAsync(this)
//        setupLocation()
//        addLocationIfNotExist()
        binding.btnSPen.setOnClickListener {
            addSPenMode = true
            isMode = POLYGON_MODE
            editing = true
            binding.btnActionContainer.toggle()
            binding.addShapeMenuContainer.close(true)
            binding.addShapeMenuContainer.gone()
            binding.btnAddMark.gone()
        }
//        wmsObservers()
        binding.btnToggleListenRtkEvent.setOnClickListener {
            pausingRtk = !pausingRtk
            if (pausingRtk) {
                binding.btnToggleListenRtkEvent.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                gnssStatusHolder.reset()
            } else
                binding.btnToggleListenRtkEvent.setImageResource(R.drawable.ic_pause_white_24dp)
        }

        binding.btnTogglePolygonPoint.setOnClickListener {
            if (isMode == POLYGON_MODE) {
                binding.btnTogglePolygonPoint.setImageResource(R.drawable.ic_location_pin)
                isMode = POINT_MODE
            } else if (isMode == POINT_MODE) {
                binding.btnTogglePolygonPoint.setImageResource(R.drawable.ic_polygon)
                isMode = POLYLINE_MODE
            } else if (isMode == POLYLINE_MODE) {
                binding.btnTogglePolygonPoint.setImageResource(R.drawable.ic_polygon_icon)
                isMode = POLYGON_MODE
            }
        }

//        logShower = ShowData()
        binding.tvRtkStatus.setOnClickListener {} //logShower?.showDialog() }
        binding.tvHvrms.setOnClickListener {}// logShower?.showDialog() }

        binding.btnPointerMarker.setOnClickListener {
            editing = true
            binding.ivMarker.show()
            binding.btnActionContainer.toggle()
            binding.addShapeMenuContainer.close(true)
            binding.addShapeMenuContainer.gone()
            binding.btnAddMark.show()
            binding.btnToggleListenRtkEvent.show()
            binding.btnTogglePolygonPoint.show()
        }

        binding.btnAddMark.setOnClickListener {
//            Log.e(TAG, gnssStatusHolder.data().toString())
            map?.cameraPosition?.target?.let { it1 ->
//                Log.i(localClassName, "isrtkconnected: $isRtkConnected")
                if (isRtkConnected || isMode == POINT_MODE) {
//                    Log.i(localClassName, "adding point with status $gnssStatusHolder")
//                    Log.i(localClassName, "$it1")
                    if (pausingRtk) {
                        gnssStatusHolder.reset()
                    }
                    gnssStatusHolder.point = GeoPoint(it1.latitude, it1.longitude)
                    if (isMode == POINT_MODE) {
                        Collections.getUserWorkspacePoints(currentUser()?.email, workspace.id)
                            .add(timeStamp(gnssStatusHolder.saveToDB()))
                    } else
                        pointWithRtkStatusSession.add(gnssStatusHolder.data())
                }
//                if (isMode != POINT_MODE)
//                    addPointMarkerSession(it1)
            }
        }

        binding.btnDoneMarking.setOnClickListener {
            doneClicked = true
            binding.ivMarker.gone()
            binding.btnActionContainer.toggle()
            binding.addShapeMenuContainer.show()
            binding.btnToggleListenRtkEvent.gone()
            binding.btnTogglePolygonPoint.gone()
            circleMarkers.forEach { circle -> circle.remove() }
            circleMarkers.clear()
            if (isMode == POLYLINE_MODE)
                addPolylineAreaToDb(pointMarkerSession.toSet())
            else if (isMode == POLYGON_MODE && pointMarkerSession.size > 2) {
                addAreaToDb(pointMarkerSession.toSet())
                polylineDisplay?.points = listOf()
            } else {
                polylineDisplay?.points = listOf()
                polygonDisplay?.points = listOf(map?.cameraPosition?.target)
            }
            addedDistanceLabel.forEach { marker -> marker.remove() }
            addedDistanceLabel.clear()
            pointMarkerSession.clear()
            lastTwoMarker.clear()
            addSPenMode = false
            editing = false
        }

        binding.btnActivateShape.setOnClickListener {
            activatingShape = true
            selectedPolygon.lastOrNull()?.points?.toSet()?.let { it1 -> addAreaToDb(it1) }
            backToNormal()
        }

        binding.btnAddPointInBetween.setOnClickListener {
            val tmpPolygon = selectedPolygon.firstOrNull() ?: return@setOnClickListener
            val first = selectedCircle.firstOrNull() ?: return@setOnClickListener
            val last = selectedCircle.lastOrNull() ?: return@setOnClickListener
            val firstIndex = tmpPolygon.points.indexOf(first.position)
            val lastIndex = tmpPolygon.points.indexOf(last.position)
            val polygonPointsSize = tmpPolygon.points.toSet().size
            val firstPlusLast = firstIndex + lastIndex
            val isLastAndFirst = firstPlusLast == polygonPointsSize - 1
//            val isMarkerSideBySide = firstIndex.minus(lastIndex).absoluteValue == 1

//            if (isMarkerSideBySide || isLastAndFirst) {
//                first.apply {
                    val lastCircleCenter = last.position
//                    val bearing = position.computeBearing(last.position)
//                    val newDestCoordinate =
//                        position.getNewCoordinateWith(
//                            bearing.toPositiveDegree(),
//                            (position.distanceTo(lastCircleCenter) / 2).toDouble()
//                        )
//                    val p = firstSelectedPolygon?.points
//                    p?.apply {
//                        var insertPosition = 0
//                        if (isMarkerSideBySide) {
//                            insertPosition = if (firstIndex < lastIndex) firstIndex + 1 else lastIndex + 1
//                        } else if (isLastAndFirst) insertPosition = size
//                        add(insertPosition, newDestCoordinate)
//                        firstSelectedPolygon?.points = this.toSet().toList()
//                        updatePointsPolygonOnDb(tmpPolygon)
//                        onPolygonClick(firstSelectedPolygon)
//                        onPolygonClick(tmpPolygon)
//                    }
//                }
//            }
//            else toast("Silahkan pilih marker yang berjarak tidak lebih dari 1").show()
        }

        binding.btnDeleteActiveShape.setOnClickListener {
            if (selectedPointWithStatus.size > 0) {
                selectedPointWithStatus.forEach { marker ->
                    pointTakenWithGnssStatusses.firstOrNull {
                        GeoPoint(
                            marker.position.latitude,
                            marker.position.longitude
                        ) == it.data.point
                    }?.apply {
                        Log.i(localClassName, "Deleting selected point with status ${data.data()}")
                        doc.delete()
                        marker.remove()
                    }
                }
                selectedPointWithStatus.clear()
            }
//            if (selectedCircle.size > 0) {
//                var whichPolygon: Polygon? = null
//                selectedCircle.forEach { circle ->
//                    circle.remove()
//                    selectedPolygon.find { polygon -> polygon.points.contains(circle.position) }?.apply {
//                        whichPolygon = this
//                        polygonUndoStack.add(PolygonUndoSnapp(this, points) { polygon ->
//                            updatePointsPolygonOnDb(polygon)
//                            onPolygonClick(polygon)
//                            onPolygonClick(polygon)
//                        })
//                        points = points.filter { latLng -> latLng != circle.position }
//                        updatePointsPolygonOnDb(this)
//                    }
//                }
//                onPolygonClick(whichPolygon)
//                onPolygonClick(whichPolygon)
//                selectedCircle.clear()
//                editMeasurementContainer.collapse()
//            } else {
//                if (selectedPolygon.size > 0)
//                    alert {
//                        message = getString(R.string.area_delete_confirmation)
//                        title = getString(R.string.confirm)
//                        okButton {
//                            selectedPolygon.forEach { polygon ->
//                                deletePolygonFromDb(polygon)
//                                polygonUndoStack.add(PolygonUndoDelete(polygon) { polygon1 -> addAreaToDb(polygon1.points.toSet()) })
//                            }
//                            selectedPolygon.clear()
//                            backToNormal()
//                        }
//                        negativeButton(getString(R.string.cancel)) { dialogInterface1 -> dialogInterface1.dismiss() }
//                    }.show()
//                if (selectedPolyline.size > 0) {
//                    alert {
//                        message = getString(R.string.area_delete_confirmation)
//                        title = getString(R.string.confirm)
//                        okButton {
//                            selectedPolyline.forEach { polyline ->
//                                deletePolylineFromDb(polyline)
//                            }
//                            selectedPolyline.clear()
//                            backToNormal()
//                        }
//                        negativeButton(getString(R.string.cancel)) { dialogInterface1 -> dialogInterface1.dismiss() }
//                    }.show()
//                }
//            }
//
//            toggleDeleteButtonText()
//            if (selectedPolygon.size <= 0 && selectedPolyline.size <= 0)
//                backToNormal()
        }

        binding.btnShapeDetail.setOnClickListener {
//            if (isPolyline) {
//                val polygon =
//                    localPolylineDbReference.withIndex()
//                        .find { decorator -> decorator.value.polyline == selectedPolyline.lastOrNull() }
//                val areaId = polygon?.value?.documentReference?.id
//                startActivity<LineFormActivity>(
//                    AREA_ID to areaId,
//                    Workspace.INTENT to workspace
//                )
//            } else {
//                val polygon =
//                    localPolygonDbReference.withIndex()
//                        .find { decorator -> decorator.value.polygon == selectedPolygon.lastOrNull() }
//                val areaId = polygon?.value?.documentReference?.id
//                val detailMenus =
//                    listOf(
//                        "Data Fisik",
//                        "Data Yuridis",
//                        "Data Yuridis II",
//                        "Data IP4T",
//                        "Foto",
//                        "Tanda Tangan",
//                        "Surat Pernyataan",
//                        "Import Data dari Excel",
//                        "Tetangga Berbatasan",
//                        "Pendataan Terpadu Rumah Tangga",
//                        "SATGAS PENGTAN"
//                    )
//                this.dialogInterface = alert {
//                    customView {
//                        listView {
//                            adapter = ArrayAdapter<String>(context, simple_list_item_1, detailMenus)
//                            onItemClickListener =
//                                AdapterView.OnItemClickListener { _, _, position, _ ->
//                                    when (position) {
//                                        0 -> startActivity<DelinasiFormActivity>(
//                                            AREA_ID to areaId,
//                                            Workspace.INTENT to workspace,
//                                            DATA_SIZE to polygon?.index?.plus(1),
//                                            AREA to computedArea
//                                        )
//                                        1 -> {
//                                            menuYuridisSelect(areaId!!, polygon?.index?.plus(1)!!, workspace.name)
//                                        }
//
//                                        2 -> startActivity<Yuridis2FormActivity>(
//                                            AREA_ID to areaId,
//                                            Workspace.INTENT to workspace,
//                                            DATA_SIZE to polygon?.index?.plus(1)
//                                        )
//                                        3 -> startActivity<ip4tFormActivity>(
//                                            AREA_ID to areaId,
//                                            Workspace.INTENT to workspace,
//                                            DATA_SIZE to polygon?.index?.plus(1)
//                                        )
//                                        4 -> startActivity<UploadImageFormActivity>(AREA_ID to areaId)
//                                        5 -> startActivity<SignatureFormActivity>(AREA_ID to areaId)
//                                        6 -> prepareAndCreateSuratPernyataan(areaId.toString().noNull())
//                                        7 -> startActivity<ExcelDataChooser>(
//                                            AREA_ID to areaId,
//                                            Workspace.INTENT to workspace,
//                                            AREA to computedArea
//                                        )
//                                        8 -> {
//                                            tetanggaBerbatasanSelected = areaId.toString()
//                                            selectMenuTetangga()
//                                        }
//                                        9 -> startActivity<BapedaFormActivity>(
//                                            AREA_ID to areaId,
//                                            Workspace.INTENT to workspace,
//                                            DATA_SIZE to polygon?.index?.plus(1),
//                                            AREA to computedArea
//                                        )
//                                        10 -> startActivity<PengtanFormActivity>(
//                                            AREA_ID to areaId,
//                                            Workspace.INTENT to workspace,
//                                            DATA_SIZE to polygon?.index?.plus(1),
//                                            AREA to computedArea
//                                        )
//                                    }
//                                    this@MainActivity.dialogInterface?.dismiss()
//                                }
//                        }
//                    }
//                    cancelButton { }
//                }.show()
//            }
        }

        binding.btnChangeDistance.setOnClickListener {
//            if (etPointDistance.text.trim().isEmpty()) return@setOnClickListener
//            selectedCircle.firstOrNull()?.apply {
//                val lastCircle = selectedCircle.lastOrNull() ?: return@setOnClickListener
//                val destinationPosition = lastCircle.position
//                val bearing = position.computeBearing(lastCircle.position)
//                val distance = etPointDistance.text.toString().toDouble()
//                var newDestCoordinate = position.getNewCoordinateWith(bearing.toPositiveDegree(), distance)
//                lastCircle.position = newDestCoordinate
//
//                changedDistanceCircle.add(CircleFromLatLng(position, newDestCoordinate))
//
//                if (changedDistanceCircle.size > 1) {
//                    val firstCircle = changedDistanceCircle.firstOrNull()?.toGeometryCircle()
//                    val secondCircle = changedDistanceCircle.lastOrNull()?.toGeometryCircle()
//                    CircleCircleIntersection(firstCircle, secondCircle)
//                        .intersectionPoints.forEach { vector2 ->
//                            val referenceUtm = position.toUtm()
//                            val newLocation =
//                                UTM.valueOf(
//                                    referenceUtm.longitudeZone(),
//                                    referenceUtm.latitudeZone(),
//                                    vector2.x,
//                                    vector2.y,
//                                    METRE
//                                )
//                                    .toLatLng()
//                            // use threshold tollerants
//                            // use new location only when it's distance to the previous location is less then 1m
//                            if (destinationPosition.distanceTo(newLocation) < 1) {
//                                newDestCoordinate = newLocation
//                                lastCircle.position = newLocation
//                            }
//                        }
//                    changedDistanceCircle.remove(changedDistanceCircle.firstOrNull())
//                }
//
//                firstSelectedPolygon?.apply {
//                    polygonUndoStack.add(PolygonUndoSnapp(this, points) { polygon ->
//                        updatePointsPolygonOnDb(
//                            polygon
//                        )
//                    })
//                    points =
//                        points.map { latLng -> if (latLng == destinationPosition) newDestCoordinate else latLng }
//                    updatePointsPolygonOnDb(this)
//                }
//
//                if (selectedPolygon.size > 1) {
//                    val first = selectedPolygon.firstOrNull()
//                    val second = selectedPolygon.lastOrNull()
//
//                    onPolygonClick(selectedPolygon.firstOrNull())
//                    onPolygonClick(selectedPolygon.lastOrNull())
//
//                    onPolygonClick(first)
//                    onPolygonClick(second)
//                } else {
//                    firstSelectedPolygon.apply {
//                        onPolygonClick(this)
//                        onPolygonClick(this)
//                    }
//                }
//            }
        }

        binding.btnUndoLastPoint.setOnClickListener {
            circleMarkers.lastOrNull()?.apply {
                remove()
                circleMarkers.remove(this)
            }
            pointMarkerSession.lastOrNull()?.apply {
                pointMarkerSession.remove(this)
            }
            if (pointMarkerSession.isNotEmpty()) {
                polygonDisplay?.points = pointMarkerSession
                polylineDisplay?.points = pointMarkerSession
            }
        }
    }

    private fun menuYuridisSelect(areaId: String, dataSize: Int, worksapceName: String) {

        Collections.getUserAreaDetailYuridisPTSLCollections(currentUser()?.email, areaId).get()
            .addOnSuccessListener { result ->
                var typeDocExist: MutableList<String> = mutableListOf()
                result.forEach { doc ->
                    typeDocExist.add(doc.id)
                }

//                val typeDoc =
//                    mutableListOf(
////                        PERORANGAN,
////                        PERSIL,
////                        ALAS_HAK,
////                        BPHTB,
////                        PPH,
////                        PBB,
////                        AKTA_JUAL_BELI,
////                        AKTA_WAKAF,
////                        DOC_TYPE
//                    )

                val detailMenus: MutableList<String> = mutableListOf()

                val menu = listOf(
                    "Fotocopy KTP / Identitas Pemohon - (%s %s)",
                    "Persil - (%s %s)",
                    "Bukti Alas Hak - (%s %s)",
                    "Fotocopy Bea Perolehan Hak Tanah dan Bangunan - (%s %s)",
                    "Fotocopy Surat Setoran Pajak/PPH",
                    "Fotocopy Pajak Bumi dan Bangunan",
                    "Akta Jual Beli",
                    "Akta Ikrar Wakaf",
                    "Upload Dokument Pendukung"
                )

//                val yuridisHelper = BerkasYuridisHelper()
//                typeDoc.forEachIndexed { index, value ->
//                    var documentNotComplete = "X"
//                    var documentImageNotComplete = "X"
//
//                    if (index < 4) {
//                        if (typeDocExist.contains("yuridis_ptsl_$value") || index == 1) {
//                            documentNotComplete = "√"
//                        }
//                        if (yuridisHelper.documentIsExist(areaId, value))
//                            documentImageNotComplete = "√"
//                        detailMenus.add(menu[index].format(documentNotComplete, documentImageNotComplete))
//                    } else
//                        detailMenus.add(menu[index])
//                }

                var dialogInterfaceMenu: DialogInterface? = null

//                dialogInterfaceMenu = alert {
//                    customView {
//                        listView {
//                            adapter = ArrayAdapter<String>(context, simple_list_item_1, detailMenus)
//                            onItemClickListener =
//                                AdapterView.OnItemClickListener { _, _, position, _ ->
//
//                                    when (position) {
//                                        1 -> {
//                                            toast("Persil tidak memiliki inputan data")
//                                        }
//                                        8 -> {
//                                            menuYuridisSelectDocument(areaId, dataSize, worksapceName)
//                                        }
//                                        else -> {
//                                            startActivity<YuridisPTSLFormActivity>(
//                                                AREA_ID to areaId,
//                                                Workspace.INTENT to workspace,
//                                                DATA_SIZE to dataSize,
//                                                AREA to position
//                                            )
//                                        }
//                                    }
//                                }
//                            dialogInterfaceMenu?.dismiss()
//                        }
//                    }
//                    cancelButton { }
//                }.show()
            }


    }

    private fun menuYuridisSelectDocument(areaId: String, dataSize: Int, worksapceName: String) {

//        val detailMenus =
//            listOf(
//                "Fotocopy KTP / Identitas Pemohon",
//                "Persil",
//                "Bukti Alas Hak",
//                "Fotocopy Bea Perolehan Hak Tanah dan Bangunan",
//                "Fotocopy Surat Setoran Pajak/PPH",
//                "Fotocopy Pajak Bumi dan Bangunan",
//                "Akta Jual Beli",
//                "Akta Ikrar Wakaf"
//            )
//        var dialogInterfaceMenu: DialogInterface? = null
//
//        val typeDoc = mutableMapOf(
//            0 to PERORANGAN,
//            1 to GeneratePdfContent.PERSIL,
//            2 to GeneratePdfContent.ALAS_HAK,
//            3 to GeneratePdfContent.BPHTB,
//            4 to GeneratePdfContent.PPH,
//            5 to GeneratePdfContent.PBB,
//            6 to GeneratePdfContent.AKTA_JUAL_BELI,
//            7 to GeneratePdfContent.AKTA_WAKAF
//        )
//        dialogInterfaceMenu = alert {
//            customView {
//                listView {
//                    adapter = ArrayAdapter<String>(context, simple_list_item_1, detailMenus)
//                    onItemClickListener =
//                        AdapterView.OnItemClickListener { _, _, position, _ ->
//                            startActivity<smartgis.project.app.smartgis.forms.yuridis_ptsl.UploadImageFormActivity>(
//                                AREA_ID to areaId,
//                                DOC_TYPE to typeDoc[position],
//                                FORM to detailMenus[position],
//                                WORSKPACE_NAME to worksapceName
//                            )
//                        }
//                    dialogInterfaceMenu?.dismiss()
//                }
//            }
//            cancelButton { }
//        }.show()
    }

    private fun listenConnectionState() {
//        connectionStateDisposable?.dispose()
//        connectionStateDisposable = onConnectionStateChanged {
//            state = it.state()
//            if (it.state() == NetworkInfo.State.DISCONNECTED && isCreatingSuratPernyataan) {
//                globalIndefiniteSnackbar?.dismiss()
//                rootLayout.longSnackbar("Pembuatan surat pernyataan gagal, sambungan internet terputus. Silahkan ulangi kembali ")
//            }
//        }
    }

    private fun prepareAndCreateSuratPernyataan(areaId: String) {
//        if (state == NetworkInfo.State.DISCONNECTED) {
//            rootLayout.longSnackbar(getString(R.string.no_internet_connection))
//            return
//        }
//        isCreatingSuratPernyataan = true
//        globalIndefiniteSnackbar =
//            rootLayout.indefiniteSnackbar("Sedang menyiapkan berkas surat pernyataan")
//        val data = mutableMapOf<String?, Any?>()
//        Collections.getUserAreaDetailDelinasi(currentUser()?.email, areaId).get()
//            .addOnSuccessListener { snapshot ->
//                snapshot.data?.let { it1 -> data.putAll(it1) }
//                Collections.getUserAreaDetailYuridis(currentUser()?.email, areaId).get()
//                    .addOnSuccessListener { documentSnapshot ->
//                        documentSnapshot.data?.let { it1 -> data.putAll(it1) }
//                        Collections.getWorkspaceYuridisDataSaksiPertama(
//                            workspace?.id,
//                            currentUser()?.email.toString()
//                        ).get()
//                            .addOnSuccessListener { referenceSaksiPertama ->
//                                referenceSaksiPertama.data?.let { it1 -> data.putAll(it1) }
//                                referenceSaksiPertama.data?.get(BaseSignatureFormContainer.PATH).toString().apply {
//                                    if (this.noNull().isEmpty()) {
//                                        globalIndefiniteSnackbar?.dismiss()
//                                        rootLayout.longSnackbar("Surat pernyataan tidak bisa dibuat. TTD Saksi Pertama belum ada")
//                                    } else {
//                                        referenceSaksiPertama.data?.get(TTD_SAKSI1_URL).toString().let { url ->
//                                            if (!url.noNull().isEmpty()) createSuratPernyataan(data)
//                                            else {
//                                                Storages.getFile(this)
//                                                    .downloadUrl
//                                                    .addOnSuccessListener { uri ->
//                                                        referenceSaksiPertama.reference.set(
//                                                            mapOf(TTD_SAKSI1_URL to "$uri"),
//                                                            SetOptions.merge()
//                                                        )
//                                                        data[TTD_SAKSI1_URL] = "$uri"
//                                                        createSuratPernyataan(data)
//                                                    }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        Collections.getWorkspaceYuridisDataSaksiKedua(
//                            workspace.id,
//                            currentUser()?.email.toString()
//                        ).get()
//                            .addOnSuccessListener { referenceSaksiKedua ->
//                                referenceSaksiKedua.data?.let { it1 -> data.putAll(it1) }
//                                referenceSaksiKedua.data?.get(BaseSignatureFormContainer.PATH).toString().apply {
//                                    if (this.noNull().isEmpty()) {
//                                        globalIndefiniteSnackbar?.dismiss()
//                                        rootLayout.longSnackbar("Surat pernyataan tidak bisa dibuat. TTD Saksi Kedua belum ada")
//                                    } else {
//                                        referenceSaksiKedua.data?.get(TTD_SAKSI2_URL).toString().let { url ->
//                                            if (!url.noNull().isEmpty()) createSuratPernyataan(data)
//                                            else {
//                                                Storages.getFile(this)
//                                                    .downloadUrl
//                                                    .addOnSuccessListener { uri ->
//                                                        referenceSaksiKedua.reference.set(
//                                                            mapOf(TTD_SAKSI2_URL to "$uri"),
//                                                            SetOptions.merge()
//                                                        )
//                                                        data[TTD_SAKSI2_URL] = "$uri"
//                                                        createSuratPernyataan(data)
//                                                    }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        Collections.getUserAreaSignature(currentUser()?.email.toString(), areaId).get()
//                            .addOnSuccessListener { referencePemilikData ->
//                                referencePemilikData.data?.let { it1 -> data.putAll(it1) }
//                                referencePemilikData.data?.get(BaseSignatureFormContainer.PATH).toString().apply {
//                                    if (this.noNull().isEmpty()) {
//                                        globalIndefiniteSnackbar?.dismiss()
//                                        rootLayout.longSnackbar("Surat pernyataan tidak bisa dibuat. TTD Pemilik belum ada")
//                                    } else {
//                                        referencePemilikData.data?.get(TTD_PEMILIK_URL).toString().let { url ->
//                                            if (!url.noNull().isEmpty()) createSuratPernyataan(data)
//                                            else {
//                                                Storages.getFile(this)
//                                                    .downloadUrl
//                                                    .addOnSuccessListener { uri ->
//                                                        referencePemilikData.reference.set(
//                                                            mapOf(TTD_PEMILIK_URL to "$uri"),
//                                                            SetOptions.merge()
//                                                        )
//                                                        data[TTD_PEMILIK_URL] = "$uri"
//                                                        createSuratPernyataan(data)
//                                                    }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                    }
//            }
    }

    private fun createSuratPernyataan(data: MutableMap<String?, Any?>) {
//        if (data.containsKey(TTD_PEMILIK_URL) && data.containsKey(TTD_SAKSI1_URL) && data.containsKey(
//                TTD_SAKSI2_URL
//            )
//        ) {
//            globalIndefiniteSnackbar?.dismiss()
//            val params = RequestParams()
//            data.forEach { entry -> params.put(entry.key, entry.value?.toString()) }
//            globalIndefiniteSnackbar =
//                rootLayout.indefiniteSnackbar(getString(R.string.creating_surat_pernyataan))
//            Http.client.post("https://smartptsl.com/apps/delinasi/surat_yuridis_2",
//                params,
//                DownloadFile(this@MainActivity) { file ->
//                    val res =
//                        file.copyTo(
//                            File("$BASE_STORAGE_PATH/Dokumen/${workspace?.name}/$nub.pdf"),
//                            true
//                        )
//                    if (res.exists()) {
//                        globalIndefiniteSnackbar?.dismiss()
//                        rootLayout.longSnackbar("Surat pernyataan telah disimpan pada folder ${res.path}")
//                            .show()
//                        isCreatingSuratPernyataan = false
//                    }
//                })
//        }
    }

    private fun deletePolygonFromDb(polygon: Polygon) {
//        localPolygonDbReference.find { decorator -> decorator.polygon == polygon }?.apply {
//            documentReference.delete()
//                .addOnSuccessListener { Log.i(TAG, "Doc ${documentReference.id} deleted!") }
//            localPolygonDbReference.remove(this)
//        }
//        polygon.remove()
//
//        selectedPolygonCircle.filter { circle -> circle.position in polygon.points }
//            .forEach { circle ->
//                circle.remove()
//                selectedPolygonCircle.remove(circle)
//            }
    }


    private fun deletePolylineFromDb(polyline: Polyline) {

        localPolylineDbReference.find { decorator -> decorator.polyline == polyline }?.apply {
            documentReference.delete()
                .addOnSuccessListener { Log.i(TAG, "Doc ${documentReference.id} deleted!") }
            localPolylineDbReference.remove(this)
        }
        polyline.remove()

        selectedPolygonCircle.filter { circle -> circle.position in polyline.points }
            .forEach { circle ->
                circle.remove()
                selectedPolygonCircle.remove(circle)
            }
    }

    private fun addAreaToDb(
        map: Set<LatLng>, isImportedShp: ShapeImportedDecorator? = null, active: Boolean = false
        /*use <SET> data structure since we don't need any same point in the polygon*/
    ) {
        //TO DO increment size here
        Collections.workspaceCountTracker().document(workspace?.id.toString())
            .set(mapOf("counter_" to workspaceIncrementCounter + 1), SetOptions.merge())
        Collections.getUserDrawnAreas(currentUser()?.email)
            .add(
                timeStamp(
                    mutableMapOf(
                        "points" to map.map { GeoPoint(it.latitude, it.longitude) },
                        "workspace_id" to workspace.id
                    )
                )
            )
            .addOnSuccessListener { reference ->
                Log.i(localClassName, "Success ${reference.id}")
                if (active) {
//                    saveImportSHPAttribut(isImportedShp, reference.id)
                }
            }
            .addOnFailureListener { Log.i(localClassName, "failed ${it.localizedMessage}") }

    }

    private fun addPolylineAreaToDb(
        map: Set<LatLng>
        /*use <SET> data structure since we don't need any same point in the polygon*/
    ) {
        //TO DO increment size here
        Collections.workspaceCountTracker().document(workspace?.id.toString())
            .set(mapOf("counter_" to workspaceIncrementCounter + 1), SetOptions.merge())
        Collections.getUserDrawnPolyAreas(currentUser()?.email)
            .add(
                timeStamp(
                    mutableMapOf(
                        "points" to map.map { GeoPoint(it.latitude, it.longitude) },
                        "workspace_id" to workspace.id
                    )
                )
            )
            .addOnSuccessListener { reference ->
                Log.i(localClassName, "Success ${reference.id}")
            }
            .addOnFailureListener { Log.i(localClassName, "failed ${it.localizedMessage}") }

    }


    private fun createPolygon(points: Set<LatLng>): Polygon? {
        return map?.addPolygon(
            PolygonOptions()
                .add(*points.toTypedArray())
                .fillColor(rColor(R.color.yellow_transparent))
                .strokeWidth(NORMAL_STROKE_WIDTH)
                .strokeColor(rColor(R.color.yellow))
                .clickable(true)
                .zIndex(2f)
        )
    }

    private fun createPolyline(points: Set<LatLng>): Polyline? {
        return map?.addPolyline(
            PolylineOptions()
                .add(*points.toTypedArray())
                .clickable(true)
                .color(rColor(R.color.yellow))
                .width(5f)
                .zIndex(2f)
        )
    }

    private fun createShpPolygon(points: List<LatLng>): Polygon? {
        return map?.addPolygon(
            PolygonOptions()
                .add(*points.toTypedArray())
                .fillColor(rColor(R.color.orange_transparent))
                .strokeWidth(NORMAL_STROKE_WIDTH)
                .strokeColor(rColor(R.color.orange))
                .clickable(true)
                .zIndex(1f)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    @SuppressLint("MissingPermission")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
//            R.id.undo -> performUndo()
            R.id.mbtile -> {
//                if (askStorageForPermissions())
//                    showMbtilesChooserDialog()
            }
            R.id.importExcel -> {
//                if (askStorageForPermissions())
//                    showExcelChooserDialog()
            }
            R.id.importShp -> {
//                if (askStorageForPermissions())
//                    loadFileSHP()
            }
            R.id.importGeoJSON -> {
//                if (askStorageForPermissions())
//                    loadFileGeoJson()
            }
            R.id.exportToShp -> {
//                if (askStorageForPermissions())
//                    startActivity<ExportAreaDetailToShp>(Workspace.INTENT to workspace)
            }
            R.id.exportBidangBapenda -> {
//                if (askStorageForPermissions())
//                    startActivity<ExportAreaBapendaToShp>(Workspace.INTENT to workspace)
            }
            R.id.exportBidangIP4T -> {
//                if (askStorageForPermissions())
//                    startActivity<ExportIP4TToShp>(Workspace.INTENT to workspace)
            }
            R.id.exportLineToShp -> {
//                if (askStorageForPermissions())
//                    startActivity<ExportPolylineAreaDetailToShp>(Workspace.INTENT to workspace)
            }
            R.id.exportToGeo -> {
//                if (askStorageForPermissions())
//                    startActivity<ExportAreaDetailToGeoJson>(Workspace.INTENT to workspace)
            }
            R.id.analisisFormSatgas -> {
//                viewModel.checkSatgasPangtan = true
//                analisisSatgas()
            }
            R.id.exportToCsv -> {
//                if (askStorageForPermissions())
//                    startActivity<ExportAreaDetailToCsv>(Workspace.INTENT to workspace)
            }
            R.id.exportPointsToShp -> {
//                if (askStorageForPermissions())
//                    startActivity<ExportPointDetailToShp>(Workspace.INTENT to workspace)
            }
//            R.id.search -> startActivityForResult<HandleNameNIKSearch>(
//                SEARCH_REQUEST_CODE,
//                Workspace.INTENT to workspace
//            )
            R.id.exportStatusRtk -> {
//                if (askStorageForPermissions())
//                    if (!isProFeaturePurchased)
//                        startActivity<FreeExportReportRtkStatus>(Workspace.INTENT to workspace)
//                    else
//                        startActivity<ProExportReportRtkStatus>(Workspace.INTENT to workspace)
            }
            R.id.checkYuridis -> {

//                if (!isProFeaturePurchased)
//                    checkYuridisIsComplete()

            }
//            R.id.petaKerja -> showWMSKantah()


//            R.id.mapType -> showMapTypeChooserDialog()
//            R.id.show_wws -> showWms()
            R.id.toggleLocation -> map?.apply {
                isMyLocationEnabled = !isMyLocationEnabled
            }
//            R.id.saksi -> startActivity<WorkspaceForms>(Workspace.INTENT to workspace.id)
//            R.id.connectRtk -> askBluetoothForPermissions()
//            R.id.ntripService -> {
//                //startActivity<NTRIPActivity>()
//                startService(
//                    Intent(
//                        this@MainActivity,
//                        smartgis.project.app.smartgis.ntrip.service.NTRIPService::class.java
//                    )
//                )
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performUndo() {
        if (polygonUndoStack.isNotEmpty())
            polygonUndoStack.removeAt(polygonUndoStack.size - 1).apply { act() }
    }


    private fun showMbtilesChooserDialog() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        try {
            startActivityForResult(intent, SELECT_MBTILE)
        } catch (e: ActivityNotFoundException) {
        }
    }


    private fun showExcelChooserDialog() {
//        ChooserDialog(this)
//            .withResources(R.string.add_excel, R.string.choose, R.string.cancel)
//            .withStartFile(appPreference().getString(EXCEL_FOLDER, BASE_STORAGE_PATH))
//            .withFilter(false, getString(R.string.excel_extension))
//            .withChosenListener { _, file ->
//                appPreference().edit().putString(EXCEL_FOLDER, file.parent).apply()
//                appPreference().edit().putString(EXCEL_FILE, file.path).apply()
//            }
//            .build()
//            .show()
    }

    private fun showMapTypeChooserDialog() {
//        dialogInterface = alert {
//            title = getString(R.string.change_map_type)
//            customView {
//                listView {
//                    adapter = ArrayAdapter<String>(
//                        this@MainActivity,
//                        simple_list_item_1,
//                        listOf("Satellite", "Road Map")
//                    )
//                    onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//                        when (position) {
//                            0 -> map?.mapType = MAP_TYPE_SATELLITE
//                            1 -> map?.mapType = MAP_TYPE_NORMAL
//                        }
//                        dialogInterface?.dismiss()
//                    }
//                }
//            }
//        }.show()
    }

    private fun showWms() {
//        dialogInterface = alert {
//            customView {
//                listView {
//                    adapter = ArrayAdapter(
//                        this@MainActivity,
//                        simple_list_item_1,
//                        listOf("Tampilkan Peta", "Pengaturan WMS")
//                    )
//                    onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//                        when (position) {
//                            0 -> {
//                                wmsShowtoMap()
//                                dialogInterface?.dismiss()
//                            }
//                            1 -> startActivity<WmsActivity>(AREA_ID to "0")
//                        }
//                    }
//                }
//            }
//        }.show()
    }

    private fun wmsShowtoMap() {
        if (!appPreference().getBoolean("wms", false)) {
            appPreference().edit().putBoolean("wms", true).apply()
            Collections.getUserWms(currentUser()?.email).get().addOnSuccessListener {
//                val wmsUrl = it.get(AddWms.URL.prefix(AddWms.PREFIX)).toString().noNull()
//                val wmsUser = it.get(AddWms.USER.prefix(AddWms.PREFIX)).toString().noNull()
//                val wmsPassword = it.get(AddWms.PASSWORD.prefix(AddWms.PREFIX)).toString().noNull()
//                val wmsVersion = it.get(AddWms.VERSION.prefix(AddWms.PREFIX)).toString().noNull()
//                val wmsLayer = it.get(AddWms.LAYER.prefix(AddWms.PREFIX)).toString().noNull()
//                var wmsAuth: String = ""
//                if (wmsUser.isNotEmpty())
//                    wmsAuth = Credentials.basic(wmsUser, wmsPassword)
//                val wmsTileProvider: TileProvider =
//                    TileProviderFactory.getOsgeoWmsTileProvider(wmsUrl, wmsAuth, wmsLayer, wmsVersion)
//                map?.addTileOverlay(TileOverlayOptions().tileProvider(wmsTileProvider))
            }
        } else {
            appPreference().edit().putBoolean("wms", true).apply()
        }
    }

    private fun showWMSKantah() {
        fun loadWms() {
//            Collections.getUserLocation(currentUser()?.email).document("kantah").get()
//                .addOnSuccessListener {
//                    kantah = it.data?.get("id").toString()
//                    viewModel.isWMSActive = true
//                    val type = appPreference().getInt("type_wms", 1)
//
//                    if(tileOverlays.isEmpty()) {
//                        wmsTileProvider1 = KantahTileProviderFactory.getOsgeoWmsTileProvider(kantah)
//                        val over1 = TileOverlayOptions().tileProvider(wmsTileProvider1)
//                        wmsTileProvider2 = KantahDroneTileProviderFactory.getOsgeoWmsTileProvider(kantah)
//                        val over2 = TileOverlayOptions().tileProvider(wmsTileProvider2)
//                        wmsTileProvider3 = KantahUAVTileProviderFactory.getOsgeoWmsTileProvider(kantah)
//                        val over3 = TileOverlayOptions().tileProvider(wmsTileProvider3)
//                        map?.let { it1 -> tileOverlays.add(it1.addTileOverlay(over1)) }
//                        map?.let { it1 -> tileOverlays.add(it1.addTileOverlay(over2)) }
//                        map?.let { it1 -> tileOverlays.add(it1.addTileOverlay(over3)) }
//                    }
//
//                    when (type) {
//                        0 -> {
//                            tileOverlays[0].isVisible = true
//                            tileOverlays[1].isVisible = false
//                            tileOverlays[2].isVisible = false
//                        }
//
//                        1 -> {
//                            tileOverlays[0].isVisible = false
//                            tileOverlays[1].isVisible = true
//                            tileOverlays[2].isVisible = false
//                        }
//
//                        2 -> {
//                            tileOverlays[0].isVisible = false
//                            tileOverlays[1].isVisible = false
//                            tileOverlays[2].isVisible = true
//                        }
//                    }
//                }.addOnFailureListener {
//                    longToast("Kantah tidak ditemukan")
//                }
        }

//        dialogInterface = alert {
//            customView {
//                listView {
//                    adapter = ArrayAdapter(
//                        this@MainActivity,
//                        simple_list_item_1,
//                        listOf("Index", "Foto Drone/UAV", "Peta Kerja")
//                    )
//                    onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//                        when (position) {
//                            0 -> {
//                                appPreference().edit().putInt("type_wms", 1).apply()
//                                loadWms()
//                                dialogInterface?.dismiss()
//                            }
//
//                            1 -> {
//                                appPreference().edit().putInt("type_wms", 2).apply()
//                                loadWms()
//                                dialogInterface?.dismiss()
//                            }
//
//                            2 -> {
//                                appPreference().edit().putInt("type_wms", 0).apply()
//                                loadWms()
//                                dialogInterface?.dismiss()
//                            }
//                        }
//                    }
//                }
//            }
//        }.show()

    }

    private fun load(from: Int, to: Int, features: JSONArray) {
//        val size = features.length()
//        var to = to
//        var active = features[size - 1].toString()
//        if (active == "1") {
//            to -= 1
//        }
//
//        Observable.range(from, to)
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(Schedulers.computation())
//            .map { it1 ->
//                val points = mutableListOf<LatLng>()
//                val geometry = features.getJSONObject(it1).get("geometry").toString()
//                if (geometry != "null") {
//                    val geometryObject = JSONObject(geometry)
//                    val data = geometryObject
//                        .getJSONArray("coordinates")
//                        .getJSONArray(0)
//                    pointMarkerSession.clear()
//                    for (index in 0 until data.length()) {
//                        points.add(
//                            LatLng(
//                                data.getJSONArray(index).getDouble(1),
//                                data.getJSONArray(index).getDouble(0)
//                            )
//                        )
//                    }
//                }
//                ImportedHolder(features.getJSONObject(it1).getJSONObject("properties"), points)
//            }
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ importedHolder ->
//                if (active.equals("1")) {
//                    val mapData =
//                        Gson().fromJson<Map<String, Any>>(
//                            importedHolder.properties.toString(),
//                            HashMap::class.java
//                        )
//                    val iskedImportedShp =
//                        ShapeImportedDecorator(mapData, createShpPolygon(importedHolder.points))
//                    addAreaToDb(importedHolder.points.toSet(), iskedImportedShp, true)
//                } else {
//                    val mapData =
//                        Gson().fromJson<Map<String, Any>>(
//                            importedHolder.properties.toString(),
//                            HashMap::class.java
//                        )
//                    shpImported.add(ShapeImportedDecorator(mapData, createShpPolygon(importedHolder.points)))
//                    map?.apply {
//                        moveCamera(
//                            CameraUpdateFactory.newLatLngZoom(
//                                importedHolder.points[0],
//                                cameraPosition.zoom
//                            )
//                        )
//                    }
//                }
//            }, { Log.i(localClassName, it.localizedMessage) }, {}).isDisposed
    }

    private fun addPointMarkerSession(position: LatLng) {
//        map?.apply {
//            val number = circleMarkers.size + 1
//            circleMarkers.add(
//                addMarker(
//                    defaultMarker(number)
//                        .position(position)
//                )
//            )
//            pointMarkerSession.add(position)
//            if (isMode == POLYGON_MODE)
//                polygonDisplay?.points = pointMarkerSession
//            polylineDisplay?.points = pointMarkerSession
//            lastTwoMarker.add(position)
//            if (lastTwoMarker.size > 1) {
//                map?.apply {
//                    val origin = lastTwoMarker.firstOrNull() ?: return
//                    val distance = lastTwoMarker.lastOrNull() ?: return
//                    addedDistanceLabel.add(addMarker(generateLabelBetween(origin, distance)))
//                }
//                lastTwoMarker.remove(lastTwoMarker.firstOrNull())
//            }
//        }
    }

    private fun loadMbTiles(file: File) {
//        tileProvider = ExpandedMBTilesTileProvider(file, 256, 256)
//        tileOverlay = map?.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
//        try {
//            val db = SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY)
//            val cursor = db.rawQuery("SELECT value FROM metadata WHERE name='bounds';", null)
//            cursor.moveToFirst()
//            val coordinates = cursor.getString(0).split(",").map { it.toDouble() }.toList()
//            val p1 = LatLng(coordinates[1], coordinates[0])
//            val p2 = LatLng(coordinates[3], coordinates[2])
//            val bounds = LatLngBounds(p1, p2)
//            cursor.close()
//            db.close()
//            map?.apply {
//                moveCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                        bounds.center,
//                        cameraPosition.zoom
//                    )
//                )
//            }
//        } catch (e: Exception) {
//            rootLayout.longSnackbar(getString(R.string.cant_get_center_bounds_mbtiles))
//        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        polygonDisplay = map?.addPolygon(
            PolygonOptions()
                .fillColor(rColor(R.color.yellow_transparent))
                .strokeWidth(1f)
                .zIndex(1f)
                .add(map?.cameraPosition?.target)
        )
        polylineDisplay = map?.addPolyline(
            PolylineOptions().apply {
                color(Color.YELLOW)
                    .zIndex(1f)
                add(map?.cameraPosition?.target)
                width(width / 2)
            }
        )

//        map?.setOnPolygonClickListener(this)
//        map?.setOnMapClickListener(this)
        map?.setOnMarkerClickListener(this)
//        map?.setOnMarkerDragListener(this)
//        map?.setOnPolylineClickListener(this)
        map?.mapType = MAP_TYPE_SATELLITE
        map?.uiSettings?.isCompassEnabled = true

        val lat = -7.7827188
        val lng = 110.343225

        map?.apply { moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17f)) }
    }

    @SuppressLint("MissingPermission")
//    @AfterPermissionGranted(ASK_LOCATION_PERMISSIONS_REQUEST_CODE)
    private fun askLocationForPermissions() {
//        if (EasyPermissions.hasPermissions(this, *locationPermissions())) {
//            Log.i(localClassName, "location grated")
//            enableGPS()
//        } else {
//            // Permission is missing and must be requested.
//            AlertDialog.Builder(this).apply {
//                setPositiveButton("Tampilkan Dialog Permission") { _, _ ->
//                    requestPermissionsCompat(locationPermissions(), ASK_LOCATION_PERMISSIONS_REQUEST_CODE)
//                }
//                setNegativeButton("Kembali", null)
//                setMessage(R.string.why_need_location)
//                create()
//            }.show()
//        }
    }


    private fun askBluetoothForPermissions() {
//        if (EasyPermissions.hasPermissions(this, *bluetoothPermissions())) {
//            startActivity<BluetoothDevices>()
//        } else {
//            requestPermissionsCompat(bluetoothPermissions(), ASK_BLUETOOTH_PERMISSIONS_REQUEST_CODE)
//        }
    }

    private fun askStorageForPermissions(): Boolean {
        return  false;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            return true;
//        }
//        else if (!EasyPermissions.hasPermissions(this, *storagePermissions())) {
//            requestPermissionsCompat(storagePermissions(), ASK_STORAGE_PERMISSIONS_REQUEST_CODE)
//        }
//        return EasyPermissions.hasPermissions(this, *storagePermissions())
    }

    private fun locationPermissions() =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE
        )

    private fun storagePermissions() =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

    private fun bluetoothPermissions() =
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )


    override
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
//        when {
//            requestCode == ASK_LOCATION_PERMISSIONS_REQUEST_CODE -> {
//                enableGPS()
//            }
//            requestCode == ASK_BLUETOOTH_PERMISSIONS_REQUEST_CODE -> {
//                startActivity<BluetoothDevices>()
//            }
//            requestCode == ASK_STORAGE_PERMISSIONS_REQUEST_CODE &&
//                    checkSelfPermissionCompat(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ->
//                longToast(R.string.reproccess)
//        }
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                SELECT_MBTILE -> {
//                    data?.data?.let { uri ->
//                        val inputStream = this.contentResolver.openInputStream(uri)
//                        val outputFile = File.createTempFile("_shp_", ".mbtiles", this.cacheDir)
//                        val outputStream = FileOutputStream(outputFile)
//                        inputStream?.use {
//                            it.copyTo(outputStream)
//                        }
//                        loadMbTiles(outputFile)
//                    }
//
//                }
//
//                SEARCH_REQUEST_CODE -> {
//                    localPolygonDbReference.firstOrNull {
//                        it.documentReference.id == data?.getStringExtra(
//                            HandleNameNIKSearch.SEARCH_RESULT
//                        )
//                    }
//                        ?.let {
//                            map?.animateCamera(
//                                CameraUpdateFactory.newLatLngZoom(
//                                    it.polygon.points.getCenter(),
//                                    20f
//                                )
//                            )
//
//                            onPolygonClick(it.polygon)
//                        }
//                }
//                UPLOAD_SHP_CODE -> {
//                    data?.data?.let { uri ->
//                        val inputStream = this.contentResolver.openInputStream(uri)
//                        val outputFile = File.createTempFile("_shp_", ".zip", this.cacheDir)
//                        val outputStream = FileOutputStream(outputFile)
//                        inputStream?.use {
//                            it.copyTo(outputStream)
//                        }
//                        importShpController?.handle(outputFile, getFileName(uri)) { load(0, it.length(), it) }
//                    }
//                }
//                UPLOAD_GEOJSON_CODE -> {
//                    data?.data?.let { uri ->
//                        val inputStream = this.contentResolver.openInputStream(uri)
//                        val outputFile = File.createTempFile("_shp_", ".zip", this.cacheDir)
//                        val outputStream = FileOutputStream(outputFile)
//                        inputStream?.use {
//                            it.copyTo(outputStream)
//                        }
//                        importGeoJsonController?.handle(outputFile, getFileName(uri)) {
//                            load(
//                                0,
//                                it.length(),
//                                it
//                            )
//                        }
//                    }
//
//                }
//                else -> super.onActivityResult(requestCode, resultCode, data)
//            }
//        }
    }

//    override
    fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        // Un-check the box until the layer has been enabled
        // and show dialog box with permission rationale.
        showPermissionDeniedDialog = true
    }

//    override
    fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }



    override fun onPolygonClick(polygon: Polygon) {
        binding.btnAddPointInBetween.gone()
        binding.tvProperty.text = ""
        binding.tvProperty.show()
        var shapeIsImported = false
        var nama = ""
        var nik = ""
        var hak = ""
        if (polygon !in localPolygonDbReference.map { it.polygon }) {
            shapeIsImported = true
            clickedImportedShp = shpImported.find { it.polygon == polygon }
            binding.btnActivateShape.show()
            binding.btnDeleteActiveShape.gone()
            binding.btnShapeDetail.gone()
        } else {
            binding.btnDeleteActiveShape.show()
            isPolyline = false
            binding.btnShapeDetail.show()
            binding.btnActivateShape.gone()
        }
        if (!editing)
            binding.shapeMenuContainer.expand()
        binding.addShapeMenuContainer.gone()
        selectedPolygon.find { it == polygon }
            ?.apply {
                strokeWidth = NORMAL_STROKE_WIDTH
                selectedPolygonCircle.filter { circle -> points.contains(circle.position) }
                    .forEach { circle ->
                        circle.remove()
                        selectedPolygonCircle.remove(circle)
                    }
                selectedPolygon.remove(this)
                if (selectedPolygon.size <= 0)
                    backToNormal()
            }
            ?: let {
                polygon?.apply {
                    strokeWidth = 5f
                    // we don't need the closing point. So <SET> it!
                    if (!shapeIsImported) /*show marker label only on saved polygon*/
                        points.toSet().forEachIndexed { index, latLng ->
                            map?.apply {
//                                selectedPolygonCircle.add(
////                                    addMarker(
////                                        defaultMarker(index + 1)
////                                            .position(latLng)
////                                            .zIndex(3f)
////                                    )
//                                )
                            }
                        }

                    // select only up to 2 polygon at a time
                    if (selectedPolygon.size >= 2) {
                        selectedPolygon.firstOrNull()?.apply {
                            strokeWidth = NORMAL_STROKE_WIDTH
                            selectedPolygon.remove(this)

                            // reset its circle
                            selectedPolygonCircle.filter { circle -> points.contains(circle.position) }
                                .forEach { circle ->
                                    selectedPolygonCircle.remove(circle)
                                    circle.remove()
                                }
                        }
                    }
                    selectedPolygon.add(this)
                }
            }
        if (selectedPolygon.size > 0) {
            val cPolygon = selectedPolygon.lastOrNull() ?: return
            val polygon =
                localPolygonDbReference.withIndex()
                    .find { decorator -> decorator.value.polygon == cPolygon }
//            computedArearea = computeAreaByCoordinate(cPolygon.points.map {
//                val cUtm = it.toUtm()
//                Vector2(cUtm.coordinates[0], cUtm.coordinates[1])
//            }).roundToInt()
            if (shapeIsImported) {
                clickedImportedShp?.properties?.apply {
                    when {
                        keys.contains("NIB") -> nub = get("NIB").toString()
                        keys.contains("NUB") -> nub = get("NUB").toString()
                        keys.contains("nub") -> nub = get("nub").toString()
                        keys.contains("nib") -> nub = get("nib").toString()
                    }
                    when {
                        keys.contains("nama") -> nama = get("nama").toString()
                        keys.contains("NAMA") -> nama = get("NAMA").toString()
                        keys.contains("Nama") -> nama = get("Nama").toString()
                    }
                    when {
                        keys.contains("nik") -> nik = get("nik").toString()
                        keys.contains("NIK") -> nik = get("NIK").toString()
                    }
                    when {
                        keys.contains("hak") -> hak = get("hak").toString()
                        keys.contains("HAK") -> hak = get("HAK").toString()
                    }
                }
                val properties =
                    "NUB: $nub, \tLuas: ${computedArea}m2, \tNama: $nama, \tNIK: $nik, \tHAK: $hak"
                binding.tvProperty.text = properties
            }
            if (tetanggaBerbatasan > 0) {
                Collections.getUserAreaDetailDelinasi(
                    currentUser()?.email,
                    polygon?.value?.documentReference?.id
                )
                    .get(Source.CACHE)
//                    .addOnSuccessListener {
//                        it?.apply {
//                            nama = it.data?.get(SubjectIdentity.NAMA.prefix(SubjectIdentity.PREFIX)).toString()
//                            nub =
//                                it.data?.get(DelinasiGeneral.YURI_FILE_NO.prefix(DelinasiGeneral.PREFIX)).toString()
//                            var batasPilih = ""
//                            var Tetangga: String = ""
//                            if (tetanggaBerbatasan == TETANGGA_UTARA) {
//                                batasPilih = YuridisGeneral.UTARA.prefix(YuridisGeneral.PREFIX)
//                                Tetangga = "Utara"
//                            } else if (tetanggaBerbatasan == TETANGGA_SELATAN) {
//                                batasPilih = YuridisGeneral.SELATAN.prefix(YuridisGeneral.PREFIX)
//                                Tetangga = "Selatan"
//                            } else if (tetanggaBerbatasan == TETANGGA_BARAT) {
//                                batasPilih = YuridisGeneral.BARAT.prefix(YuridisGeneral.PREFIX)
//                                Tetangga = "Barat"
//                            } else if (tetanggaBerbatasan == TETANGGA_TIMUR) {
//                                batasPilih = YuridisGeneral.TIMUR.prefix(YuridisGeneral.PREFIX)
//                                Tetangga = "Timur"
//                            }
//                            rootLayout.snackbar("Tetangga " + Tetangga + " sudah dipilih")
//                            Collections.getUserAreaDetailYuridis(currentUser()?.email, tetanggaBerbatasanSelected)
//                                .set(
//                                    mapOf(
//                                        batasPilih to nama
//                                    ), SetOptions.merge()
//                                )
//                            tetanggaBerbatasan = 0
//                            backToNormal()
//                        }
//                    }
            }


            if (!shapeIsImported) {
                Collections.getUserAreaDetailDelinasi(
                    currentUser()?.email,
                    polygon?.value?.documentReference?.id
                )
                    .get(Source.CACHE)
                    .addOnSuccessListener {
//                        it?.apply {
//                            nama = it.data?.get(SubjectIdentity.NAMA.prefix(SubjectIdentity.PREFIX)).toString()
//                            nik = it.data?.get(SubjectIdentity.NIK.prefix(SubjectIdentity.PREFIX)).toString()
//                            hak = it.data?.get(AreaPosition.KETERANGAN.prefix(AreaPosition.PREFIX)).toString()
//                                .noNull()
//                            it.data?.get(DelinasiGeneral.YURI_FILE_NO.prefix(DelinasiGeneral.PREFIX))?.apply {
//                                if (toString() != "null") nub = toString()
//                            }
//                            val properties =
//                                "NUB: $nub, \tLuas: ${computedArea}m2, \tNama: $nama, \tNIK: $nik, \tHAK: $hak"
//                            tvProperty.text = properties
//                        }
                    }
            }
        }
        addedDistanceLabel.forEach { it.remove() }
        addedDistanceLabel.clear()
        selectedPolygon.lastOrNull()?.apply {
            for (index in 0..points.size - 2) {
//                map?.apply {
//                    addedDistanceLabel.add(addMarker(generateLabelBetween(points[index], points[index + 1])))
//                }
            }
        }
//        toggleDeleteButtonText()
    }

    override fun onPolylineClick(polyline: Polyline) {
        isPolyline = true
        binding.btnAddPointInBetween.gone()
        binding.tvProperty.text = ""
        binding.tvProperty.show()
        binding.btnDeleteActiveShape.show()
        binding.btnShapeDetail.show()
        binding.btnActivateShape.gone()
        binding.addShapeMenuContainer.gone()
        binding.shapeMenuContainer.expand()
        selectedPolyline.find { it == polyline }
            ?.apply {
                width = 10f
                selectedPolygonCircle.filter { circle -> points.contains(circle.position) }
                    .forEach { circle ->
                        circle.remove()
                        selectedPolygonCircle.remove(circle)
                    }
                selectedPolyline.remove(this)
            }
            ?: let {
                polyline?.apply {
                    width = 10f
                    points.toSet().forEachIndexed { index, latLng ->
                        map?.apply {
//                            selectedPolygonCircle.add(
////                                addMarker(
////                                    defaultMarker(index + 1)
////                                        .position(latLng)
////                                        .zIndex(3f)
////                                )
//                            )
                        }
                    }
                    // select only up to 2 polyline at a time
                    if (selectedPolyline.size >= 2) {
                        selectedPolyline.firstOrNull()?.apply {
                            selectedPolyline.remove(this)

                            // reset its circle
                            selectedPolygonCircle.filter { circle -> points.contains(circle.position) }
                                .forEach { circle ->
                                    selectedPolygonCircle.remove(circle)
                                    circle.remove()
                                }
                        }
                    }
                    selectedPolyline.add(this)
                }

            }
        val cPolyline = selectedPolyline.lastOrNull() ?: return
        val polyline =
            localPolylineDbReference.withIndex()
                .find { decorator -> decorator.value.polyline == cPolyline }
        Collections.getUserPolylineAreaDetail(
            currentUser()?.email,
            polyline?.value?.documentReference?.id
        )
            .get(Source.CACHE)
            .addOnSuccessListener {
//                it?.apply {
//                    val jenis = it.data?.get(Line.JENIS.prefix(Line.PREFIX)).toString()
//                    val diskripsi = it.data?.get(Line.DISKRIPSI.prefix(Line.PREFIX)).toString()
//                    val properties = "Jenis: $jenis, \tKeterangan: $diskripsi"
//                    tvProperty.text = properties
//                }
            }

        addedDistanceLabel.forEach { it.remove() }
        addedDistanceLabel.clear()
        selectedPolyline.lastOrNull()?.apply {
            for (index in 0..points.size - 2) {
//                map?.apply {
//                    addedDistanceLabel.add(addMarker(generateLabelBetween(points[index], points[index + 1])))
//                }
            }
        }
//        toggleDeleteButtonText()
    }


    override fun onMapClick(coordinate: LatLng) {
        if (addSPenMode) {
//            coordinate.let { addPointMarkerSession(it) }
        } else if (!editing) {
            backToNormal()
        }
//        if (viewModel.isWMSActive)
//            viewModel.clickWms(coordinate, kantah)
    }

    private fun backToNormal() {
        binding.addShapeMenuContainer.show()
        binding.shapeMenuContainer.collapse()
        binding.editMeasurementContainer.collapse()

        binding.tvProperty.gone()
        binding.tvLatPreview.text = ""
        binding.tvLongPreview.text = ""

        selectedPolygon.forEach {
            it.strokeWidth = NORMAL_STROKE_WIDTH
        }

        selectedPolyline.forEach {
            it.width = 5f
        }

        selectedPolygonCircle.forEach { it.remove() }
        val color = mutableListOf<Int>()
        color.add(R.drawable.ic_map_marker_red)
        color.add(R.drawable.ic_map_marker_green)
        color.add(R.drawable.ic_map_marker_yellow)
        color.add(R.drawable.ic_map_marker_blue)

        selectedPointWithStatus.forEach {
            it.apply {
                var statusPoint = 0
                var pointStatus = markerStatus[it.title?.toInt()]
                if (pointStatus.equals("Fixed", true)) {
                    statusPoint = 1
                } else if (pointStatus.equals("Float RTK", true)) {
                    statusPoint = 2
                } else if (pointStatus.equals("Differential", true)) {
                    statusPoint = 0
                } else if (pointStatus.equals("", true)) {
                    statusPoint = 3
                }

                setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        defaultIconGenerator(color[statusPoint]).makeIcon(
                            title
                        )
                    )
                )
            }
        }
        selectedCircle.forEach { it.remove() }
        addedDistanceLabel.forEach { it.remove() }

        lastTwoMarker.clear()
        addedDistanceLabel.clear()
        selectedPolygonCircle.clear()
        selectedPolygon.clear()
        selectedPolyline.clear()
        selectedCircle.clear()
        selectedPointWithStatus.clear()
        firstSelectedPolygon = null
    }

    @SuppressLint("SetTextI18n")
    override fun onMarkerClick(currentCircle: Marker): Boolean {
        val tm3Coordinate = currentCircle?.position?.toTm3()
//        val utmCoordinate = currentCircle?.position?.toUtm()
        binding.tvLatPreview.text = tm3Coordinate?.first.toString()
        binding.tvLongPreview.text = tm3Coordinate?.second.toString()
        if (currentCircle?.title == null) return true
        if (editing) {
//            addPointMarkerSession(currentCircle.position)
            pointTakenWithGnssStatusses.firstOrNull {
                GeoPoint(
                    currentCircle.position.latitude,
                    currentCircle.position.longitude
                ) == it.data.point
            }
                ?.apply {
                    Log.i(localClassName, "got status ${data.data()}")
                    pointWithRtkStatusSession.add(data.data())
                }
            return true
        }
        if (!editing && selectedPolygon.size <= 0) {
            currentCircle.apply {
                setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        defaultIconGenerator(R.drawable.ic_map_marker_grey).makeIcon(
                            title
                        )
                    )
                )
                selectedPointWithStatus.add(this)
            }
            binding.btnDeleteActiveShape.text = getString(R.string.delete_point)
            binding.shapeMenuContainer.expand()
            binding.btnDeleteActiveShape.show()
            binding.btnShapeDetail.gone()
            return true
        }
        binding.btnAddPointInBetween.gone()
        var snapped = false
        binding.editMeasurementContainer.collapse()

        selectedCircle.find { it == currentCircle }
            ?.apply /*if current tapped circle is active, toggle it!*/ {
                setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        defaultIconGenerator(R.drawable.ic_map_marker_yellow).makeIcon(
                            title
                        )
                    )
                )
                selectedCircle.remove(this)

                selectedCircle.firstOrNull()?.apply /*when there's still one left
        on the selected circle make it red*/ {
                    setIcon(
                        BitmapDescriptorFactory.fromBitmap(
                            defaultIconGenerator(R.drawable.ic_map_marker_red).makeIcon(
                                title
                            )
                        )
                    )
                }
            }
            ?: let {
                // allow only two circle to be selected
                if (selectedCircle.size >= 2) {
                    selectedCircle.firstOrNull()?.apply {
                        setIcon(
                            BitmapDescriptorFactory.fromBitmap(
                                defaultIconGenerator(R.drawable.ic_map_marker_yellow).makeIcon(
                                    title
                                )
                            )
                        )
                        selectedCircle.remove(this)
                    }
                }

                val secondSelectedPolygon =
                    selectedPolygon.find { polygon -> polygon.points.contains(currentCircle.position) }

                // if circle selected on different polygon. Snap it!
                if (secondSelectedPolygon != firstSelectedPolygon) {
                    firstSelectedPolygon?.apply {
                        polygonUndoStack.add(
                            PolygonUndoSnapp(
                                this,
                                points
                            ) {}) // polygon -> updatePointsPolygonOnDb(polygon) })
//                        points =
//                            points?.map { latLng -> if (latLng == selectedCircle.firstOrNull()?.position) currentCircle.position else latLng }
//                        updatePointsPolygonOnDb(this)
                        snapped = true
                    }
                } else /*show distance change form*/ {
                    selectedCircle.firstOrNull()?.apply {
                        binding.editMeasurementContainer.expand()
                        val destPoint = currentCircle.position ?: return false
                        binding.etPointDistance.setText("%.2f".format(Locale.ENGLISH, position.distanceTo(destPoint)))
                        binding.btnAddPointInBetween.show()
                    }
                }

                firstSelectedPolygon = secondSelectedPolygon

                currentCircle.apply {
                    selectedCircle.add(this)
                }

                selectedCircle.firstOrNull()?.apply {
                    try {
                        setIcon(
                            BitmapDescriptorFactory.fromBitmap(
                                defaultIconGenerator(R.drawable.ic_map_marker_red).makeIcon(
                                    title
                                )
                            )
                        )
                    } catch (e: IllegalArgumentException) {
//                        toast("Mohon bersabar").show()
                    }
                }

                if (selectedCircle.size > 1) {
                    selectedCircle.lastOrNull()?.apply {
                        setIcon(
                            BitmapDescriptorFactory.fromBitmap(
                                defaultIconGenerator(R.drawable.ic_map_marker_green).makeIcon(
                                    title
                                )
                            )
                        )
                    }
                }

                if (snapped)
                    backToNormal()
            }
        if (selectedCircle.size > 0) {
            selectedCircle.firstOrNull()?.isDraggable = false
            selectedCircle.lastOrNull()?.isDraggable = true
            lastCircleLocation = selectedCircle.lastOrNull()?.position
            selectedPolygon.find { lastCircleLocation in it.points }.apply {
                draggingPolygonMarker = this
            }
        }
        //toggleDeleteButtonText()
        return true
    }


    private fun toggleDeleteButtonText() {
        if (selectedCircle.size > 0)
            binding.btnDeleteActiveShape.text = getString(R.string.delete_point)
        else if (selectedPolygon.size > 0)
            binding.btnDeleteActiveShape.text = getString(R.string.delete_shape)
    }

    private fun updatePointsPolygonOnDb(polygon: Polygon) {
        polygon.apply {
            localPolygonDbReference.find { decorator -> decorator.polygon == this }?.apply {
                documentReference
                    .update(mapOf("points" to points.map { latLng ->
                        GeoPoint(
                            latLng.latitude,
                            latLng.longitude
                        )
                    }))
                    .addOnSuccessListener { Log.i(TAG, "[${documentReference.id}] updated") }
            }
        }
    }

    private fun unregisterEvent() {
        Log.i(localClassName, "unregistering event")
//        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    private fun registerEvent() {
//        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        Log.i(localClassName, "ondestroy")
        unregisterEvent()
        //connectionStateDisposable?.dispose()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        //connectionStateDisposable?.dispose()
    }

    override fun onResume() {
        registerEvent()
        listenConnectionState()
        analisisSatgas()
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
//        registerEvent()
        listenConnectionState()
        Collections.getUserDrawnAreas(currentUser()?.email)
            .whereEqualTo("workspace_id", workspace.id)
            .addSnapshotListener(this) { doc, _ ->
                val docs = doc?.documentChanges?.size
                bidangWorkspace = docs!!
                doc.documentChanges.forEach { documentChange ->
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val snapshot = documentChange.document
                        if (snapshot.reference !in localPolygonDbReference.map { it.documentReference }) {
                            snapshot.toObject(Area::class.java).points?.apply {
                                val createdPolygon =
                                    createPolygon(this.map { geoPoint ->
                                        LatLng(
                                            geoPoint.latitude,
                                            geoPoint.longitude
                                        )
                                    }.toSet())?.apply {
                                        snapshot.data["syc"]?.apply {
                                            if (this as Boolean) {
                                                fillColor = rColor(R.color.red_transparent)
                                                strokeColor = Color.GREEN
                                            }
                                        }

                                        localPolygonDbReference.add(PolygonDecorator(snapshot.reference, this))
                                        polygonDisplay?.points = listOf(map?.cameraPosition?.target)
                                        val moveCameraTo = map { LatLng(it.latitude, it.longitude) }.getCenter()
                                        map?.apply { moveCamera(CameraUpdateFactory.newLatLng(moveCameraTo)) }
                                        if (activatingShape) {
                                            var nub = ""
                                            var nama = ""
                                            var nik = ""
                                            var hak = ""

                                            clickedImportedShp?.properties?.apply {
                                                when {
                                                    keys.contains("NIB") -> nub = get("NIB").toString()
                                                    keys.contains("NUB") -> nub = get("NUB").toString()
                                                    keys.contains("nub") -> nub = get("nub").toString()
                                                    keys.contains("nib") -> nub = get("nib").toString()
                                                }
                                                when {
                                                    keys.contains("nama") -> nama = get("nama").toString()
                                                    keys.contains("NAMA") -> nama = get("NAMA").toString()
                                                    keys.contains("Nama") -> nama = get("Nama").toString()
                                                }
                                                when {
                                                    keys.contains("nik") -> nik = get("nik").toString()
                                                    keys.contains("NIK") -> nik = get("NIK").toString()
                                                }
                                                when {
                                                    keys.contains("hak") -> hak = get("hak").toString()
                                                    keys.contains("HAK") -> hak = get("HAK").toString()
                                                }
                                            }

                                            Collections.getUserAreaDetailDelinasi(
                                                currentUser()?.email,
                                                snapshot.reference.id
                                            )
//                                                .set(
//                                                    mapOf(
//                                                        DelinasiGeneral.YURI_FILE_NO.prefix(DelinasiGeneral.PREFIX) to nub,
//                                                        SubjectIdentity.NAMA.prefix(SubjectIdentity.PREFIX) to nama,
//                                                        SubjectIdentity.NIK.prefix(SubjectIdentity.PREFIX) to nik,
//                                                        AreaPosition.KETERANGAN.prefix(AreaPosition.PREFIX) to hak
//                                                    ), SetOptions.merge()
//                                                )
                                            Collections.getUserAreaDetailYuridis(
                                                currentUser()?.email,
                                                snapshot.reference.id
                                            )
//                                                .set(
//                                                    mapOf(
//                                                        YuridisGeneral.NUB.prefix(YuridisGeneral.PREFIX) to nub,
//                                                        YuridisGeneral.UTARA.prefix(YuridisGeneral.PREFIX) to "-",
//                                                        YuridisGeneral.SELATAN.prefix(YuridisGeneral.PREFIX) to "-",
//                                                        YuridisGeneral.BARAT.prefix(YuridisGeneral.PREFIX) to "-",
//                                                        YuridisGeneral.TIMUR.prefix(YuridisGeneral.PREFIX) to "-"
//                                                    ), SetOptions.merge()
//                                                )
                                            binding.btnActivateShape.gone()
                                            activatingShape = false
                                        }
                                    }
                                Collections.getUserAreaDetailDelinasi(currentUser()?.email, snapshot.id).get()
                                    .addOnSuccessListener { delinasiSnapshot ->
                                        delinasiSnapshot.data?.let { it1 ->
                                            it1.apply {
//                                                val cluster = get(DelinasiGeneral.CLUSTER.prefix(DelinasiGeneral.PREFIX))
//                                                changePolygonColorByCluster(cluster.toString(), createdPolygon)
                                            }
                                        }
                                    }
                            }
                            if (doneClicked) {
                                nub = "%s%s-%03d".format(
                                    workspace.getFormattedRw(),
                                    workspace.getFormattedRt(),
                                    workspaceIncrementCounter
                                )
                                if ((isRtkConnected && !addSPenMode) || pointWithRtkStatusSession.size > 0) {
                                    pointWithRtkStatusSession.forEach { statusData ->
                                        Collections.getUserAreaRtkData(currentUser()?.email, snapshot.reference.id)
                                            .add(timeStamp(statusData))
                                    }
                                    pointWithRtkStatusSession.clear()
                                }
                                Collections.getUserAreaDetailDelinasi(currentUser()?.email, snapshot.reference.id)
//                                    .set(
//                                        mapOf(
//                                            DelinasiGeneral.YURI_FILE_NO.prefix(DelinasiGeneral.PREFIX) to nub
//                                        ), SetOptions.merge()
//                                    )
                                Collections.getUserAreaDetailYuridis(currentUser()?.email, snapshot.reference.id)
//                                    .set(
//                                        mapOf(
//                                            YuridisGeneral.NUB.prefix(YuridisGeneral.PREFIX) to nub,
//                                            YuridisGeneral.UTARA.prefix(YuridisGeneral.PREFIX) to "-",
//                                            YuridisGeneral.SELATAN.prefix(YuridisGeneral.PREFIX) to "-",
//                                            YuridisGeneral.BARAT.prefix(YuridisGeneral.PREFIX) to "-",
//                                            YuridisGeneral.TIMUR.prefix(YuridisGeneral.PREFIX) to "-"
//                                        ), SetOptions.merge()
//                                    )
//                                startActivityForResult(
//                                    intentFor<DelinasiFormActivity>(
//                                        AREA_ID to snapshot.id,
//                                        Workspace.INTENT to workspace,
//                                        DATA_SIZE to localPolygonDbReference.size,
//                                        AREA to computedArea
//                                    ), DONE_CLICKED
//                                )
                                doneClicked = false
                            }
                        }
                    }
                }
            }

        /**
         * Polyline listener
         */


        Collections.getUserDrawnPolyAreas(currentUser()?.email)
            .whereEqualTo("workspace_id", workspace.id)
            .addSnapshotListener(this) { doc, _ ->
                doc?.documentChanges?.forEach { documentChange ->
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val snapshot = documentChange.document
                        if (snapshot.reference !in localPolylineDbReference.map { it.documentReference }) {
                            snapshot.toObject(Area::class.java).points?.apply {
                                val createdPolyline =
                                    createPolyline(this.map { geoPoint ->
                                        LatLng(
                                            geoPoint.latitude,
                                            geoPoint.longitude
                                        )
                                    }.toSet())?.apply {
                                        localPolylineDbReference.add(PolylineDecorator(snapshot.reference, this))
                                        polylineDisplay?.points = listOf(map?.cameraPosition?.target)
                                    }

                                Collections.getUserPolylineAreaDetail(currentUser()?.email, snapshot.id).get()
                                    .addOnSuccessListener { lineSnapshot ->
                                        lineSnapshot.data?.let { it1 ->
                                            it1.apply {
//                                                val jenis = get(Line.JENIS.prefix(Line.PREFIX))
//                                                changePolylineColorByJenis(jenis.toString(), createdPolyline)
                                            }
                                        }
                                    }
                            }
                        }

                        if (doneClicked) {
//                            startActivityForResult(
//                                intentFor<LineFormActivity>(
//                                    AREA_ID to snapshot.id,
//                                    Workspace.INTENT to workspace,
//                                    DATA_SIZE to localPolygonDbReference.size,
//                                    AREA to computedArea
//                                ), DONE_CLICKED
//                            )
                            doneClicked = false
                        }
                    }
                }
            }


        Collections.workspaceCountTracker().document(workspace?.id.toString())
            .addSnapshotListener { snapshot, _ ->
                workspaceIncrementCounter = try {
                    snapshot?.data?.get("counter_").toString().toInt()
                } catch (e: Exception) {
                    Log.i(
                        localClassName,
                        "Error while getting increment counter data indicating no value set to it, " +
                                "so here 0 or continuing the size of area in the workspace assigned to the counter is safe"
                    )
                    localPolygonDbReference.size
                }
            }
//        Collections.getUserPurchasedItem(currentUser()?.email).document(BuildConfig.PRO_PRODUCT_ID)
//            .addSnapshotListener { a, b ->
//                Log.i(localClassName, "$b")
//                if (b == null) {
//                    a?.data?.get(getString(R.string.product_item_key))?.toString()?.noNull()?.apply {
//                        if (!isEmpty() && equals(BuildConfig.PRO_PRODUCT_ID)) {
//                            val expire = a?.data?.get("expire_at").toString()
//                            a?.data?.get("expire_at").toString().noNull().apply {
//                                if (this.isNotEmpty()) {
//                                    isProFeaturePurchased = this.toDate() >= getDate().toDate()
//                                } else {
//                                    isProFeaturePurchased = true
//                                    Collections.getUserPurchasedItem(currentUser()?.email)
//                                        .document(BuildConfig.PRO_PRODUCT_ID).update(
//                                            mapOf("expire_at" to getExpire())
//                                        )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        Collections.getUserWorkspacePoints(currentUser()?.email, workspace.id)
            .orderBy("created_at", Query.Direction.ASCENDING)
            .addSnapshotListener(this) { a, _ ->
                map?.apply {
                    a?.documentChanges?.withIndex()?.forEach {
                        if (it.value.type == DocumentChange.Type.ADDED) {
                            val status = it.value.document.toObject(GnssStatusHolder::class.java)
                            if (it.value.document.reference !in pointTakenWithGnssStatusses.map { holder -> holder.doc }) {
                                pointTakenWithGnssStatusses.add(
                                    ReferenceToGnssStatusHolder(
                                        it.value.document.reference,
                                        status
                                    )
                                )
                                var statusPoint: Int = 0
                                if (status.status.equals("Fixed", true)) {
                                    statusPoint = 1
                                } else if (status.status.equals("Float RTK", true)) {
                                    statusPoint = 2
                                } else if (status.status.equals("Differential", true)) {
                                    statusPoint = 0
                                } else if (status.status.equals("", true)) {
                                    statusPoint = 3
                                }

                                markerStatus[pointTakenWithGnssStatusses.size] = status.status

//                                addMarker(
//                                    defaultMarkerPoint(pointTakenWithGnssStatusses.size, statusPoint).position(
//                                        LatLng(
//                                            status.point.latitude,
//                                            status.point.longitude
//                                        )
//                                    )
//                                )
                            }
                        }
                    }
                }
            }
        appPreference().edit().putBoolean("wms", false).apply()
    }
    private fun changePolygonColorByCluster(clusterName: String, createdPolygon: Polygon?) {
//        val polygonColor = clusterColorMaps.colorMaps[clusterName]
//        polygonColor?.let { colors ->
//            createdPolygon?.fillColor = rColor(colors.fillColor)
//            createdPolygon?.strokeColor = rColor(colors.strokeColor)
//        }
    }

    private fun changePolylineColorByJenis(jenis: String, createdPolyline: Polyline?) {
//        val polylineColor = lineColorMaps.colorMaps[jenis]
//        polylineColor?.let { colors ->
//            createdPolyline?.color = rColor(colors.color)
//        }
    }

//    override
    fun onMarkerDragStart(p0: Marker?) {}

//    override
    fun onMarkerDrag(p0: Marker?) {}

//    override
    fun onMarkerDragEnd(p0: Marker?) {
//        draggingPolygonMarker?.apply {
//            polygonUndoStack.add(PolygonUndoSnapp(this, points) { polygon ->
//                updatePointsPolygonOnDb(polygon)
//                onPolygonClick(polygon)
//                onPolygonClick(polygon)
//            })
//            points = points.map { if (it == lastCircleLocation) p0?.position else it }
//            updatePointsPolygonOnDb(this)
//            onPolygonClick(this)
//            onPolygonClick(this)
//        }
//        lastCircleLocation = p0?.position
    }

//    @Subscribe
//    fun onHrmsVrms(event: HrmsVrmsEvent) {
//        setHvrms(event.hrms, event.vrms, event.rms)
//    }


//    @Subscribe
//    fun onNtripMessage(event: NtripEvent) {
//        longToast(event.message)
//    }

//    @Subscribe
//    fun onMountpoint(event: MountpointEvent) {
//        if (event.selected) {
//            stopService(
//                Intent(
//                    this,
//                    smartgis.project.app.smartgis.ntrip.service.NTRIPService::class.java
//                )
//            )
//            mountPoint()
//        }
//    }


    private fun setHvrms(hrms: Double, vrms: Double, rms: Double) {
        binding.tvHvrms.show()
        binding.tvHvrms.text = "HRMS: %.3f - VRMS: %.3f".format(hrms, vrms)
        gnssStatusHolder.hrms = hrms
        gnssStatusHolder.vrms = vrms
        gnssStatusHolder.rms = rms
    }

    @Subscribe
    fun onLocation(event: LocationEvent) {
        binding.tvLatPreview.text = event.location?.latitude.toString()
        binding.tvLongPreview.text = event.location?.longitude.toString()
        if (!pausingRtk)
            map?.apply {
                moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        event.location?.let { LatLng(it.latitude, it.longitude) } as LatLng,
                        maxZoomLevel
                    )
                )

                circleRtkPosition?.let {
                    it.remove()
                    circleRtkPosition =
                        addCircle(
                            defaultCircle()
                                .center(event.location?.let { it1 -> LatLng(it1.latitude, it1.longitude) } as LatLng)
                                .fillColor(rColor(R.color.colorPrimary))
                                .strokeColor(rColor(R.color.colorPrimary))
                        )

                } ?: let {
                    circleRtkPosition = addCircle(
                        defaultCircle()
                            .center(event.location.let { it1 -> LatLng(it1.latitude, it1.longitude) } as LatLng)
                            .fillColor(rColor(R.color.colorPrimary))
                            .strokeColor(rColor(R.color.colorPrimary))
                    )
                }
            }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onRtkData(event: RtkDataEvent) {
//        logShower?.show(event.message)
//        event.message.hvrms().apply {
//            if (size > 0) {
//                setHvrms(get(0), get(1), get(2))
//            }
//        }
//
//        event.message.altitude()?.apply {
//            Log.i("ALT", "lat $this")
//        }
    }

    @Subscribe
    fun onSatellite(event: SatelliteStatusEvent) {
        gnssStatusHolder.hdop = event.hdop
        gnssStatusHolder.vdop = event.vdop
        gnssStatusHolder.pdop = event.pdop
    }

    @Subscribe
    fun onQuality(event: QualityEvent) {
        binding.tvRtkStatus.show()
        binding.tvRtkStatus.text = "Status: ${event.quality}"
        event.altitude?.let {
            gnssStatusHolder.altitude = it.toFloat()
        }
        gnssStatusHolder.status = event.quality.toString()
        gnssStatusHolder.origin = event.origin.toString()
    }

    @Subscribe
    fun onRtkEvent(event: RtkEvent) {
        Log.i(localClassName, "connecting rtk")
        isRtkConnected = true
        if (!event.connect) {
            Log.i(localClassName, "dissconnecting rtk")
//            stopService(
//                Intent(
//                    this,
//                    smartgis.project.app.smartgis.ntrip.service.NTRIPService::class.java
//                )
//            )
            isRtkConnected = false
            binding.tvHvrms.gone()
            binding.tvRtkStatus.gone()
            circleRtkPosition?.remove()
        }
    }

//    @Subscribe
//    fun onFormData(data: FormDataEvent) {
//        if (!selectedPolyline.isEmpty()) {
//            val jenis = data.data?.get(Line.JENIS.prefix(Line.PREFIX)).toString()
//            changePolylineColorByJenis(jenis, selectedPolyline.lastOrNull())
//        }
//
//        if (!selectedPolygon.isEmpty()) {
//            val cluster =
//                data.data?.get(DelinasiGeneral.CLUSTER.prefix(DelinasiGeneral.PREFIX)).toString()
//            changePolygonColorByCluster(cluster, selectedPolygon.lastOrNull())
//        }
//    }


//    inner class ShowData : ToParse {
//        private val data = mutableListOf<String>()
//        private val adapter = ArrayAdapter<String>(this@MainActivity, R.layout.simple_small_item, data)
//        private var lvLog: ListView? = null
//        private var autoScroll: Boolean = false
//        private var sendLog: Boolean = false
//
//        fun showDialog() {
//            alert {
//                customView {
//                    verticalLayout {
//                        lparams(width = matchParent, height = matchParent)
//                        lvLog = listView {
//                            title = "Log Data RTK"
//                            adapter = this@ShowData.adapter
//                        }.lparams(width = matchParent, height = dip(0), weight = 1f)
//                        checkBox {
//                            text = context.getString(R.string.auto_scroll)
//                            onCheckedChange { _, isChecked -> autoScroll = isChecked }
//                        }
//                        checkBox {
//                            text = "Kirim Log Data"
//                            onCheckedChange { _, isChecked -> sendLog = isChecked }
//                        }
//                    }
//                }
//                positiveButton(getString(R.string.close)) { it.dismiss() }
//            }.show()
//        }
//
//        private fun scrollToLast() {
//            lvLog?.setSelection(data.size - 1)
//        }
//
//        override fun show(data: String?) {
//            this@ShowData.data.add("$data")
//            adapter.notifyDataSetChanged()
//            if (autoScroll)
//                scrollToLast()
//            if (sendLog)
//                Collections.getRtkData()
//                    .add(
//                        timeStamp(
//                            mutableMapOf(
//                                "from" to currentUser()?.email.toString(),
//                                "data" to data.toString()
//                            )
//                        )
//                    )
//        }
//    }


    fun selectMenuTetangga() {
        val detailMenus =
            listOf(
                "Tetangga Utara",
                "Tetangga Selatan",
                "Tetangga Barat",
                "Tetangga Timur"
            )

        var dialogInterfaceMenu: DialogInterface? = null

//        dialogInterfaceMenu = alert {
//            customView {
//                listView {
//                    adapter = ArrayAdapter<String>(context, simple_list_item_1, detailMenus)
//                    onItemClickListener =
//                        AdapterView.OnItemClickListener { _, _, position, _ ->
//                            tetanggaBerbatasan = position + 1
//                            dialogInterfaceMenu?.dismiss()
//                        }
//                }
//            }
//            cancelButton { }
//        }.show()
    }

    private fun saveImportSHPAttribut(
        isImportedShp: ShapeImportedDecorator? = null,
        id: String = ""
    ) {
        var nub = ""
        var nama = ""
        var nik = ""
        var hak = ""

        isImportedShp?.properties?.apply {
            when {
                keys.contains("NIB") -> nub = get("NIB").toString()
                keys.contains("NUB") -> nub = get("NUB").toString()
                keys.contains("nub") -> nub = get("nub").toString()
                keys.contains("nib") -> nub = get("nib").toString()
            }
            when {
                keys.contains("nama") -> nama = get("nama").toString()
                keys.contains("NAMA") -> nama = get("NAMA").toString()
                keys.contains("Nama") -> nama = get("Nama").toString()
            }
            when {
                keys.contains("nik") -> nik = get("nik").toString()
                keys.contains("NIK") -> nik = get("NIK").toString()
            }
            when {
                keys.contains("hak") -> hak = get("hak").toString()
                keys.contains("HAK") -> hak = get("HAK").toString()
            }
        }

//        Collections.getUserAreaDetailDelinasi(currentUser()?.email, id)
//            .set(
//                mapOf(
//                    DelinasiGeneral.YURI_FILE_NO.prefix(DelinasiGeneral.PREFIX) to nub,
//                    SubjectIdentity.NAMA.prefix(SubjectIdentity.PREFIX) to nama,
//                    SubjectIdentity.NIK.prefix(SubjectIdentity.PREFIX) to nik,
//                    AreaPosition.KETERANGAN.prefix(AreaPosition.PREFIX) to hak
//                ), SetOptions.merge()
//            )
//        Collections.getUserAreaDetailYuridis(currentUser()?.email, id)
//            .set(
//                mapOf(
//                    YuridisGeneral.NUB.prefix(YuridisGeneral.PREFIX) to nub,
//                    YuridisGeneral.UTARA.prefix(YuridisGeneral.PREFIX) to "-",
//                    YuridisGeneral.SELATAN.prefix(YuridisGeneral.PREFIX) to "-",
//                    YuridisGeneral.BARAT.prefix(YuridisGeneral.PREFIX) to "-",
//                    YuridisGeneral.TIMUR.prefix(YuridisGeneral.PREFIX) to "-"
//                ), SetOptions.merge()
//            )

    }

    private fun mountPoint() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val Entries = ArrayList<String>()
        val EntryValues = ArrayList<String>()
        Entries.add("Refresh Stream List")
        EntryValues.add("")
        val sourcetable = preferences.getString("ntripsourcetable", "")
        val lines =
            sourcetable!!.split("\\r?\\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        for (i in lines.indices) {
            val fields = lines[i].split(";".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (fields.size > 4) {
                if (fields[0].lowercase(Locale.ENGLISH) == "str") {
                    Entries.add(fields[1])
                    EntryValues.add(fields[1])
                }
            }
        }
        EntryValues[0] = "Refresh Stream List"

        val detailMenus = EntryValues

//        this.dialogInterface = alert {
//            customView {
//                listView {
//                    adapter = ArrayAdapter<String>(context, simple_list_item_1, detailMenus)
//                    onItemClickListener =
//                        AdapterView.OnItemClickListener { _, _, position, _ ->
//
//                            val ntripstream = if (position == 0) "" else EntryValues[position]
//                            preferences.edit().putString("ntripstream", ntripstream).commit()
//                            startService(
//                                Intent(
//                                    this@MainActivity,
//                                    smartgis.project.app.smartgis.ntrip.service.NTRIPService::class.java
//                                )
//                            )
//                            this@MainActivity.dialogInterface?.dismiss()
//                        }
//                }
//            }
//            cancelButton { }
//        }.show()
    }

    private fun loadAds() {
//        billingContainer =
//            BillingProcessor.newBillingProcessor(this, getString(R.string.license_key), this)
//        billingContainer.initialize()
//
//        MobileAds.initialize(this)
    }

    private fun showMobs() {

    }

    fun showDialogAds() {
//        alert {
//            message = getString(R.string.upgrade_pro_msg)
//            title = getString(R.string.upgrade_pro)
//            positiveButton("Upgrade") {
//                if (billingContainer.isOneTimePurchaseSupported && BillingProcessor.isIabServiceAvailable(
//                        this@MainActivity
//                    )
//                )
//                    billingContainer.purchase(this@MainActivity, BuildConfig.PRO_PRODUCT_ID)
//                else alert(getString(R.string.os_not_supported), getString(R.string.attention)) {}.show()
//            }
//            negativeButton("Tonton Video") { dialogInterface1 ->
//                dialogInterface1.dismiss()
//                showMobs()
//            }
//        }.show()
    }

//    override
    fun onBillingInitialized() {

    }

//    override
    fun onPurchaseHistoryRestored() {

    }

//    override
//    fun onProductPurchased(productId: String, details: PurchaseInfo?) {
//
//    }

//    override
    fun onBillingError(errorCode: Int, error: Throwable?) {
//        alert(
//            error?.localizedMessage.toString(),
//            "${getString(R.string.error_occured)}: $errorCode"
//        ).show()
    }
    fun getExpire(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 4)
        val date = calendar.time
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        return format1.format(date)
    }

    private fun getDate(): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        return format1.format(date)

    }

    private fun checkYuridisIsComplete() {
//        val typeDoc =
//            mutableListOf(
//                "yuridis_ptsl_$PERORANGAN",
//                "yuridis_ptsl_$BPHTB",
//                "yuridis_ptsl_$ALAS_HAK"
//            )
//
//        val yuridisHelper = BerkasYuridisHelper()
//
//        localPolygonDbReference.forEach { polygon ->
//            val id = polygon.documentReference.id
//            Collections.getUserAreaDetailYuridisPTSLCollections(currentUser()?.email, id).get()
//                .addOnSuccessListener { result ->
//                    var typeDocExist: MutableList<String> = mutableListOf()
//                    result.forEach { doc ->
//                        typeDocExist.add(doc.id)
//                    }
//                    val difference = typeDoc.toSet().minus(typeDocExist?.toSet())
//                    if (difference.isNotEmpty() || !yuridisHelper.getComplateByParent(id)) {
//                        polygon.polygon.strokeWidth = 5f
//                        polygon.polygon.strokeColor = Color.RED
//                        polygon.polygon.fillColor = resources.getColor(R.color.red_transparent)
//                    } else {
//                        polygon.polygon.strokeColor = Color.GREEN
//                        polygon.polygon.strokeWidth = NORMAL_STROKE_WIDTH
//                        polygon.polygon.fillColor = resources.getColor(R.color.yellow_transparent)
//                    }
//
//                }
//        }
    }

    private fun setupLocation() {
//        locationHelper = SimpleLocation(
//            this,
//            true,
//            false,
//            updateIntervalInMilliseconds
//        )
//        locationHelper.setListener(this)
//        locationHelper.beginUpdates()
//        getLastLocation()
    }

//    override
    fun onPositionChanged() {
//        try {
//            altitude = locationHelper.altitude
//            accuracy = locationHelper.lastLocation.accuracy
//            gpsPosition = LatLng(locationHelper.latitude, locationHelper.longitude)
//            if (accuracy <= 50 && gpsPosition.distanceTo(lastLocation) > 30)
//                saveLocation(locationHelper.latitude, locationHelper.longitude)
//        } catch (e: Exception) {
//        }
    }

    fun saveLocation(latitude: Double, longitude: Double) {
        Collections.getUserLocation(currentUser()?.email).document("current")
            .update(
                timeStamp(
                    mutableMapOf(
                        "lokasi" to GeoPoint(latitude, longitude),
                    )
                )
            )
            .addOnSuccessListener { reference ->
                Log.i(localClassName, "Success $reference")
            }
            .addOnFailureListener {
            }

        Collections.trackUserLocation(currentUser()?.email).add(
            waktu(
                mutableMapOf(
                    "lokasi" to GeoPoint(latitude, longitude),
                )
            )
        )
            .addOnSuccessListener { reference ->
                Log.i(localClassName, "Success $reference")
            }
            .addOnFailureListener {
            }
    }

    private fun getLastLocation() {
        Collections.getUserLocation(currentUser()?.email).document("current")
            .addSnapshotListener { snapshot, _ ->
                try {
                    snapshot?.data?.get("lokasi")?.let {
                        val location = it as GeoPoint
                        lastLocation = LatLng(location.latitude, location.longitude)
                    } ?: addLocationIfNotExist()
                } catch (e: Exception) {
                }
            }
    }

    private fun addLocationIfNotExist() {
        val lat = -7.7827188
        val lng = 110.343225
        Collections.getUserLocation(currentUser()?.email).document("current").set(
            timeStamp(
                mutableMapOf(
                    "lokasi" to GeoPoint(lat, lng),
                )
            )
        )
    }

    private fun wmsObservers() {
//        viewModel.wmsShapeDecorator.observe(this, { wms ->
//            createDialogWMS(wms)
//        })
    }

    private fun createDialogWMS(wms: ShapeWMSDecorator) {
//        alert {
//            title = "Info Bidang/PERSIL"
//            positiveButton("Tutup") {}
//            negativeButton("Aktifkan Bidang") {
//                createShpPolygon(wms.polygon)
//            }
//            customView {
//                linearLayout {
//                    textView(
//                        "NIB: ${wms.properties.nib}\n" +
//                                "TIPE HAK: ${wms.properties.tipe_hak}\n" +
//                                "NAMA: ${wms.properties.nama}\n" +
//                                "NO HAK: ${wms.properties.no_hak}\n" +
//                                "KETERANGAN: ${wms.properties.keterangan}\n"
//                    )
//                    padding = dip(26)
//                }
//            }
//        }.show()
    }

    fun analisisSatgas() {
//        if (viewModel.checkSatgasPangtan) {
//            localPolygonDbReference.forEach {
//                Collections.getUserAreaDetailPengtan(currentUser()?.email, it.documentReference.id).get()
//                    .addOnSuccessListener { doc ->
//                        if (!doc.exists()) {
//                            it.polygon.apply {
//                                strokeColor = rColor(R.color.jalan)
//                                strokeWidth = 6f
//                            }
//                        } else {
//                            it.polygon.apply {
//                                fillColor = rColor(R.color.yellow_transparent)
//                                strokeWidth = NORMAL_STROKE_WIDTH
//                                strokeColor = rColor(R.color.yellow)
//                            }
//                        }
//                    }
//            }
//        }
    }

    private fun loadFileSHP() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/zip"
        try {
            startActivityForResult(intent, UPLOAD_SHP_CODE)
        } catch (e: ActivityNotFoundException) {
        }
    }

    private fun enableGPS() {
        GpsUtils(this).turnGPSOn { isGPSEnable ->
            isGPS = isGPSEnable
        }
    }

    private fun loadFileGeoJson() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/octet-stream"
        try {
            startActivityForResult(intent, UPLOAD_GEOJSON_CODE)
        } catch (e: ActivityNotFoundException) {
        }
    }
}