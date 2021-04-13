package im.bnw.android.presentation.profile

import im.bnw.android.domain.profile.User
import im.bnw.android.domain.settings.Settings
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
    data class Unauthorized(
        val settings: Settings,
    ) : ProfileState()

    @Parcelize
    data class ProfileInfo(
        val user: User,
        val settings: Settings,
    ) : ProfileState()
}
