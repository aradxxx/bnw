package im.bnw.android.data.login

import com.google.gson.annotations.SerializedName

class LoginResponse(
    @SerializedName("ok")
    val ok: String,
    @SerializedName("desc")
    val token: String,
)
