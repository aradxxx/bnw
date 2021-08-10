package im.bnw.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import im.bnw.android.di.auth.AuthFragmentModule
import im.bnw.android.di.message.MessagesFragmentModule
import im.bnw.android.di.messagedetails.MessageDetailsFragmentModule
import im.bnw.android.di.newpost.NewPostFragmentModule
import im.bnw.android.di.profile.ProfileFragmentModule
import im.bnw.android.di.savedreplies.SavedRepliesFragmentModule
import im.bnw.android.di.savedmessages.SavedMessagesFragmentModule
import im.bnw.android.di.settings.SettingsFragmentModule
import im.bnw.android.di.splash.SplashFragmentModule
import im.bnw.android.di.user.UserFragmentModule
import im.bnw.android.presentation.auth.AuthFragment
import im.bnw.android.presentation.core.navigation.tab.BnwMultiStackFragment
import im.bnw.android.presentation.messagedetails.MessageDetailsFragment
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.newpost.NewPostFragment
import im.bnw.android.presentation.profile.ProfileFragment
import im.bnw.android.presentation.savedmessages.SavedMessagesFragment
import im.bnw.android.presentation.savedreplies.SavedRepliesFragment
import im.bnw.android.presentation.settings.SettingsFragment
import im.bnw.android.presentation.splash.SplashFragment
import im.bnw.android.presentation.user.UserFragment

@Module
@Suppress("TooManyFunctions")
interface FragmentBindingModule {
    @ContributesAndroidInjector
    fun bindBnwMultiStackFragment(): BnwMultiStackFragment

    @ContributesAndroidInjector(modules = [NewPostFragmentModule::class])
    fun bindNewPostFragment(): NewPostFragment

    @ContributesAndroidInjector(modules = [MessagesFragmentModule::class])
    fun bindMessagesFragment(): MessagesFragment

    @ContributesAndroidInjector(modules = [SplashFragmentModule::class])
    fun bindSplashFragment(): SplashFragment

    @ContributesAndroidInjector(modules = [UserFragmentModule::class])
    fun bindUserFragment(): UserFragment

    @ContributesAndroidInjector(modules = [AuthFragmentModule::class])
    fun bindAuthFragment(): AuthFragment

    @ContributesAndroidInjector(modules = [MessageDetailsFragmentModule::class])
    fun bindMessageDetailsFragment(): MessageDetailsFragment

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    fun bindProfileFragment(): ProfileFragment

    @ContributesAndroidInjector(modules = [SettingsFragmentModule::class])
    fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector(modules = [SavedMessagesFragmentModule::class])
    fun bindSavedMessagesFragment(): SavedMessagesFragment

    @ContributesAndroidInjector(modules = [SavedRepliesFragmentModule::class])
    fun bindSavedRepliesFragment(): SavedRepliesFragment
}
