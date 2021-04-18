package im.bnw.android.domain.user

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.usermanager.UserManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileInteractorImpl @Inject constructor(
    private val userManager: UserManager,
    private val dispatchersProvider: DispatchersProvider
) : ProfileInteractor {
    private val retry = MutableStateFlow(false)

    override suspend fun logout() = withContext(dispatchersProvider.io) {
        userManager.logout()
    }

    override suspend fun retry() = withContext(dispatchersProvider.io) {
        retry.value = !retry.value
    }

    override suspend fun userInfo(userName: String): Result<User> {
        return userManager.getUserInfo(userName)
    }

    @ExperimentalCoroutinesApi
    override fun subscribeUserInfo(): Flow<Result<User?>> {
        return retry.flatMapLatest {
            userManager.userInfo()
        }
    }
}
