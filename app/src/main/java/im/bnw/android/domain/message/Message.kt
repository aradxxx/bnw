package im.bnw.android.domain.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val tags: List<String>,
    val text: String,
    val user: String,
    val content: Content,
    val timestamp: Long,
    val id: String,
    val anonymous: Boolean,
    val anoncomments: Boolean,
    val replyCount: Int,
    val recommendations: List<String>
) : Parcelable
