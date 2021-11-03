package im.bnw.android.data.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

class ProfileResponse(
    @Json(name = "ok")
    val ok: Boolean,
    /*@SerializedName("loltroll")
    val loltroll: List<String>,
    @SerializedName("subscriptions")
    val subscriptions: List<String>,*/
    @Json(name = "user")
    val user: String,
    /*@SerializedName("subscribers")
    val subscribers: List<String>,
    @SerializedName("friends")
    val friends: List<String>,
    @SerializedName("vcard")
    val vcard: String,*/
    @Json(name = "messages_count")
    val messagesCount: Int,
    /*@SerializedName("about")
    val about: String,
    @SerializedName("subscriptions_all")
    val subscriptionsAll: List<String>,
    @SerializedName("characters_count")
    val charactersCount: Int,*/
    @Json(name = "regdate")
    val regDate: Double,
    @Json(name = "comments_count")
    val commentsCount: Int,
    /*@SerializedName("subscribers_all")
    val subscribersAll: List<String>*/
)
