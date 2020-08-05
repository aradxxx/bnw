package im.bnw.android.presentation.main

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import ru.aradxxx.ciceronetabs.TabCicerone
import javax.inject.Inject

class MainViewModel @Inject constructor(
    cicerone: TabCicerone<AppRouter>,
    restoredState: MainState?
) : BaseViewModel<MainState>(
    restoredState ?: MainState(),
    cicerone.activityRouter()
) {
    init {
        router.newRootScreen(Screens.TabsContainer)
        router.switchTab(Tab.General)
        router.newRootScreen(Tab.General, Screens.Messages(""))
        router.newRootScreen(Tab.About, Screens.Login)
    }
}
