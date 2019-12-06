package im.bnw.android.domain.message

import im.bnw.android.data.core.network.Api
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(private val api: Api) :
    MessageInteractor {
    override fun messages(after: String, before: String, user: String): Flow<List<Message>> {
        return flow {
            Timber.d(Thread.currentThread().name)
            val messages = api.getMessages(after, before, user).messages
            val sorted = messages.sortedByDescending { message -> message.timestamp() }
            emit(sorted)
        }
    }
}
