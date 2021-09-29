package im.bnw.android.presentation.messagedetails.adapter

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import im.bnw.android.R
import im.bnw.android.presentation.util.attrColor
import im.bnw.android.presentation.util.dpToPxF
import im.bnw.android.presentation.util.vibrate
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlin.math.absoluteValue

fun callback(swiped: (Int) -> Unit) = object : ItemTouchHelper.Callback() {
    var candidate: Int? = null
    val distance = 128.dpToPxF
    val xLimit = 64.dpToPxF

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (viewHolder.bindingAdapterPosition == 0) {
            makeMovementFlags(0, 0)
        } else {
            makeMovementFlags(0, ItemTouchHelper.LEFT)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // no op
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 1F

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = Float.MAX_VALUE

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val position = viewHolder.bindingAdapterPosition

        if (!isCurrentlyActive && candidate != null) {
            swiped(position)
            candidate = null
        }

        candidate = if (dX.absoluteValue > xLimit && isCurrentlyActive) {
            if (candidate == null && isCurrentlyActive) {
                recyclerView.context.vibrate()
            }
            position
        } else {
            if (candidate != null && isCurrentlyActive) {
                recyclerView.context.vibrate()
            }
            null
        }

        val iconColor = if (candidate != null) {
            recyclerView.context.getColor(R.color.colorPrimary)
        } else {
            recyclerView.context.attrColor(R.attr.secondaryIconColor)
        }
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftActionIcon(R.drawable.ic_reply)
            .setActionIconTint(iconColor)
            .create()
            .decorate()

        val xDx = dX.coerceIn(-distance, distance)
        super.onChildDraw(c, recyclerView, viewHolder, xDx, dY, actionState, isCurrentlyActive)
    }
}

fun buildTouchHelper(swiped: (Int) -> Unit) = object : ItemTouchHelper(callback(swiped)) {}
