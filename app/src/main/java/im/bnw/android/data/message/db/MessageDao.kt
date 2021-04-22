package im.bnw.android.data.message.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    fun observeSavedMessages(filter: List<String>? = null): Flow<List<MessageEntity>> {
        return if (filter == null) {
            observeSavedMessagesAll()
        } else {
            observeSavedMessagesFilter(filter)
        }
    }

    @Query("SELECT * from message")
    fun observeSavedMessagesAll(): Flow<List<MessageEntity>>

    @Query("SELECT * from message WHERE id IN (:filter)")
    fun observeSavedMessagesFilter(filter: List<String>): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messageEntity: MessageEntity)

    @Query("DELETE FROM message WHERE id == :messageId")
    fun delete(messageId: String)
}
