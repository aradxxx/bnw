package im.bnw.android.presentation.profile

import im.bnw.android.domain.user.User
import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

sealed class ProfileState : State {
    @Parcelize
    object Init : ProfileState()

    @Parcelize
    object Loading : ProfileState()

    @Parcelize
    data class LoadingFailed(
        val throwable: Throwable
    ) : ProfileState()

    @Parcelize
    data class ProfileInfo(
        val user: User,
    ) : ProfileState()
}
