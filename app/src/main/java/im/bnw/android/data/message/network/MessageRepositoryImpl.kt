package im.bnw.android.data.message.network

import com.squareup.moshi.JsonDataException
import im.bnw.android.data.core.db.AppDb
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.core.network.httpresult.asFailure
import im.bnw.android.data.core.network.httpresult.baseResponseToResult
import im.bnw.android.data.core.network.httpresult.isFailure
import im.bnw.android.data.core.network.httpresult.toResult
import im.bnw.android.data.message.MessageMapper.toMessage
import im.bnw.android.data.message.MessageMapper.toMessageEntity
import im.bnw.android.data.message.MessageMapper.toReply
import im.bnw.android.data.message.MessageMapper.toReplyEntity
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageDetails
import im.bnw.android.domain.message.MessageRepository
import im.bnw.android.domain.message.Reply
import im.bnw.android.presentation.util.PostNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("TooManyFunctions")
class MessageRepositoryImpl @Inject constructor(
    private val api: Api,
    private val appDb: AppDb,
    private val dispatchersProvider: DispatchersProvider
) : MessageRepository {
    override suspend fun messages(before: String, user: String, today: Boolean): Result<List<Message>> {
        return when {
            !today -> {
                messages(before, user)
            }
            today && before.isEmpty() -> {
                todayMessages()
            }
            else -> {
                Result.Success(emptyList())
            }
        }
    }

    override suspend fun post(text: String, anonymous: Boolean): Result<Unit> = withContext(dispatchersProvider.io) {
        return@withContext api.post(text, anonymous.asApiParam()).toResult { }
    }

    override suspend fun messageDetails(messageId: String): Result<MessageDetails> =
        withContext(dispatchersProvider.io) {
            val result = api.messageDetail(messageId)
            return@withContext if (result.isFailure() && result.asFailure().error is JsonDataException) {
                Result.Failure(PostNotFoundException)
            } else {
                result.toResult { messageDetailsDto ->
                    MessageDetails(
                        messageId,
                        messageDetailsDto.message.toMessage(),
                        messageDetailsDto.replies.map { it.toReply() }
                    )
                }
            }
        }

    override suspend fun reply(text: String, messageId: String, anonymous: Boolean): Result<Unit> {
        return withContext(dispatchersProvider.io) {
            return@withContext api.comment(messageId, text, anonymous.asApiParam()).baseResponseToResult()
        }
    }

    override fun observeSavedMessages(filter: List<String>?): Flow<List<Message>> {
        return appDb.messageDao().observeSavedMessages(filter).map { list ->
            list.map {
                it.toMessage()
            }
        }
    }

    override suspend fun save(message: Message) {
        return withContext(dispatchersProvider.io) {
            appDb.messageDao().insert(message.toMessageEntity())
        }
    }

    override suspend fun remove(message: Message) {
        return withContext(dispatchersProvider.io) {
            appDb.messageDao().delete(message.id)
        }
    }

    override fun observeSavedReplies(filter: List<String>?): Flow<List<Reply>> {
        return appDb.replyDao().observeSavedReplies(filter).map { list ->
            list.map {
                it.toReply()
            }
        }
    }

    override suspend fun save(reply: Reply) {
        return withContext(dispatchersProvider.io) {
            appDb.replyDao().insert(reply.toReplyEntity())
        }
    }

    override suspend fun remove(reply: Reply) {
        return withContext(dispatchersProvider.io) {
            appDb.replyDao().delete(reply.id)
        }
    }

    private suspend fun messages(before: String, user: String): Result<List<Message>> =
        withContext(dispatchersProvider.io) {
            return@withContext api.messages(before, user).toResult { response ->
                response.messages.map { it.toMessage() }
            }
        }

    private suspend fun todayMessages(): Result<List<Message>> =
        withContext(dispatchersProvider.io) {
            return@withContext api.today().toResult { response ->
                response.messages.map { it.toMessage() }
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
