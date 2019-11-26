package im.bnw.android.domain.message

import kotlinx.coroutines.flow.Flow

interface MessageInteractor {
    fun messages(): Flow<List<Message>>
}
