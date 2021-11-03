package im.bnw.android.presentation.profile

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.externalForward
import im.bnw.android.BuildConfig
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.MessageMode
import im.bnw.android.domain.user.ProfileInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.util.nullOr
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    restoredState: ProfileState?,
    modo: Modo,
    private val screenParams: ProfileScreenParams,
    private val profileInteractor: ProfileInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<ProfileState>(
    restoredState ?: ProfileState.Loading,
    modo
) {
    init {
        getProfile(screenParams.userName)
    }

    fun messagesClicked() {
        val currentState = state.nullOr<ProfileState.ProfileInfo>() ?: return
        modo.externalForward(Screens.Messages(user = currentState.user.name, mode = MessageMode.All))
    }

    fun retryClicked() {
        state.nullOr<ProfileState.Failed>() ?: return
        getProfile(screenParams.userName)
    }

    private fun getProfile(userName: String) {
        vmScope.launch(dispatchersProvider.default) {
            updateState { ProfileState.Loading }
            when (val result = profileInteractor.userInfo(userName)) {
                is Result.Success -> updateState {
                    ProfileState.ProfileInfo(result.value)
                }
                is Result.Failure -> updateState {
                    ProfileState.Failed(result.throwable)
                }
            }
        }
    }

    fun avatarClicked() {
        val current = state.nullOr<ProfileState.ProfileInfo>() ?: return
        val imageUrl = String.format(BuildConfig.USER_AVA_URL, current.user.name)
        postEvent(OpenMediaEvent(listOf(imageUrl)))
    }
}
