package im.bnw.android.data.core

import com.google.gson.annotations.SerializedName

class BaseResponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("desc")
    val desc: String,
) {
    fun isSuccess(): Boolean = ok
}
