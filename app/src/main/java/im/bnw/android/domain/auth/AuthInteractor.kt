package im.bnw.android.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthInteractor {
    suspend fun login(userName: String, password: String)
    suspend fun logout()
    fun subscribeAuth(): Flow<Boolean>
}
