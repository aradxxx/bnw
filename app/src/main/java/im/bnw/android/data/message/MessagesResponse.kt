package im.bnw.android.data.message

import com.google.gson.annotations.SerializedName

class MessagesResponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("messages")
    val messages: List<MessageDto>
)

class PostResponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("id")
    val id: String
)
