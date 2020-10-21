package im.bnw.android.presentation.core.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import im.bnw.android.presentation.core.navigation.tab.TabsContainerFragment
import im.bnw.android.presentation.imageview.ImageFragment
import im.bnw.android.presentation.imageview.ImageScreenParams
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

    data class Messages(private val user: String) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            MessagesFragment.newInstance(MessagesScreenParams(user))
    }

    data class ImageView(private val url: String) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            ImageFragment.newInstance(ImageScreenParams(url))
    }

    data class ExternalHyperlink(val url: String) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    object Login : SupportAppScreen() {
        override fun getFragment(): Fragment = LoginFragment.newInstance()
    }
}
