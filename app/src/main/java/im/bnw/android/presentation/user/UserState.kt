package im.bnw.android.presentation.user

import im.bnw.android.domain.user.User
import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

sealed class UserState : State {
    @Parcelize
    object Loading : UserState()

    @Parcelize
    data class Failed(
        val throwable: Throwable
    ) : UserState()

    @Parcelize
    data class Authorized(
        val user: User,
        val savedMessagesCount: Int,
    ) : UserState()

    @Parcelize
    data class Unauthorized(
        val savedMessagesCount: Int,
    ) : UserState()
}
