package im.bnw.android.domain.usermanager

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.profile.User
import kotlinx.coroutines.flow.Flow

interface UserManager {
    suspend fun login(userName: String, password: String)
    suspend fun logout()
    fun userInfo(): Flow<Result<User?>>
    fun isAuthenticated(): Flow<Boolean>
    fun getUserName(): Flow<String>
    fun subscribeToken(): Flow<String>
}
