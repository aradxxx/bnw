package im.bnw.android.data.message

import com.google.gson.annotations.SerializedName
import im.bnw.android.domain.message.Message

class MessagesResponse(
    @SerializedName("ok")
    val ok: String,
    @SerializedName("messages")
    val messages: List<Message>
)
