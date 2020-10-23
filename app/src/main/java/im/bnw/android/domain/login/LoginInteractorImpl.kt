package im.bnw.android.domain.login

import im.bnw.android.data.core.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LoginInteractorImpl @Inject constructor(private val api: Api) :
    LoginInteractor {
    override suspend fun auth(userName: String, password: String): String =
        withContext(Dispatchers.IO) {
            Timber.d(Thread.currentThread().name)
            api.getLogin(userName, password).token
        }
}
