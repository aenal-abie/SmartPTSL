package smartgis.project.app.smartgis.export

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
//import com.anjlab.android.iab.v3.BillingProcessor
//import com.anjlab.android.iab.v3.PurchaseInfo
//import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.SetOptions
//import kotlinx.android.synthetic.main.activity_export.*
//import org.jetbrains.anko.alert
//import org.jetbrains.anko.design.indefiniteSnackbar
import smartgis.project.app.smartgis.BuildConfig
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.databinding.ActivityExportBinding
import smartgis.project.app.smartgis.documents.Collections
//import smartgis.project.app.smartgis.forms.signature.BaseSignatureFormContainer
//import smartgis.project.app.smartgis.forms.signature.SignatureFormActivity
//import smartgis.project.app.smartgis.forms.workspaceforms.gu.SignatureGuKedua
//import smartgis.project.app.smartgis.forms.workspaceforms.gu.SignatureGuPertama
//import smartgis.project.app.smartgis.forms.workspaceforms.saksi.SignatureSaksiKedua
//import smartgis.project.app.smartgis.forms.workspaceforms.saksi.SignatureSaksiPertama
import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.currentUser
import smartgis.project.app.smartgis.utils.enable
import smartgis.project.app.smartgis.utils.timeStamp
import smartgis.project.app.smartgis.viewmodels.FeatureViewModel
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseExportableData : LoginRequiredActivity()
//    BillingProcessor.IBillingHandler,
//  BillingProcessor.IPurchasesResponseListener
{

    private var loading: Snackbar? = null
    protected var workspace: Workspace? = null

    //  private lateinit var rewardedAd: RewardedAd
//  private lateinit var billingContainer: BillingProcessor
    private var isProFeaturePurchased = false
    private var finishWatch = false

    protected lateinit var exportBinding: ActivityExportBinding


    protected val featureVM: FeatureViewModel by viewModels()

    @SuppressLint("SetTextI18n", "SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exportBinding = DataBindingUtil.setContentView(this, R.layout.activity_export)
        exportBinding.lifecycleOwner = this
        exportBinding.proViewModel = featureVM
//    loading = rootLayout.indefiniteSnackbar("Sedang menyiapkan data")
//    setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        workspace = intent.getParcelableExtra(Workspace.INTENT)
//  exportBinding.tvExportLocation.text = getExportPath()

        exportBinding.btnExport.setOnClickListener {
            onSaveClick()
        }

        initBilling()
//    featureVM.getStatusPro(BuildConfig.PRO_PRODUCT_ID)

        initListener()

        initObservers()
    }

    private fun initObservers() {
        featureVM.isFeaturePro.observe(this, { isPro ->
            if (!isPro) {
                val toast = Toast.makeText(
                    this@BaseExportableData,
                    getString(R.string.expire_msg),
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        })
    }

    private fun initListener() {
//    exportBinding.btnPro.setOnClickListener {
//      alert(
//        getString(R.string.please_upgrade).format(getString(R.string.export_status_rtk)),
//        getString(R.string.attention)
//      ) {
//        positiveButton("Upgrade") {
//          billingContainer.consumePurchaseAsync(BuildConfig.PRO_PRODUCT_ID, this@BaseExportableData)
//          billingContainer.purchase(this@BaseExportableData, BuildConfig.PRO_PRODUCT_ID)
//        }
//        negativeButton("Kembali") {}
//      }.show()
//    }
    }

    private fun initBilling() {
//    billingContainer =
//      BillingProcessor.newBillingProcessor(this, getString(R.string.license_key), this)
//    billingContainer.initialize()
    }

    abstract fun onSaveClick()
    abstract fun getExportPath(): String
    abstract fun loadData(dataLoaded: () -> Unit)

    protected fun showLoading() {
        loading?.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        loadData { enableExport() }
    }

    protected fun enableExport() {
        loading?.dismiss()
    exportBinding.btnExport.enable()
    exportBinding.btnExport.text = getString(R.string.export)
    }


    companion object {
        //    val TTD_PEMILIK_URL =
//      BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureFormActivity.INFIX)
//    val TTD_SAKSI2_URL =
//      BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureSaksiKedua.INFIX)
//    val TTD_SAKSI1_URL =
//      BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureSaksiPertama.INFIX)
//    val TTD_GU1_URL = BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureGuPertama.INFIX)
//    val TTD_GU2_URL = BaseSignatureFormContainer.TTD_URL_PATTERN.format(SignatureGuKedua.INFIX)
        const val POLYGON_ID = "polygon_id"
        const val X = "x"
        const val Y = "y"
    }


//  override fun onBillingInitialized() {
////    billingContainer.consumePurchaseAsync(BuildConfig.PRO_PRODUCT_ID, this)
//  }

//  override fun onPurchaseHistoryRestored() {
//
//  }

//  override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
//    Collections.getUserPurchasedItem(currentUser()?.email).document(productId).set(
//      timeStamp(
//        mutableMapOf(getString(R.string.product_item_key) to productId, "expire_at" to getExpire())
//      ), SetOptions.merge()
//    ).addOnSuccessListener {
//      featureVM.hideButtonPro()
//    }
//  }


//  override fun onBillingError(errorCode: Int, error: Throwable?) {
//    alert(
//      error?.localizedMessage.toString(),
//      "${getString(R.string.error_occured)}: $errorCode"
//    ).show()
//  }

//  override fun onDestroy() {
//    if (billingContainer != null)
//      billingContainer.release()
//    super.onDestroy()
//  }

    @SuppressLint("SimpleDateFormat")
    private fun getExpire(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 4)
        val date = calendar.time
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        return format1.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        return format1.format(date)

    }

}

data class DataAndReferenceHolder(val id: String, val values: Map<String, Any>?)