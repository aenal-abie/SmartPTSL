package smartgis.project.app.smartgis.fragments

import android.R.attr.onClick
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import smartgis.project.app.smartgis.databinding.ItemListWorkspaceBinding


class WorkspacesAdapter(
    private val items: MutableList<WorkspaceAndDbReferenceHolder>,
    private val onMenuClick: (Int) -> Unit,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<WorkspacesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemListWorkspaceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListWorkspaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            txtName.text = items[position].workspace.name

            btnMenu.setOnClickListener {
                onMenuClick(holder.bindingAdapterPosition)
            }

            item.setOnClickListener {
                onItemClick(holder.bindingAdapterPosition)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}