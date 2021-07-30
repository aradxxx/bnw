package im.bnw.android.presentation.user

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.externalForward
import im.bnw.android.BuildConfig
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.user.ProfileInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.LogoutEvent
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.util.nullOr
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("TooManyFunctions")
class UserViewModel @Inject constructor(
    restoredState: UserState?,
    modo: Modo,
    private val messageInteractor: MessageInteractor,
    private val profileInteractor: ProfileInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<UserState>(
    restoredState ?: UserState.Loading,
    modo
) {
    init {
        subscribeUserInfo()
    }

    fun settingsClicked() {
        modo.externalForward(Screens.Settings)
    }

    fun loginClicked() {
        state.nullOr<UserState.Unauthorized>() ?: return
        modo.externalForward(Screens.Auth)
    }

    fun logoutClicked() {
        state.nullOr<UserState.Authorized>() ?: return
        postEvent(LogoutEvent)
    }

    fun logoutConfirmed() {
        state.nullOr<UserState.Authorized>() ?: return
        logout()
    }

    fun messagesClicked() {
        val currentState = state.nullOr<UserState.Authorized>() ?: return
        modo.externalForward(Screens.Messages(currentState.user.name))
    }

    fun retryClicked() {
        retry()
    }

    fun avatarClicked() {
        val current = state.nullOr<UserState.Authorized>() ?: return
        val imageUrl = String.format(BuildConfig.USER_AVA_URL, current.user.name)
        postEvent(OpenMediaEvent(listOf(imageUrl)))
    }

    fun savedMessagesClicked() {
        modo.externalForward(Screens.SavedMessages)
    }

    fun donateClicked(donateUrl: String) {
        modo.launch(Screens.externalHyperlink(donateUrl))
    }

    private fun logout() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { UserState.Loading }
            profileInteractor.logout()
        }
    }

    private fun retry() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { UserState.Loading }
            profileInteractor.retry()
        }
    }

    private fun subscribeUserInfo() = vmScope.launch {
        combine(
            profileInteractor.subscribeUserInfo(),
            messageInteractor.observeSavedMessages()
        ) { result, savedMessages ->
            when (result) {
                is Result.Success -> {
                    val user = result.value
                    if (user == null) {
                        UserState.Unauthorized(savedMessages.count())
                    } else {
                        UserState.Authorized(user, savedMessages.count())
                    }
                }
                is Result.Failure -> {
                    UserState.Failed(result.throwable)
                }
            }
        }
            .flowOn(dispatchersProvider.io)
            .collect { newState ->
                updateState { newState }
            }
    }
}
