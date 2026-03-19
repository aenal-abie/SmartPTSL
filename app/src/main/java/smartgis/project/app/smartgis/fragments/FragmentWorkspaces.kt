package smartgis.project.app.smartgis.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdView
//import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
//import kotlinx.android.synthetic.main.activity_workspaces.*
//import kotlinx.android.synthetic.main.activity_workspaces.view.*
//import smartgis.project.app.smartgis.BuildConfig
//import smartgis.project.app.smartgis.MainActivity
import smartgis.project.app.smartgis.R
import smartgis.project.app.smartgis.databinding.ActivityHomeBinding
import smartgis.project.app.smartgis.databinding.ActivityWorkspacesBinding
import smartgis.project.app.smartgis.documents.Collections
import smartgis.project.app.smartgis.models.Workspace
//import smartgis.project.app.smartgis.documents.Collections
//import smartgis.project.app.smartgis.models.Workspace
import smartgis.project.app.smartgis.utils.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentWorkspaces : Fragment() {

    private var queryRegistration: ListenerRegistration? = null
    private var isProFeaturePurchased = false

    companion object {
        val fragment by lazy { FragmentWorkspaces() }
    }

    private val data =
        mutableListOf<WorkspaceAndDbReferenceHolder>()
    private val originData =
        mutableListOf<WorkspaceAndDbReferenceHolder>()

    private lateinit var adapter: WorkspacesAdapter


    private lateinit var binding: ActivityWorkspacesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 1. Initialize the binding
        binding = ActivityWorkspacesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    private fun animateView() {
        //    AnimatorSet().apply {
        //      playSequentially(
        //        ObjectAnimator.ofFloat(iv_animation, "translationX", 20f)
        //      )
        //      duration = 500
        //      doOnEnd {
        //        start()
        //      }
        //      start()
        //    }
    }

    private fun contactUs() {
        val urlContactDeveloper = getString(R.string.contact_us)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(urlContactDeveloper)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val query = Collections.getUserWorkspace(currentUser()?.email)
            .orderBy("created_at", Query.Direction.DESCENDING)


        queryRegistration = query.addSnapshotListener { querySnapshot, e ->
            querySnapshot?.apply {
                toggleListView(isEmpty)
                val workspacesData = documents.map { snapshot ->
                    WorkspaceAndDbReferenceHolder(
                        snapshot.reference,
                        Workspace.toObject(snapshot)
                    )
                }
                data.clear()
                data.addAll(workspacesData)


                originData.clear()
                originData.addAll(workspacesData)
            }
        }

        binding.imgWa.setOnClickListener {
            contactUs()
        }
        animateView()
    }

    private fun showMenuDialog(position: Int) {
        val options = arrayOf("Edit", "Delete")

        this@FragmentWorkspaces.context?.let {
            AlertDialog.Builder(it)
                .setTitle("Pilih Aksi")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showEditWorkspaceDialog(position)
                        1 -> showDeleteWorkspaceDialog(position)
                    }
                }
                .show()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = WorkspacesAdapter(data) { position ->
            showMenuDialog(position)
        }

        binding.lvWorkspaces.layoutManager = LinearLayoutManager(this@FragmentWorkspaces.context)
        binding.lvWorkspaces.adapter = adapter

        binding.textFind.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                if (s.isEmpty()) {
                    data.clear()
                    data.addAll(originData)
                } else {
                    data.clear()
                    data.addAll(originData.filter { it.workspace.name.contains(s) })
                }
                adapter.notifyDataSetChanged()
            }
        })
        binding.btnAddWorkspace.setOnClickListener { showCreateNewWorkspaceDialog() }
        binding.btnCreateWorkspace.setOnClickListener { showCreateNewWorkspaceDialog() }


    }

    private fun toggleListView(boolean: Boolean) {
        if (boolean) {
            binding.llNoWorkspace.show()
            binding.lvWorkspaces.gone()
        } else {
            binding.llNoWorkspace.gone()
            binding.lvWorkspaces.show()
        }
    }

    private fun showCreateNewWorkspaceDialog() {
        showWorkspaceForm(getString(R.string.add_workspace), null) { name, rw, rt ->

            val email = currentUser()?.email ?: return@showWorkspaceForm

            Collections.getUserWorkspace(email)
                .add(
                    timeStamp(
                        mutableMapOf(
                            "name" to name,
                            "rw" to rw,
                            "rt" to rt,
                            "points" to 30
                        )
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(
                        this@FragmentWorkspaces.requireContext(),
                        getString(R.string.workspace_created),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@FragmentWorkspaces.requireContext(),
                        e.localizedMessage ?: "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun showEditWorkspaceDialog(position: Int) {
        val referenceHolder = data[position]
        showWorkspaceForm(
            getString(R.string.update_workspace_title),
            referenceHolder.workspace
        ) { a, b, c ->
            referenceHolder.docReference.update(
                mapOf(
                    "name" to a,
                    "rw" to b,
                    "rt" to c
                )
            )
        }
    }

    private fun showDeleteWorkspaceDialog(position: Int) {
        MaterialAlertDialogBuilder(this@FragmentWorkspaces.requireContext())
            .setTitle(getString(R.string.confirm))
            .setMessage(getString(R.string.confirm_delete_workspace_message))
            .setPositiveButton("OK") { dialog, _ ->
                data.removeAt(position).docReference.delete()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showWorkspaceForm(
        title_: String,
        workspace: Workspace?,
        onOk: (String, String, String) -> Unit
    ) {
        val view = LayoutInflater.from(this@FragmentWorkspaces.context)
            .inflate(R.layout.dialog_workspace, null)

        val etWorkspace = view.findViewById<EditText>(R.id.etWorkspace)
        val etRw = view.findViewById<EditText>(R.id.etRw)
        val etRt = view.findViewById<EditText>(R.id.etRt)

        // set default value jika edit
        workspace?.let {
            etWorkspace.setText(it.name)
            etRw.setText(it.getFormattedRw())
            etRt.setText(it.getFormattedRt())
        }

        val dialog = this@FragmentWorkspaces.context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(title_)
                .setView(view)
                .setPositiveButton("OK", null) // nanti override biar tidak auto close
                .setNegativeButton("Cancel", null)
                .create()
        }

        dialog?.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val name = etWorkspace.text.toString().trim()
                val rw = etRw.text.toString().trim()
                val rt = etRt.text.toString().trim()

                if (name.isNotEmpty() && rw.isNotEmpty() && rt.isNotEmpty()) {
                    onOk(name, rw, rt)
                    dialog.dismiss()
                } else {
                    Snackbar.make(
                        view,
                        getString(R.string.text_cannot_empty),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog?.show()

    }


    fun getExpire(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 4)
        val date = calendar.time
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        return format1.format(date)
    }

    fun getDate(): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        return format1.format(date)

    }

    private fun getPucrhase() {
//    Collections.getUserPurchasedItem(currentUser()?.email).document(BuildConfig.PRO_PRODUCT_ID)
//      .addSnapshotListener { a, b ->
//        if (b == null) {
//          a?.data?.get(getString(R.string.product_item_key))?.toString()?.noNull()?.apply {
//            if (!isEmpty() && equals(BuildConfig.PRO_PRODUCT_ID)) {
//              a?.data?.get("expire_at").toString().noNull().apply {
//                if (this.isNotEmpty()) {
//                  isProFeaturePurchased = this.toDate() >= getDate().toDate()
//                } else {
//                  isProFeaturePurchased = true
//                  Collections.getUserPurchasedItem(currentUser()?.email)
//                    .document(BuildConfig.PRO_PRODUCT_ID).update(
//                      mapOf("expire_at" to getExpire())
//                    )
//                }
//              }
//            }
//            if (!isProFeaturePurchased) {
//              val toast = Toast.makeText(context, getString(R.string.expire_msg), Toast.LENGTH_LONG)
//              toast.setGravity(Gravity.CENTER, 0, 0)
//              toast.show()
//            }
//          }
//        }
//        if (!isProFeaturePurchased) {
//          val adRequest = AdRequest.Builder().build()
//          adsView.loadAd(adRequest)
//        } else {
//          adsView.visibility = View.GONE
//        }
//      }
//  }
    }
}

data class WorkspaceAndDbReferenceHolder(
    val docReference: DocumentReference,
    val workspace: Workspace
) {
    override fun toString(): String {
        return workspace.name
    }
}

