package im.bnw.android.presentation.profile

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.profile.ProfileInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import im.bnw.android.presentation.util.nullOr
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    router: AppRouter,
    restoredState: ProfileState?,
    private val profileInteractor: ProfileInteractor,
    private val dispatchersProvider: DispatchersProvider
) : BaseViewModel<ProfileState>(
    restoredState ?: ProfileState.Init,
    router
) {
    init {
        updateState { ProfileState.Loading }
        subscribeUserInfo()
    }

    fun loginClicked() {
        state.nullOr<ProfileState.Unauthorized>() ?: return
        router.navigateTo(Tab.GLOBAL, Screens.authScreen())
    }

    fun logoutConfirmed() {
        state.nullOr<ProfileState.ProfileInfo>() ?: return
        logout()
    }

    fun messagesClicked() {
        val currentState = state.nullOr<ProfileState.ProfileInfo>() ?: return
        router.navigateTo(Tab.GLOBAL, Screens.messagesScreen(currentState.user.name))
    }

    fun retryClicked() {
        state.nullOr<ProfileState.LoadingFailed>() ?: return
        retry()
    }

    private fun logout() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { ProfileState.Loading }
            profileInteractor.logout()
        }
    }

    private fun retry() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { ProfileState.Loading }
            profileInteractor.retry()
        }
    }

    private fun subscribeUserInfo() = vmScope.launch {
        profileInteractor.subscribeUserInfo()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val user = result.value
                        if (user == null) {
                            ProfileState.Unauthorized
                        } else {
                            ProfileState.ProfileInfo(user)
                        }
                    }
                    is Result.Failure -> {
                        ProfileState.LoadingFailed(result.throwable)
                    }
                }
            }
            .flowOn(dispatchersProvider.io)
            .collect { newState ->
                updateState { newState }
            }
    }
}
