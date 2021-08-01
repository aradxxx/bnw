package im.bnw.android.data.message.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import im.bnw.android.data.message.db.Table.MESSAGE_TABLE
import im.bnw.android.domain.message.Media

@Entity(tableName = MESSAGE_TABLE)
data class MessageEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "tags")
    val tags: List<String>,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "user")
    val user: String,
    @ColumnInfo(name = "htmlText")
    val htmlText: String,
    @ColumnInfo(name = "media")
    val media: List<Media>,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "anonymous")
    val anonymous: Boolean,
    @ColumnInfo(name = "anoncomments")
    val anonComments: Boolean,
    @ColumnInfo(name = "replycount")
    val replyCount: Int,
    @ColumnInfo(name = "recommendations")
    val recommendations: List<String>,
    @ColumnInfo(name = "format")
    val format: String,
    @ColumnInfo(name = "clubs")
    val clubs: List<String>,
)
