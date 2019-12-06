package im.bnw.android.presentation.messages.adapter

import im.bnw.android.domain.message.Message
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageWithMediaItem(
    val message: Message
) : MessageListItem {
    override fun message(): Message = message
}
