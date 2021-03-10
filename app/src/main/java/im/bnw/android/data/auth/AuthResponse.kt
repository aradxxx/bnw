package im.bnw.android.data.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthResponse(
    @Json(name = "ok")
    val ok: Boolean,
    @Json(name = "desc")
    val token: String,
)
