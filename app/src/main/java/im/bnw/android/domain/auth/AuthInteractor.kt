package im.bnw.android.domain.auth

import kotlinx.coroutines.flow.Flow
import im.bnw.android.domain.core.Result

interface AuthInteractor {
    suspend fun login(userName: String, password: String): Result<Unit>
    fun subscribeAuth(): Flow<Boolean>
}
