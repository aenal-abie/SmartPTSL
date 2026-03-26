package smartgis.project.app.smartgis.forms.workspaceforms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import smartgis.project.app.smartgis.LoginRequiredActivity
import smartgis.project.app.smartgis.databinding.SaksiLayoutBinding
import smartgis.project.app.smartgis.forms.workspaceforms.saksi.DataSaksiPertama
import smartgis.project.app.smartgis.models.Workspace

class WorkspaceForms : LoginRequiredActivity() {

    private lateinit var binding: SaksiLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SaksiLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val workspaceValue = intent.getStringExtra(Workspace.INTENT)

        Log.i(
            localClassName,
            Pair(Workspace.INTENT, workspaceValue).toString()
        )

        binding.listview.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listOf(
                "Saksi Pertama",
                "Saksi Kedua",
                "TTD Saksi Pertama",
                "TTD Saksi Kedua",
                "Data GU Pertama",
                "TTD GU Pertama",
                "Data GU Kedua",
                "TTD GU Kedua"
            )
        )

        binding.listview.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val intent = Intent(this, DataSaksiPertama::class.java)
                    intent.putExtra(Workspace.INTENT, workspaceValue)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}