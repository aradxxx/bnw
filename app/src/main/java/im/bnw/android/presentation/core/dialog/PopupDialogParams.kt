package im.bnw.android.presentation.core.dialog

import android.os.Parcelable
import im.bnw.android.presentation.core.dialog.PopupDialogFragment.Companion.UNDEFINED_VALUE
import kotlinx.parcelize.Parcelize

@Parcelize
data class PopupDialogParams(
    val requestCode: Int = UNDEFINED_VALUE,
    val title: String = "",
    val message: String = "",
    val positiveText: String = "",
    val negativeText: String = "",
    val neutralText: String = "",
) : Parcelable
