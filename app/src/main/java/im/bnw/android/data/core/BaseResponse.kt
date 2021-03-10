package im.bnw.android.data.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BaseResponse(
    @Json(name = "ok")
    val ok: Boolean,
    @Json(name = "desc")
    val desc: String,
)
