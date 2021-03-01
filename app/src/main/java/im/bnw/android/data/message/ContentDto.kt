package im.bnw.android.data.message

import android.os.Parcelable
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
data class ContentDto(
    val text: String,
    val mediaDto: List<MediaDto>
) : Parcelable

@Parcelize
data class MediaDto(
    val previewUrl: String,
    val fullUrl: String
) : Parcelable

object ContentDtoDeserializer : JsonDeserializer<ContentDto> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ContentDto {
        var text = ""
        val media: MutableList<MediaDto> = mutableListOf()

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
                    MediaDto(
                        previewUrl,
                        fullUrl
                    )
                )
            }
        }
        return ContentDto(text, media)
    }
}
