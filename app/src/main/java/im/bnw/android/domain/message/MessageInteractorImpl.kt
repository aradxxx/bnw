package im.bnw.android.domain.message

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
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

    override suspend fun post(text: String, anonymous: Boolean) = withContext(dispatchersProvider.default) {
        messageRepository.post(text, anonymous)
    }

    override suspend fun messageDetails(messageId: String): Result<MessageDetails> =
        withContext(dispatchersProvider.io) {
            messageRepository.messageDetails(messageId)
        }
}
