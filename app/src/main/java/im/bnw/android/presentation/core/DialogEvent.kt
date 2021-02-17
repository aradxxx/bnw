package im.bnw.android.presentation.core

import androidx.annotation.StringRes

data class DialogEvent(
    @StringRes
    val title: Int,
    @StringRes
    val message: Int? = null
)
