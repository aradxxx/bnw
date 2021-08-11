package im.bnw.android.domain.usermanager

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.user.User
import kotlinx.coroutines.flow.Flow

interface UserManager {
    suspend fun login(userName: String, password: String)
    suspend fun logout()
    suspend fun getUserInfo(userName: String): Result<User>
    fun userInfo(): Flow<Result<User?>>
    fun isAuthenticated(): Flow<Boolean>
    fun getUserName(): Flow<String>
    fun subscribeToken(): Flow<String>
    suspend fun saveDraft(text: String)
    suspend fun deleteDraft()
    suspend fun draft(): Result<String>
}
