package im.bnw.android.presentation.core

import androidx.annotation.StringRes
import im.bnw.android.domain.message.Media

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
    val items: List<Media>,
    val selectedItem: Media
)
