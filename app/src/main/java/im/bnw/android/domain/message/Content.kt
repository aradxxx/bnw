package im.bnw.android.domain.message

import android.net.Uri
import android.os.Parcelable
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

private const val YOUTUBE_DOMAIN = "youtube"
private const val YOUTUBE_SHORT = "youtu.be"
private const val YOUTUBE_THUMB = "https://img.youtube.com/vi/%s/default.jpg"

@Parcelize
data class Content(
    val text: String,
    val media: List<Media>
) : Parcelable

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

class ContentDeserializer : JsonDeserializer<Content> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Content {
        var text = ""
        val media: MutableList<Media> = mutableListOf()

        val secureRaw = json.asJsonObject.getAsJsonArray("secure")
        if (secureRaw.size() == 2) {
            text = secureRaw.get(0).asString
        }

        val contents = secureRaw.get(1).asJsonArray
        if (contents.size() > 0) {
            for (array in contents) {
                val previewUrl = array.asJsonArray.get(1).asString
                val fullUrl = array.asJsonArray.get(0).asString
                media.add(
                    Media(
                        previewUrl,
                        fullUrl
                    )
                )
            }
        }
        return Content(text, media)
    }
}
