package smartgis.project.app.smartgis

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.net.NetworkInfo
import android.os.Bundle
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
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import smartgis.project.app.smartgis.databinding.ActivityMainBinding
import smartgis.project.app.smartgis.databinding.ActivityWelcomeLoginBinding
import smartgis.project.app.smartgis.decorators.PolygonDecorator
import smartgis.project.app.smartgis.decorators.PolylineDecorator
import smartgis.project.app.smartgis.decorators.ShapeImportedDecorator
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.models.GnssStatusHolder
import smartgis.project.app.smartgis.models.ReferenceToGnssStatusHolder
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.CircleFromLatLng
import smartgis.project.app.smartgis.utils.ClusterColorMapping
import smartgis.project.app.smartgis.utils.LineColorMapping
import smartgis.project.app.smartgis.utils.SimpleLocation
import smartgis.project.app.smartgis.utils.currentUser
import smartgis.project.app.smartgis.utils.gone
import smartgis.project.app.smartgis.utils.rColor
import smartgis.project.app.smartgis.utils.show
import smartgis.project.app.smartgis.utils.timeStamp

class MainActivity :  LoginRequiredActivity(),
    OnMapReadyCallback {
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
//    private var tileOverlay: TileOverlay? = null
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
//    private var clickedImportedShp: ShapeImportedDecorator? = null
    private var computedArea: Int = 0
    private var nub = ""
    private var draggingPolygonMarker: Polygon? = null
    private var circleRtkPosition: Circle? = null
//    private val shpImported = mutableListOf<ShapeImportedDecorator>()
//    private val wmsActived = mutableListOf<ShapeWMSDecorator>()
//    private val polygonUndoStack = mutableListOf<Actionable>()
    private val lastTwoMarker = mutableSetOf<LatLng>()
    private val addedDistanceLabel = mutableSetOf<Marker>()
//    private var importShpController: ImportShpController? = null
//    private var importGeoJsonController: ImportGeoJSONController? = null
    private val changedDistanceCircle = mutableSetOf<CircleFromLatLng>()
    private var pausingRtk = false
    private var isMode = 1
    private var isRtkConnected = false
    private var state: NetworkInfo.State? = null
//    private var connectionStateDisposable: Disposable? = null
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
//        askLocationForPermissions()
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
//            if (isMode == POLYLINE_MODE)
//                addPolylineAreaToDb(pointMarkerSession.toSet())
//            else if (isMode == POLYGON_MODE && pointMarkerSession.size > 2) {
//                addAreaToDb(pointMarkerSession.toSet())
//                polylineDisplay?.points = listOf()
//            } else {
//                polylineDisplay?.points = listOf()
//                polygonDisplay?.points = listOf(map?.cameraPosition?.target)
//            }
            addedDistanceLabel.forEach { marker -> marker.remove() }
            addedDistanceLabel.clear()
            pointMarkerSession.clear()
            lastTwoMarker.clear()
            addSPenMode = false
            editing = false
        }

        binding.btnActivateShape.setOnClickListener {
            activatingShape = true
//            selectedPolygon.lastOrNull()?.points?.toSet()?.let { it1 -> addAreaToDb(it1) }
//            backToNormal()
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
//        map?.setOnMarkerClickListener(this)
//        map?.setOnMarkerDragListener(this)
//        map?.setOnPolylineClickListener(this)
        map?.mapType = MAP_TYPE_SATELLITE
        map?.uiSettings?.isCompassEnabled = true

        val lat = -7.7827188
        val lng = 110.343225

        map?.apply { moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17f)) }
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
}