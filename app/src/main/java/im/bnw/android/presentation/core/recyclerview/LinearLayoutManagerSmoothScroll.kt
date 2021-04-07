package im.bnw.android.presentation.core.recyclerview

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller

class LinearLayoutManagerSmoothScroll @JvmOverloads constructor(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    smoothScrollSpeedMillisecondsPerInch: Float = SCROLL_SPEED_DEFAULT
) : LinearLayoutManager(context, orientation, reverseLayout) {
    private val speed: Float = smoothScrollSpeedMillisecondsPerInch
    private var canScrollVertically = true

    fun setCanScrollVertically(scrollable: Boolean) {
        canScrollVertically = scrollable
    }

    override fun canScrollVertically(): Boolean {
        return canScrollVertically && super.canScrollVertically()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val smoothScroller: SmoothScroller = TopSnappedSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private inner class TopSnappedSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@LinearLayoutManagerSmoothScroll.computeScrollVectorForPosition(targetPosition)
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return speed / displayMetrics.densityDpi
        }
    }

    companion object {
        const val SCROLL_SPEED_FAST = 30f
        const val SCROLL_SPEED_DEFAULT = 120f
    }
}
