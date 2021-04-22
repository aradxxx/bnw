package im.bnw.android.domain.message

import im.bnw.android.domain.core.Result
import kotlinx.coroutines.flow.Flow

interface MessageInteractor {
    suspend fun messages(after: String, before: String, user: String): List<Message>
    suspend fun post(text: String, anonymous: Boolean): Result<Unit>
    suspend fun messageDetails(messageId: String): Result<MessageDetails>
    suspend fun reply(text: String, messageId: String, replyTo: String, anonymous: Boolean): Result<Unit>
    fun observeSavedMessages(filter: List<String>? = null): Flow<List<Message>>
    suspend fun save(message: Message)
    suspend fun remove(message: Message)
}
