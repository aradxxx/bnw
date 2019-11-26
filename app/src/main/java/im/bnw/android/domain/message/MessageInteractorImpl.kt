package im.bnw.android.domain.message

import im.bnw.android.data.message.MessageSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(private val messageSource: MessageSource) :
    MessageInteractor {
    override fun messages(): Flow<List<Message>> {
        return messageSource.getMessages()
    }
}
