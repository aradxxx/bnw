package im.bnw.android.data.core.db

import androidx.room.TypeConverter
import im.bnw.android.domain.message.Media

const val DELIMITER_ENTITIES = ";"
const val DELIMITER_VALUES = ","

class Converters {
    @TypeConverter
    fun stringListToString(strings: List<String>): String {
        return strings.joinToString(separator = DELIMITER_ENTITIES)
    }

    @TypeConverter
    fun stringToStringList(string: String): List<String> {
        if (string.isEmpty()) {
            return emptyList()
        }
        return string.split(DELIMITER_ENTITIES)
    }

    @TypeConverter
    fun mediaListToString(media: List<Media>): String {
        return media.joinToString(DELIMITER_ENTITIES) {
            "${it.previewUrl}$DELIMITER_VALUES${it.fullUrl}"
        }
    }

    @TypeConverter
    fun stringToMediaList(string: String): List<Media> {
        if (string.isEmpty()) {
            return emptyList()
        }
        return string.split(DELIMITER_ENTITIES).mapNotNull {
            val splitted = it.split(DELIMITER_VALUES)
            if (splitted.size == 2) {
                Media(splitted[0], splitted[1])
            } else {
                null
            }
        }
    }
}
