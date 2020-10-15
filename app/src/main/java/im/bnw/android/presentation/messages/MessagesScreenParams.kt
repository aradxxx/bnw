package im.bnw.android.presentation.messages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessagesScreenParams(val user: String) : Parcelable
