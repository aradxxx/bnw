package im.bnw.android.data.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import im.bnw.android.BuildConfig
import im.bnw.android.data.message.db.MessageDao
import im.bnw.android.data.message.db.MessageEntity
import im.bnw.android.data.message.db.ReplyDao
import im.bnw.android.data.message.db.ReplyEntity

@Database(
    entities = [
        MessageEntity::class,
        ReplyEntity::class,
    ],
    exportSchema = false,
    version = BuildConfig.DB_VERSION
)
@TypeConverters(
    Converters::class
)
abstract class AppDb : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun replyDao(): ReplyDao
}
