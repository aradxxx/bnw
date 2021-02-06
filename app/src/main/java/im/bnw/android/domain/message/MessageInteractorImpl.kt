package im.bnw.android.domain.message

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val messageRepository: MessageRepository
) : MessageInteractor {
    override suspend fun messages(after: String, before: String, user: String): List<Message> =
        withContext(Dispatchers.Default) {
            val messages = messageRepository.messages(after, before, user)
            val sorted = messages.sortedByDescending { message -> message.timestamp() }
            sorted
        }

    override suspend fun post(text: String, anonymous: Boolean) = withContext(Dispatchers.Default) {
        messageRepository.post(text, anonymous)
    }
}
