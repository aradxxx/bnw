package im.bnw.android.domain.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class User(
    val name: String,
    val messagesCount: Int,
    val regDate: Long,
    val commentsCount: Int,
) : Parcelable {
    fun timestamp() = TimeUnit.SECONDS.toMillis(regDate)
}
