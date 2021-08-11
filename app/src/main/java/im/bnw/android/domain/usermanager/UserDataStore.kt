package im.bnw.android.domain.usermanager

import kotlinx.coroutines.flow.Flow

interface UserDataStore {
    suspend fun updateUserToken(token: String)
    fun subscribeUserToken(): Flow<String>
    suspend fun updateUserName(userName: String)
    fun subscribeUserName(): Flow<String>
    suspend fun updatePostDraft(text: String)
    fun subscribePostDraft(): Flow<String>
}
