package im.bnw.android.domain.message

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    @SerializedName("tags")
    val tags: List<String>,

    @SerializedName("text")
    val text: String,

    @SerializedName("user")
    val user: String,

    @SerializedName("html")
    val content: Content,

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
    fun timestamp(): Long = (date * 1000).toLong()
}
