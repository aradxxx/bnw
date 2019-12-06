package im.bnw.android.domain.message

import android.os.Parcelable
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Type

@Parcelize
data class Content(
    val text: String,
    val media: List<Media>
) : Parcelable

@Parcelize
data class Media(
    val previewUrl: String,
    val fullUrl: String
) : Parcelable

class ContentDeserializer : JsonDeserializer<Content> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Content {
        var text = ""
        var media: MutableList<Media> = mutableListOf()

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
