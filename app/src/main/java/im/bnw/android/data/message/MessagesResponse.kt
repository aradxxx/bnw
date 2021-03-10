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
class PostResponse(
    @Json(name = "ok")
    val ok: Boolean,
    @Json(name = "id")
    val id: String,
)
