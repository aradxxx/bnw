package im.bnw.android.domain.user

import im.bnw.android.domain.core.Result
import kotlinx.coroutines.flow.Flow

interface ProfileInteractor {
    suspend fun logout()
    suspend fun retry()
    suspend fun userInfo(userName: String): Result<User>
    fun subscribeUserInfo(): Flow<Result<User?>>
}
