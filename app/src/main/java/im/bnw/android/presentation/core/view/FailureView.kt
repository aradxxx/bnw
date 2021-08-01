package im.bnw.android.presentation.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import im.bnw.android.R
import im.bnw.android.databinding.ViewFailureBinding
import im.bnw.android.presentation.util.newText

class FailureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = ViewFailureBinding.inflate(LayoutInflater.from(context), this)

    var isVisible: Boolean
        get() = visibility == View.VISIBLE
        set(value) {
            visibility = if (value) View.VISIBLE else View.GONE
        }

    init {
        if (attrs != null) {
            updateAttrs(attrs)
        }
    }

    fun setActionListener(action: () -> Unit) {
        binding.action.setOnClickListener {
            action()
        }
    }

    fun setFailure(
        @DrawableRes imageResId: Int = R.drawable.ic_google_downasaur,
        @StringRes titleResId: Int = 0,
        messageString: String = "",
        @StringRes actionTextResId: Int = R.string.retry,
    ) = with(binding) {
        if (imageResId != 0) {
            image.setImageResource(imageResId)
            image.isVisible = true
        } else {
            image.isVisible = false
        }

        if (titleResId != 0) {
            title.newText = context.getString(titleResId)
            title.isVisible = true
        } else {
            title.isVisible = false
        }

        if (messageString.isNotEmpty()) {
            message.newText = messageString
            message.isVisible = true
        } else {
            message.isVisible = false
        }

        if (actionTextResId != 0) {
            action.newText = context.getString(actionTextResId)
            action.isVisible = true
        } else {
            action.isVisible = false
        }

        isVisible = true
    }

    private fun updateAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FailureView)
        typedArray.getResourceId(R.styleable.FailureView_image, 0).notZero {
            binding.image.setImageResource(it)
        }
        typedArray.getResourceId(R.styleable.FailureView_title, 0).notZero {
            binding.title.newText = context.getString(it)
        }
        typedArray.getResourceId(R.styleable.FailureView_message, 0).notZero {
            binding.message.newText = context.getString(it)
        }
        typedArray.getResourceId(R.styleable.FailureView_actionText, 0).let {
            if (it != 0) {
                binding.action.newText = context.getString(it)
            } else {
                binding.action.newText = context.getString(R.string.retry)
            }
        }
        typedArray.recycle()
    }

    private fun Int.notZero(block: (Int) -> Unit) {
        if (this != 0) {
            block(this)
        }
    }
}
