package im.bnw.android.domain.usermanager

import kotlinx.coroutines.flow.Flow

interface UserManager {
    suspend fun login(userName: String, password: String)
    suspend fun logout()
    fun isAuthenticated(): Flow<Boolean>
}
