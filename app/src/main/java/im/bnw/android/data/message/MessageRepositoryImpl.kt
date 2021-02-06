package im.bnw.android.data.message

import im.bnw.android.data.core.network.Api
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageRepository
import im.bnw.android.domain.usermanager.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api: Api,
    private val userManager: UserManager
) : MessageRepository {
    override suspend fun messages(after: String, before: String, user: String): List<Message> =
        withContext(Dispatchers.IO) {
            val response = api.messages(after, before, user)
            if (response.ok) {
                response.messages
            } else {
                // not io
                throw IOException("Get messages failed")
            }
        }

    override suspend fun post(text: String, anonymous: Boolean) = withContext(Dispatchers.IO) {
        val token = userManager.subscribeToken().firstOrNull()
        val response = api.post(text, token, anonymous.asApiParam())
        if (!response.ok) {
            throw IOException("Post failed")
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
