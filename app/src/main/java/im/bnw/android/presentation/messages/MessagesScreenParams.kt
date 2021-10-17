package im.bnw.android.presentation.messages

import android.os.Parcelable
import im.bnw.android.domain.message.MessageMode
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessagesScreenParams(
    val user: String,
    val mode: MessageMode,
) : Parcelable
