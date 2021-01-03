package im.bnw.android.presentation.core.navigation

import android.content.Intent
import android.net.Uri
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import im.bnw.android.presentation.core.navigation.tab.TabsContainerFragment
import im.bnw.android.presentation.imageview.ImageFragment
import im.bnw.android.presentation.imageview.ImageScreenParams
import im.bnw.android.presentation.login.LoginFragment
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.messages.MessagesScreenParams
import im.bnw.android.presentation.splash.SplashFragment

object Screens {
    fun tabContainerScreen() = FragmentScreen() {
        TabsContainerFragment()
    }

    fun splashScreen() = FragmentScreen() {
        SplashFragment()
    }

    fun messagesScreen(user: String) = FragmentScreen() {
        MessagesFragment.newInstance(MessagesScreenParams(user))
    }

    fun imageViewScreen(url: String) = FragmentScreen() {
        ImageFragment.newInstance(ImageScreenParams(url))
    }

    fun externalHyperlinkScreen(url: String) = ActivityScreen() {
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    fun loginScreen() = FragmentScreen() {
        LoginFragment()
    }
}
