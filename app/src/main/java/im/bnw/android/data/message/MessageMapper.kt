package im.bnw.android.data.message

import im.bnw.android.domain.message.Media
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.Reply

object MessageMapper {
    fun MessageDto.toMessage(): Message {
        return Message(
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
    }

    fun ReplyDto.toReply(): Reply {
        return Reply(
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
            messageId = messageId
        )
    }

    private fun ContentDto.media(): List<Media> {
        return mediaDto.map { it.toMedia() }
    }

    private fun MediaDto.toMedia(): Media {
        return Media(
            previewUrl = previewUrl,
            fullUrl = fullUrl
        )
    }
}
