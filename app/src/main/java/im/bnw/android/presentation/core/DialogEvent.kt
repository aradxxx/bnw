package im.bnw.android.presentation.core

import androidx.annotation.StringRes

data class DialogEvent(
    @StringRes
    val title: Int,
    @StringRes
    val message: Int? = null
)

data class BnwApiErrorEvent(
    val description: String
)

data class OpenMediaEvent(
    val urls: List<String>,
    val selectedItem: String = urls.first()
)
