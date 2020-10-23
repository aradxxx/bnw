package im.bnw.android.presentation.login

import im.bnw.android.R
import im.bnw.android.domain.login.LoginInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.DialogEvent
import im.bnw.android.presentation.core.navigation.AppRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLException

class LoginViewModel @Inject constructor(
    router: AppRouter,
    private val loginInteractor: LoginInteractor
) : BaseViewModel<LoginState>(LoginState(), router) {

    private fun authorization() {
        if (state.loading) {
            return
        }
        vmScope.launch(Dispatchers.Default) {
            updateState { it.copy(loading = true) }
            try {
                loginInteractor.auth(state.userName, state.password)
                updateState { it.copy(loading = false) }
            } catch (t: IOException) {
                handleException(t)
                updateState { it.copy(loading = false) }
            }
        }
    }

    fun userNameChanged(userName: String) {
        updateState { it.copy(userName = userName) }
    }

    fun passwordChanged(password: String) {
        updateState { it.copy(password = password) }
    }

    fun onAuthClicked() {
        authorization()
    }
}
