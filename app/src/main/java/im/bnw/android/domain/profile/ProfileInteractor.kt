package im.bnw.android.domain.profile

import im.bnw.android.domain.core.Result
import kotlinx.coroutines.flow.Flow

interface ProfileInteractor {
    suspend fun logout()
    suspend fun retry()
    fun subscribeUserInfo(): Flow<Result<User?>>
}
