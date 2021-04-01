package im.bnw.android.domain.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reply(
    val format: String,
    val replyTo: String,
    val replyToText: String,
    val htmlText: String,
    val media: List<Media>,
    val timestamp: Long,
    val num: Int,
    val user: String,
    val anonymous: Boolean,
    val text: String,
    val id: String,
    val messageId: String,
) : Parcelable
