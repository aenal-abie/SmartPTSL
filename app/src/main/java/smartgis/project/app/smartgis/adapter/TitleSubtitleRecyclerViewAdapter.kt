package smartgis.project.app.smartgis.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
//import kotlinx.android.synthetic.main.title_subtitle_item_with_indicator.view.*
import smartgis.project.app.smartgis.R

class TitleSubtitleRecyclerViewAdapter(
  private val items: List<ViewItem>,
  private val filterable: Filter,
  val clickedOn: (Int) -> Unit
) :
  Adapter<TitleSubtitleRecyclerViewAdapter.ViewHolder>(), Filterable {

  override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
    return ViewHolder(
      LayoutInflater.from(p0.context).inflate(
        R.layout.title_subtitle_item_with_indicator,
        p0,
        false
      ), clickedOn
    )
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
    p0.bind(items[p1])
  }

  class ViewHolder(v: View, val clickedOn: (Int) -> Unit) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
    fun bind(data: ViewItem) {
//      itemView.apply {
//        tvTitle.text = data.title
//        tvSubtitle.text = data.subtitle
//        setOnClickListener { clickedOn(adapterPosition) }
//        tvIndicator.text = data.indicator
//        if (data.indicatorColor.isNotEmpty()) {
//          tvIndicator.setTextColor(Color.parseColor(data.indicatorColor))
//        }
//      }
    }
  }

  override fun getFilter(): Filter = filterable

}

data class ViewItem(
  val title: String,
  val subtitle: String,
  val indicator: String = "",
  val indicatorColor: String = ""
)