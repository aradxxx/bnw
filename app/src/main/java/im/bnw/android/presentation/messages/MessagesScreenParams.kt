package im.bnw.android.presentation.messages

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessagesScreenParams(
    val user: String,
    val today: Boolean,
) : Parcelable
