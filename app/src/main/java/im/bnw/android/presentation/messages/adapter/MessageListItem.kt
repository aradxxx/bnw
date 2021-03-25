package im.bnw.android.presentation.messages.adapter

import android.os.Parcelable
import im.bnw.android.domain.message.Message
import kotlinx.parcelize.Parcelize

interface MessageListItem : Parcelable

@Parcelize
data class MessageItem(
    val message: Message
) : MessageListItem
