package im.bnw.android.presentation.user

import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.user.User
import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

sealed class UserState : State {
    @Parcelize
    object Init : UserState()

    @Parcelize
    object Loading : UserState()

    @Parcelize
    data class LoadingFailed(
        val throwable: Throwable
    ) : UserState()

    @Parcelize
    data class Unauthorized(
        val savedMessagesCount: Int,
        val settings: Settings,
    ) : UserState()

    @Parcelize
    data class UserInfo(
        val user: User,
        val savedMessagesCount: Int,
        val settings: Settings,
    ) : UserState()
}
