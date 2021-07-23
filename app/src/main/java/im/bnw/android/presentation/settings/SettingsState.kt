package im.bnw.android.presentation.settings

import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.user.User
import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

sealed class SettingsState : State {
    @Parcelize
    object Loading : SettingsState()

    @Parcelize
    data class Idle(
        val settings: Settings,
        val user: User? = null,
    ) : SettingsState()
}
