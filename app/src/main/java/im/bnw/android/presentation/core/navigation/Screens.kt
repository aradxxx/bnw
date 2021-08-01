package im.bnw.android.presentation.core.navigation

import android.content.Intent
import android.net.Uri
import com.github.terrakok.modo.android.AppScreen
import com.github.terrakok.modo.android.ExternalScreen
import com.github.terrakok.modo.android.MultiAppScreen
import im.bnw.android.presentation.auth.AuthFragment
import im.bnw.android.presentation.messagedetails.MessageDetailsFragment
import im.bnw.android.presentation.messagedetails.MessageDetailsScreenParams
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.messages.MessagesScreenParams
import im.bnw.android.presentation.newpost.NewPostFragment
import im.bnw.android.presentation.profile.ProfileFragment
import im.bnw.android.presentation.profile.ProfileScreenParams
import im.bnw.android.presentation.savedmessages.SavedMessagesFragment
import im.bnw.android.presentation.savedreplies.SavedRepliesFragment
import im.bnw.android.presentation.settings.SettingsFragment
import im.bnw.android.presentation.splash.SplashFragment
import im.bnw.android.presentation.user.UserFragment
import kotlinx.parcelize.Parcelize

object Screens {
    @Parcelize
    object Splash : AppScreen("Splash") {
        override fun create() = SplashFragment()
    }

    @Parcelize
    object NewPost : AppScreen("NewPost") {
        override fun create() = NewPostFragment.newInstance()
    }

    @Parcelize
    class Messages(
        val user: String = "",
        val today: Boolean = false
    ) : AppScreen("Messages_${user}_$today") {
        override fun create() = MessagesFragment.newInstance(
            MessagesScreenParams(user, today)
        )
    }

    @Parcelize
    class MessageDetails(
        val messageId: String
    ) : AppScreen("MessageDetails_$messageId") {
        override fun create() = MessageDetailsFragment.newInstance(
            MessageDetailsScreenParams(messageId)
        )
    }

    fun externalHyperlink(url: String) = ExternalScreen {
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    @Parcelize
    object User : AppScreen("User") {
        override fun create() = UserFragment.newInstance()
    }

    @Parcelize
    object Auth : AppScreen("Auth") {
        override fun create() = AuthFragment.newInstance()
    }

    @Parcelize
    class Profile(
        val user: String
    ) : AppScreen("Profile_$user") {
        override fun create() = ProfileFragment.newInstance(ProfileScreenParams(user))
    }

    @Parcelize
    object Settings : AppScreen("Settings") {
        override fun create() = SettingsFragment.newInstance()
    }

    @Parcelize
    object SavedMessages : AppScreen("SavedMessages") {
        override fun create() = SavedMessagesFragment.newInstance()
    }

    @Parcelize
    object SavedReplies : AppScreen("SavedReplies") {
        override fun create() = SavedRepliesFragment.newInstance()
    }

    fun tabs() = MultiAppScreen(
        "Tabs",
        listOf(Messages(), Messages(today = true), User),
        0
    )
}
