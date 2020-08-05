package im.bnw.android.presentation.core.navigation

import androidx.fragment.app.Fragment
import im.bnw.android.presentation.core.navigation.tab.TabsContainerFragment
import im.bnw.android.presentation.login.LoginFragment
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.messages.MessagesScreenParams
import im.bnw.android.presentation.splash.SplashFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    object TabsContainer : SupportAppScreen() {
        override fun getFragment(): Fragment = TabsContainerFragment()
    }

    object Splash : SupportAppScreen() {
        override fun getFragment(): Fragment = SplashFragment()
    }

    class Messages(private val user: String) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            MessagesFragment.newInstance(MessagesScreenParams(user))
    }

    object Login : SupportAppScreen() {
        override fun getFragment(): Fragment = LoginFragment.newInstance()
    }
}
