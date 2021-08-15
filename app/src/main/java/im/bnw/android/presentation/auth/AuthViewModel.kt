package im.bnw.android.presentation.auth

import com.github.terrakok.modo.Modo
import im.bnw.android.R
import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.domain.core.Result
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.DialogEvent
import im.bnw.android.presentation.util.AuthFailedException
import javax.inject.Inject
import kotlinx.coroutines.launch

class AuthViewModel @Inject constructor(
    restoredState: AuthState?,
    modo: Modo,
    private val authInteractor: AuthInteractor,
) : BaseViewModel<AuthState>(
    restoredState ?: AuthState(),
    modo
) {
    override fun handleException(e: Throwable) = when (e) {
        is AuthFailedException -> postEvent(DialogEvent(R.string.auth_failed))
        else -> super.handleException(e)
    }

    fun loginChanged(userName: String) {
        if (state.loading) {
            return
        }
        updateState { it.copy(userName = userName) }
    }

    fun passwordChanged(password: String) {
        if (state.loading) {
            return
        }
        updateState { it.copy(password = password) }
    }

    fun signInClicked() {
        if (state.loading) {
            return
        }
        vmScope.launch {
            updateState { it.copy(loading = true) }
            when (val result = authInteractor.login(state.userName, state.password)) {
                is Result.Success -> {
                    backPressed()
                }
                is Result.Failure -> {
                    handleException(result.throwable)
                    updateState { it.copy(loading = false) }
                }
            }
        }
    }
}
