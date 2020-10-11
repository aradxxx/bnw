package im.bnw.android.presentation.imageview

import im.bnw.android.presentation.core.State
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageState(val fullUrl: String = "") : State()
