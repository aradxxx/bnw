package im.bnw.android.presentation.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import im.bnw.android.R
import im.bnw.android.databinding.ViewFailureBinding
import im.bnw.android.presentation.util.newText

class FailureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var binding: ViewFailureBinding =
        ViewFailureBinding.inflate(LayoutInflater.from(context), this)

    @DrawableRes
    var imageResId: Int = 0
        set(value) {
            field = value
            binding.image.setImageResource(value)
        }

    var message: String = ""
        set(value) {
            field = value
            binding.message.newText = value
        }

    var actionText: String = ""
        set(value) {
            field = value
            binding.action.newText = value
        }

    init {
        if (attrs != null) {
            updateAttrs(attrs)
        }
    }

    fun setActionListener(action: () -> Unit) {
        binding.action.setOnClickListener { action() }
    }

    private fun updateAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FailureView)

        val imageResourceId = typedArray.getResourceId(R.styleable.FailureView_image, 0)
        if (imageResourceId != 0) {
            imageResId = imageResourceId
        }

        val messageResId = typedArray.getResourceId(R.styleable.FailureView_message, 0)
        if (messageResId != 0) {
            message = resources.getString(messageResId)
        }

        val actionTextResId = typedArray.getResourceId(R.styleable.FailureView_actionText, 0)
        actionText = if (actionTextResId == 0) {
            resources.getString(R.string.retry)
        } else {
            resources.getString(actionTextResId)
        }

        typedArray.recycle()
    }
}
