package im.bnw.android.data.message.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import im.bnw.android.data.message.ContentDto
import im.bnw.android.data.message.db.Table.REPLY_TABLE
import im.bnw.android.domain.message.Media

@Entity(tableName = REPLY_TABLE)
data class ReplyEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "format")
    val format: String?,
    @ColumnInfo(name = "replyto")
    val replyTo: String?,
    @ColumnInfo(name = "replytotext")
    val replyToText: String?,
    @ColumnInfo(name = "html")
    val htmlText: String,
    @ColumnInfo(name = "media")
    val media: List<Media>,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "num")
    val num: Int,
    @ColumnInfo(name = "user")
    val user: String,
    @ColumnInfo(name = "anonymous")
    val anonymous: Boolean,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "message")
    val messageId: String,
)
