package im.bnw.android.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MessagesResponse(
    @Json(name = "ok")
    val ok: Boolean,
    @Json(name = "messages")
    val messages: List<MessageDto>,
)

@JsonClass(generateAdapter = true)
class MessageDetailsResponse(
    @Json(name = "ok")
    val ok: Boolean,
    @Json(name = "msgid")
    val messageId: String,
    @Json(name = "message")
    val message: MessageDto,
    @Json(name = "replies")
    val replies: List<ReplyDto>,
)

@JsonClass(generateAdapter = true)
class PostResponse(
    @Json(name = "ok")
    val ok: Boolean,
    @Json(name = "id")
    val id: String,
)
