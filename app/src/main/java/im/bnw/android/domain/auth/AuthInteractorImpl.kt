package im.bnw.android.domain.auth

import im.bnw.android.domain.usermanager.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    private val userManager: UserManager
) : AuthInteractor {
    override suspend fun login(userName: String, password: String) =
        withContext(Dispatchers.IO) {
            userManager.login(userName, password)
        }

    override fun subscribeAuth(): Flow<Boolean> {
        return userManager.isAuthenticated()
    }
}
