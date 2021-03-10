package im.bnw.android.data.message

import android.os.Parcelable
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import kotlinx.parcelize.Parcelize

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

class ContentDtoDeserializer : JsonAdapter<ContentDto>() {
    override fun toJson(writer: JsonWriter, value: ContentDto?) {
        // no op
    }

    override fun fromJson(reader: JsonReader): ContentDto {
        var text = ""
        val media: MutableList<MediaDto> = mutableListOf()

        reader.beginObject()
        while (reader.hasNext()) {
            if (reader.nextName().equals("secure")) {
                reader.beginArray()
                // put this html text as message text instead text from response
                text = reader.nextString()
                reader.beginArray()
                while (reader.hasNext()) {
                    reader.beginArray()
                    media.add(
                        MediaDto(
                            fullUrl = reader.nextString(),
                            previewUrl = reader.nextString(),
                        )
                    )
                    reader.endArray()
                }
                reader.endArray()
                reader.endArray()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()

        return ContentDto(text, media)
    }
}
