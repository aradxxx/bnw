package im.bnw.android.presentation.messages

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import im.bnw.android.presentation.util.dpToPx

class FabVisibilityScrollListener(private val fab: FloatingActionButton) : RecyclerView.OnScrollListener() {
    @Suppress("MagicNumber")
    private val deltaMax = 64.dpToPx
    private var current = deltaMax

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!fab.isEnabled) {
            return
        }
        current -= dy
        if (current <= 0) {
            current = 0
        }
        if (current >= deltaMax) {
            current = deltaMax
        }
        if (current == 0 && !fab.isOrWillBeHidden) {
            fab.hide()
        }
        if (current == deltaMax && !fab.isOrWillBeShown) {
            fab.show()
        }
    }
}
