package im.bnw.android.data.auth

import com.google.gson.annotations.SerializedName

class AuthResponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("desc")
    val token: String,
)
