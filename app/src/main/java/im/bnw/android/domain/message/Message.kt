package im.bnw.android.domain.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val tags: List<String>,
    val text: String,
    val user: String,
    val htmlText: String,
    val media: List<Media>,
    val timestamp: Long,
    val id: String,
    val anonymous: Boolean,
    val anonComments: Boolean,
    val replyCount: Int,
    val recommendations: List<String>,
    val format: String,
    val clubs: List<String>,
) : Parcelable
