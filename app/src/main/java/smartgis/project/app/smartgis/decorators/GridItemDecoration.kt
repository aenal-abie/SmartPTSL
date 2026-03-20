package smartgis.project.app.smartgis.decorators

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class GridItemDecoration(val gridSpacingPx: Int, val gridSize: Int) :
  androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: androidx.recyclerview.widget.RecyclerView,
    state: androidx.recyclerview.widget.RecyclerView.State
  ) {
    val itemPosition = (view.getLayoutParams() as androidx.recyclerview.widget.RecyclerView.LayoutParams).viewAdapterPosition
    outRect.set(gridSpacingPx, gridSpacingPx, gridSpacingPx, gridSpacingPx)
    if (itemPosition == 0 || itemPosition == 1) outRect.top = gridSpacingPx * 2
    if ((itemPosition % gridSize) == 0) outRect.left = gridSpacingPx * 2
    else outRect.right = gridSpacingPx * 2
  }
}