package im.bnw.android.data.message

import im.bnw.android.data.message.db.MessageEntity
import im.bnw.android.data.message.db.ReplyEntity
import im.bnw.android.data.message.network.MessageDto
import im.bnw.android.data.message.network.ReplyDto
import im.bnw.android.domain.message.Media
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.Reply

object MessageMapper {
    fun MessageDto.toMessage() = Message(
        id = id,
        tags = tags,
        text = text,
        user = user,
        htmlText = contentDto.text,
        media = contentDto.media(),
        timestamp = timestamp(),
        anonymous = anonymous,
        anonComments = anonComments,
        replyCount = replyCount,
        recommendations = recommendations,
        format = format ?: "",
        clubs = clubs,
    )

    fun Message.toMessageEntity() = MessageEntity(
        id = id,
        tags = tags,
        text = text,
        user = user,
        htmlText = text,
        media = media,
        timestamp = timestamp,
        anonymous = anonymous,
        anonComments = anonComments,
        replyCount = replyCount,
        recommendations = recommendations,
        format = format,
        clubs = clubs,
    )

    fun MessageEntity.toMessage() = Message(
        id = id,
        tags = tags,
        text = text,
        user = user,
        htmlText = text,
        media = media,
        timestamp = timestamp,
        anonymous = anonymous,
        anonComments = anonComments,
        replyCount = replyCount,
        recommendations = recommendations,
        format = format,
        clubs = clubs,
    )

    fun ReplyDto.toReply() = Reply(
        format = format ?: "",
        replyTo = replyTo ?: "",
        replyToText = replyToText ?: "",
        htmlText = contentDto.text,
        media = contentDto.media(),
        timestamp = timestamp(),
        num = num,
        user = user,
        anonymous = anonymous,
        text = text,
        id = id,
        messageId = messageId,
    )

    fun Reply.toReplyEntity() = ReplyEntity(
        format = format,
        replyTo = replyTo,
        replyToText = replyToText,
        htmlText = htmlText,
        media = media,
        timestamp = timestamp,
        num = num,
        user = user,
        anonymous = anonymous,
        text = text,
        id = id,
        messageId = messageId,
    )

    fun ReplyEntity.toReply() = Reply(
        format = format ?: "",
        replyTo = replyTo ?: "",
        replyToText = replyToText ?: "",
        htmlText = htmlText,
        media = media,
        timestamp = timestamp,
        num = num,
        user = user,
        anonymous = anonymous,
        text = text,
        id = id,
        messageId = messageId,
    )

    private fun ContentDto.media() = mediaDto.map { it.toMedia() }

    private fun MediaDto.toMedia() = Media(
        previewUrl = previewUrl,
        fullUrl = fullUrl,
    )
}
