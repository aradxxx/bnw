package im.bnw.android.data.message

import im.bnw.android.data.core.network.Api
import im.bnw.android.domain.message.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class MessageSourceImpl @Inject constructor(private val api: Api) : MessageSource {
    override fun getMessages(): Flow<List<Message>> {
        return flow {
            Timber.d(Thread.currentThread().name)
            val messages = api.getMessages(0, "").messages
            val sorted = messages.sortedByDescending { message -> message.date }
            emit(sorted)
        }
    }
}
