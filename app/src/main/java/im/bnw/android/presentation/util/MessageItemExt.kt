package im.bnw.android.presentation.util

import im.bnw.android.domain.message.Media
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.messages.adapter.MessageListItem

val MessageListItem.media: List<Media>
    get() = when (this) {
        is MessageItem -> {
            message.media
        }
        is ReplyItem -> {
            reply.media
        }
        else -> {
            throw IllegalArgumentException("What the messageListItem is this")
        }
    }

val MessageListItem.user: String
    get() = when (this) {
        is MessageItem -> {
            message.user
        }
        is ReplyItem -> {
            reply.user
        }
        else -> {
            throw IllegalArgumentException("What the messageListItem is this")
        }
    }

val MessageListItem.id: String
    get() = when (this) {
        is MessageItem -> {
            message.id
        }
        is ReplyItem -> {
            reply.id
        }
        else -> {
            throw IllegalArgumentException("What the messageListItem is this")
        }
    }

val MessageListItem.text: String
    get() = when (this) {
        is MessageItem -> {
            message.text
        }
        is ReplyItem -> {
            reply.text
        }
        else -> {
            throw IllegalArgumentException("What the messageListItem is this")
        }
    }
