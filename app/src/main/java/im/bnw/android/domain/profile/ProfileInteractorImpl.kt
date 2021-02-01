package im.bnw.android.domain.profile

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.usermanager.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileInteractorImpl @Inject constructor(
    private val userManager: UserManager
) : ProfileInteractor {
    private val retry = MutableStateFlow(false)

    override suspend fun logout() = withContext(Dispatchers.IO) {
        userManager.logout()
    }

    override suspend fun retry() = withContext(Dispatchers.IO) {
        retry.value = !retry.value
    }

    @ExperimentalCoroutinesApi
    override fun subscribeUserInfo(): Flow<Result<User?>> {
        return retry.flatMapLatest {
            userManager.userInfo()
        }
    }
}
