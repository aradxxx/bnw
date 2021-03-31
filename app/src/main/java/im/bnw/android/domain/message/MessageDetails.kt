package im.bnw.android.domain.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageDetails(
    val messageId: String,
    val message: Message,
    val replies: List<Reply>
) : Parcelable
