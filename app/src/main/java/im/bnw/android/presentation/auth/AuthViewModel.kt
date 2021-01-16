package im.bnw.android.presentation.auth

import im.bnw.android.R
import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.DialogEvent
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.AuthFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    router: AppRouter,
    private val authInteractor: AuthInteractor
) : BaseViewModel<AuthState>(
    AuthState(),
    router
) {
    init {
        subscribeAuth()
    }

    override fun handleException(e: Throwable) {
        when (e) {
            is AuthFailedException -> postEvent(DialogEvent(R.string.auth_failed))
            else -> super.handleException(e)
        }
    }

    fun onLoginChanged(userName: String) {
        updateState { it.copy(userName = userName) }
    }

    fun onPasswordChanged(password: String) {
        updateState { it.copy(password = password) }
    }

    fun onSignInClicked() {
        if (state.loading) {
            return
        }
        vmScope.launch(Dispatchers.Default) {
            updateState { it.copy(loading = true) }
            try {
                authInteractor.login(state.userName, state.password)
                updateState { it.copy(loading = false) }
            } catch (t: IOException) {
                handleException(t)
                updateState { it.copy(loading = false) }
            }
        }
    }

    private fun subscribeAuth() = vmScope.launch {
        authInteractor.subscribeAuth()
            .flowOn(Dispatchers.IO)
            .collect { authorized ->
                updateState { it.copy(authorized = authorized) }
            }
    }
}
