package im.bnw.android.domain.message

import im.bnw.android.domain.core.Result

interface MessageInteractor {
    suspend fun messages(after: String, before: String, user: String): List<Message>
    suspend fun post(text: String, anonymous: Boolean)
    suspend fun messageDetails(messageId: String): Result<MessageDetails>
}
