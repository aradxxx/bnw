package im.bnw.android.domain.auth

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.usermanager.UserManager
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    private val userManager: UserManager,
    private val dispatchersProvider: DispatchersProvider
) : AuthInteractor {
    override suspend fun login(userName: String, password: String) =
        withContext(dispatchersProvider.io) {
            try {
                userManager.login(userName, password)
                Result.Success(Unit)
            } catch (e: Throwable) {
                Result.Failure(e)
            }
        }

    override fun subscribeAuth() = userManager.isAuthenticated()
}
