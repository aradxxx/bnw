package im.bnw.android.presentation.messagedetails.adapter

import im.bnw.android.domain.message.Reply
import im.bnw.android.presentation.messages.adapter.MessageListItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReplyItem(
    val reply: Reply,
    val sortTag: String,
    val offset: Int,
    val replyToUser: String,
    val saved: Boolean = false,
) : MessageListItem
