package im.bnw.android.data.message

import im.bnw.android.domain.message.Message
import kotlinx.coroutines.flow.Flow

interface MessageSource {
    fun getMessages(): Flow<List<Message>>
}
