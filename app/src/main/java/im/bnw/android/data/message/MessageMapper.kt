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
            anoncomments = anoncomments,
            replyCount = replyCount,
            recommendations = recommendations,
            content = contentDto.toContent()
        )
    }

    private fun ContentDto.toContent(): Content {
        return Content(
            text = text,
            media = mediaDto.map { it.toMedia() }
        )
    }

    fun MediaDto.toMedia(): Media {
        return Media(
            previewUrl = previewUrl,
            fullUrl = fullUrl
        )
    }
}
