package im.bnw.android.data.message

import im.bnw.android.domain.message.Content
import im.bnw.android.domain.message.Media
import im.bnw.android.domain.message.Message

object MessageMapper {
    fun MessageDto.toMessage(): Message {
        return Message(
            id = id,
            tags = tags,
            text = text,
            user = user,
            timestamp = timestamp(),
            anonymous = anonymous,
            anonComments = anonComments,
            replyCount = replyCount,
            recommendations = recommendations,
            content = contentDto.toContent(),
            format = format ?: "",
            clubs = clubs,
        )
    }

    private fun ContentDto.toContent(): Content {
        return Content(
            text = text,
            media = mediaDto.map { it.toMedia() }
        )
    }

    private fun MediaDto.toMedia(): Media {
        return Media(
            previewUrl = previewUrl,
            fullUrl = fullUrl
        )
    }
}
