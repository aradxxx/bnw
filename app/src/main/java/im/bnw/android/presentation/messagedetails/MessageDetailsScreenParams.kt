package im.bnw.android.presentation.messagedetails

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageDetailsScreenParams(
    val messageId: String
) : Parcelable
