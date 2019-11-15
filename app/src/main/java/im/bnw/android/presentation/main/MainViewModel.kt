package im.bnw.android.presentation.main

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.tab.Tab
import im.bnw.android.presentation.core.tab.TabScreen
import im.bnw.android.presentation.core.tab.TabsContainerScreen
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val restoredState: MainState?,
    private val router: AppRouter
) : BaseViewModel<MainState>(
    restoredState ?: MainState()
) {
    init {
        router.newRootScreen(TabsContainerScreen())
        router.switchTab(TabScreen(Tab.General.tag()))
    }
}