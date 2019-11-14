package im.bnw.android.presentation.util

import android.view.View

fun View.setVisibility(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}
