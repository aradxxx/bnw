package im.bnw.android.domain.message

import kotlinx.coroutines.flow.Flow

interface MessageInteractor {
    fun messages(after: String, before: String, user: String): Flow<List<Message>>
}
