package im.bnw.android.presentation.splash

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    router: AppRouter
) : BaseViewModel<SplashState>(SplashState(), router) {
    override fun action(action: Any?) {
        super.action(action)
        when (action) {
            Action.TestClick -> router.navigateTo(Tab.GLOBAL, Screens.Splash)
        }
    }
}
