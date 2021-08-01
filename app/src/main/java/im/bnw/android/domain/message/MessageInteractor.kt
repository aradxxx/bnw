package im.bnw.android.domain.message

import im.bnw.android.domain.core.Result
import kotlinx.coroutines.flow.Flow

interface MessageInteractor {
    suspend fun messages(before: String, user: String, today: Boolean): Result<List<Message>>
    suspend fun post(text: String, anonymous: Boolean): Result<Unit>
    suspend fun messageDetails(messageId: String): Result<MessageDetails>
    suspend fun reply(text: String, messageId: String, replyTo: String, anonymous: Boolean): Result<Unit>
    fun observeSavedMessages(filter: List<String>? = null): Flow<List<Message>>
    suspend fun save(message: Message)
    suspend fun remove(message: Message)
    fun observeSavedReplies(filter: List<String>? = null): Flow<List<Reply>>
    suspend fun save(reply: Reply)
    suspend fun remove(reply: Reply)
}
