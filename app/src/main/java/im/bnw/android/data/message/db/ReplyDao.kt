package im.bnw.android.data.message.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.bnw.android.data.message.db.Table.REPLY_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface ReplyDao {
    fun observeSavedReplies(filter: List<String>? = null): Flow<List<ReplyEntity>> {
        return if (filter == null) {
            observeSavedRepliesAll()
        } else {
            observeSavedRepliesFilter(filter)
        }
    }

    @Query("SELECT * from $REPLY_TABLE")
    fun observeSavedRepliesAll(): Flow<List<ReplyEntity>>

    @Query("SELECT * from $REPLY_TABLE WHERE id IN (:filter)")
    fun observeSavedRepliesFilter(filter: List<String>): Flow<List<ReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(replyEntity: ReplyEntity)

    @Query("DELETE FROM $REPLY_TABLE WHERE id == :replyId")
    fun delete(replyId: String)
}
