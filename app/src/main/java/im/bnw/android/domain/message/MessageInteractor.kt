package im.bnw.android.domain.message

import im.bnw.android.data.core.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MessageInteractor @Inject constructor(private val api: Api) {
    suspend fun messages(after: String, before: String, user: String): List<Message> =
        withContext(Dispatchers.IO) {
            Timber.d(Thread.currentThread().name)
            val messages = api.getMessages(after, before, user).messages
            val sorted = messages.sortedByDescending { message -> message.timestamp() }
            sorted
        }
}
