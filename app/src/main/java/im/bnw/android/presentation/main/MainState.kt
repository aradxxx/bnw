package im.bnw.android.presentation.main

import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

sealed class MainState : State {
    @Parcelize
    object Init : MainState()

    @Parcelize
    data class Main(val theme: ThemeSettings) : MainState()
}
