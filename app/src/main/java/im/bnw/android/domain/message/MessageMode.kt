package im.bnw.android.domain.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class MessageMode : Parcelable {
    @Parcelize
    object All : MessageMode()

    @Parcelize
    object Today : MessageMode()

    @Parcelize
    object Feed : MessageMode()
}