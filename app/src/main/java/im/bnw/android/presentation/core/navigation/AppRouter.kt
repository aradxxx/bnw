package im.bnw.android.presentation.core.navigation

import com.github.aradxxx.ciceroneflow.FlowRouter
import com.github.terrakok.cicerone.Screen
import im.bnw.android.presentation.core.navigation.tab.Tab

class AppRouter : FlowRouter() {
    fun backToRoot(tab: Tab) {
        super.backTo(tab.screenKey(), null)
    }

    fun switchTab(tab: Tab) {
        super.switch(tab.screen())
    }

    fun backTo(tab: Tab, screen: Screen?) {
        super.backTo(tab.screenKey(), screen)
    }

    fun exit(tab: Tab) {
        super.exit(tab.screenKey())
    }

    fun navigateTo(tab: Tab, screen: Screen) {
        super.navigateTo(tab.screenKey(), screen, true)
    }

    fun newRootScreen(tab: Tab, screen: Screen) {
        super.newRootScreen(tab.screenKey(), screen)
    }

    fun replaceScreen(tab: Tab, screen: Screen) {
        super.replaceScreen(tab.screenKey(), screen)
    }
}
