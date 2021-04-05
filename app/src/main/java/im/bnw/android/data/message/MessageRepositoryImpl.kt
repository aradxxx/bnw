package im.bnw.android.data.message

import im.bnw.android.data.core.network.Api
import im.bnw.android.data.core.network.httpresult.toResult
import im.bnw.android.data.message.MessageMapper.toMessage
import im.bnw.android.data.message.MessageMapper.toReply
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageDetails
import im.bnw.android.domain.message.MessageRepository
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api: Api,
    private val dispatchersProvider: DispatchersProvider
) : MessageRepository {
    override suspend fun messages(after: String, before: String, user: String): List<Message> =
        withContext(dispatchersProvider.io) {
            val response = api.messages(after, before, user)
            if (response.ok) {
                response.messages.map { it.toMessage() }
            } else {
                // not io
                throw IOException("Get messages failed")
            }
        }

    override suspend fun post(text: String, anonymous: Boolean) = withContext(dispatchersProvider.io) {
        val response = api.post(text, anonymous.asApiParam())
        if (!response.ok) {
            throw IOException("Post failed")
        }
    }

    override suspend fun messageDetails(messageId: String): Result<MessageDetails> =
        withContext(dispatchersProvider.io) {
            return@withContext api.messageDetail(messageId).toResult { messageDetailsDto ->
                MessageDetails(
                    messageId,
                    messageDetailsDto.message.toMessage(),
                    messageDetailsDto.replies.map { it.toReply() }
                )
            }
        }

    private fun Boolean.asApiParam(): String = when (this) {
        true -> {
            this.toString()
        }
        false -> {
            ""
        }
    }
}
