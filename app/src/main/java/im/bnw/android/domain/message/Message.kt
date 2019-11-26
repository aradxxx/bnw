package im.bnw.android.domain.message

import com.google.gson.annotations.SerializedName

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
    val date: Float,

    @SerializedName("id")
    val id: String,

    @SerializedName("anonymous")
    val anonymous: Boolean,

    @SerializedName("anoncomments")
    val anoncomments: Boolean
)
