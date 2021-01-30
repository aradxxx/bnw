package im.bnw.android.domain.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val messagesCount: Int,
    val commentsCount: Int,
) : Parcelable
