package im.bnw.android.presentation.imageview

import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageState(val url: String = "") : State
