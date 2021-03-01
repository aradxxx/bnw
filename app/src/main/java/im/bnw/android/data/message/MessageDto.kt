package im.bnw.android.data.message

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageDto(
    @SerializedName("tags")
    val tags: List<String>,

    @SerializedName("text")
    val text: String,

    @SerializedName("user")
    val user: String,

    @SerializedName("html")
    val contentDto: ContentDto,

    @SerializedName("date")
    val date: Double,

    @SerializedName("id")
    val id: String,

    @SerializedName("anonymous")
    val anonymous: Boolean,

    @SerializedName("anoncomments")
    val anoncomments: Boolean,

    @SerializedName("replycount")
    val replyCount: Int,

    @SerializedName("recommendations")
    val recommendations: List<String>
) : Parcelable {
    @SuppressWarnings("MagicNumber")
    fun timestamp() = (date * 1_000).toLong()
}
