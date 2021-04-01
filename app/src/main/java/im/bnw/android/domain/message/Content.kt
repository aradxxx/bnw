package im.bnw.android.domain.message

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val YOUTUBE_DOMAIN = "youtube"
const val YOUTUBE_SHORT = "youtu.be"
const val YOUTUBE_THUMB = "https://img.youtube.com/vi/%s/default.jpg"

@Parcelize
data class Media(
    val previewUrl: String,
    val fullUrl: String
) : Parcelable {
    fun isYoutube() = fullUrl.contains(YOUTUBE_DOMAIN) || fullUrl.contains(YOUTUBE_SHORT)

    fun youtubePreviewLink(): String {
        val uri = Uri.parse(fullUrl)
        val id = if (fullUrl.contains(YOUTUBE_SHORT)) {
            uri.lastPathSegment
        } else {
            uri.getQueryParameter("v") ?: ""
        }

        return String.format(YOUTUBE_THUMB, id)
    }
}
