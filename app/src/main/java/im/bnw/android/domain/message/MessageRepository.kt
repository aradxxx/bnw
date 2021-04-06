package im.bnw.android.domain.message

import im.bnw.android.domain.core.Result

interface MessageRepository {
    suspend fun messages(after: String, before: String, user: String): List<Message>
    suspend fun post(text: String, anonymous: Boolean): Result<Unit>
    suspend fun messageDetails(messageId: String): Result<MessageDetails>
    suspend fun reply(text: String, messageId: String, anonymous: Boolean): Result<Unit>
}
