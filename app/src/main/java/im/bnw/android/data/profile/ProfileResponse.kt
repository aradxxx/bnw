package im.bnw.android.data.profile

import com.google.gson.annotations.SerializedName

class ProfileResponse(
    @SerializedName("ok")
    val ok: Boolean,
    /*@SerializedName("loltroll")
    val loltroll: List<String>,
    @SerializedName("subscriptions")
    val subscriptions: List<String>,*/
    @SerializedName("user")
    val user: String,
    /*@SerializedName("subscribers")
    val subscribers: List<String>,
    @SerializedName("friends")
    val friends: List<String>,
    @SerializedName("vcard")
    val vcard: String,*/
    @SerializedName("messages_count")
    val messagesCount: Int,
    /*@SerializedName("about")
    val about: String,
    @SerializedName("subscriptions_all")
    val subscriptionsAll: List<String>,
    @SerializedName("characters_count")
    val charactersCount: Int,
    @SerializedName("regdate")
    val regDate: Long,*/
    @SerializedName("comments_count")
    val commentsCount: Int,
    /*@SerializedName("subscribers_all")
    val subscribersAll: List<String>*/
)
