package im.bnw.android.presentation.main

import com.github.aradxxx.ciceroneflow.FlowCicerone
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import javax.inject.Inject

class MainViewModel @Inject constructor(
    cicerone: FlowCicerone<AppRouter>,
    restoredState: MainState?
) : BaseViewModel<MainState>(
    restoredState ?: MainState(),
    cicerone.mainRouter()
) {
    init {
        router.newRootScreen(Screens.tabContainerScreen())
        router.switchTab(Tab.General)
        router.newRootScreen(Tab.General, Screens.messagesScreen(""))
        router.newRootScreen(Tab.About, Screens.loginScreen())
    }
}
