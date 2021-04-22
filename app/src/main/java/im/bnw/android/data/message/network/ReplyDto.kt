package im.bnw.android.data.message.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import im.bnw.android.data.message.ContentDto
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ReplyDto(
    @Json(name = "format")
    val format: String?,

    @Json(name = "replyto")
    val replyTo: String?,

    @Json(name = "replytotext")
    val replyToText: String?,

    @Json(name = "html")
    val contentDto: ContentDto,

    @Json(name = "date")
    val date: Double,

    @Json(name = "num")
    val num: Int,

    @Json(name = "user")
    val user: String,

    @Json(name = "anonymous")
    val anonymous: Boolean,

    @Json(name = "text")
    val text: String,

    @Json(name = "id")
    val id: String,

    @Json(name = "message")
    val messageId: String,
) : Parcelable {
    @SuppressWarnings("MagicNumber")
    fun timestamp() = (date * 1_000).toLong()
}
