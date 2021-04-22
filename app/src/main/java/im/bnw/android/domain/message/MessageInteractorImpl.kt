package im.bnw.android.domain.message

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val messageRepository: MessageRepository,
    private val dispatchersProvider: DispatchersProvider
) : MessageInteractor {
    override suspend fun messages(after: String, before: String, user: String): List<Message> =
        withContext(dispatchersProvider.default) {
            val messages = messageRepository.messages(after, before, user)
            val sorted = messages.sortedByDescending { message -> message.timestamp }
            sorted
        }

    override suspend fun post(text: String, anonymous: Boolean): Result<Unit> {
        return withContext(dispatchersProvider.default) {
            messageRepository.post(text, anonymous)
        }
    }

    override suspend fun messageDetails(messageId: String): Result<MessageDetails> =
        withContext(dispatchersProvider.io) {
            messageRepository.messageDetails(messageId)
        }

    override suspend fun reply(text: String, messageId: String, replyTo: String, anonymous: Boolean): Result<Unit> {
        return withContext(dispatchersProvider.default) {
            val id = if (replyTo.isEmpty()) {
                messageId
            } else {
                replyTo
            }
            messageRepository.reply(text, id, anonymous)
        }
    }

    override fun observeSavedMessages(filter: List<String>?): Flow<List<Message>> {
        return messageRepository.observeSavedMessages(filter)
    }

    override suspend fun save(message: Message) {
        return withContext(dispatchersProvider.default) {
            messageRepository.save(message)
        }
    }

    override suspend fun remove(message: Message) {
        return withContext(dispatchersProvider.default) {
            messageRepository.remove(message)
        }
    }
}
