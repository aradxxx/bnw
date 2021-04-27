package im.bnw.android.presentation.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import im.bnw.android.R
import im.bnw.android.databinding.ViewMediaOverlayBinding

class MediaOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding = ViewMediaOverlayBinding.inflate(LayoutInflater.from(context), this)

    fun setNavigationOnClickListener(navigationListener: () -> Unit) {
        binding.toolbar.setNavigationOnClickListener {
            navigationListener()
        }
    }

    fun updatePosition(currentPosition: Int, maxPosition: Int) = with(binding) {
        toolbar.title = context.getString(R.string.one_of_list, currentPosition, maxPosition)
    }
}
