package im.bnw.android.data.message

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class MessageDto(
    @Json(name = "tags")
    val tags: List<String>,

    @Json(name = "text")
    val text: String,

    @Json(name = "user")
    val user: String,

    @Json(name = "html")
    val contentDto: ContentDto,

    @Json(name = "date")
    val date: Double,

    @Json(name = "id")
    val id: String,

    @Json(name = "anonymous")
    val anonymous: Boolean,

    @Json(name = "anoncomments")
    val anoncomments: Boolean,

    @Json(name = "replycount")
    val replyCount: Int,

    @Json(name = "recommendations")
    val recommendations: List<String>
) : Parcelable {
    @SuppressWarnings("MagicNumber")
    fun timestamp() = (date * 1_000).toLong()
}
