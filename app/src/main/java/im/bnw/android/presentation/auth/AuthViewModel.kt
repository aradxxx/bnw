package im.bnw.android.presentation.auth

import im.bnw.android.R
import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.DialogEvent
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.AuthFailedException
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    router: AppRouter,
    private val authInteractor: AuthInteractor,
    private val dispatchersProvider: DispatchersProvider
) : BaseViewModel<AuthState>(
    AuthState(),
    router
) {
    override fun handleException(e: Throwable) {
        when (e) {
            is AuthFailedException -> postEvent(DialogEvent(R.string.auth_failed))
            else -> super.handleException(e)
        }
    }

    fun loginChanged(userName: String) {
        updateState { it.copy(userName = userName) }
    }

    fun passwordChanged(password: String) {
        updateState { it.copy(password = password) }
    }

    fun signInClicked() {
        if (state.loading) {
            return
        }
        vmScope.launch(dispatchersProvider.default) {
            updateState { it.copy(loading = true) }
            try {
                authInteractor.login(state.userName, state.password)
                authSuccess()
            } catch (t: IOException) {
                handleException(t)
                updateState { it.copy(loading = false) }
            }
        }
    }

    private fun authSuccess() = vmScope.launch {
        backPressed()
    }
}
