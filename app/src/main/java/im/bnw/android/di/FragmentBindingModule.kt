package im.bnw.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import im.bnw.android.di.auth.AuthFragmentModule
import im.bnw.android.di.imageview.ImageFragmentModule
import im.bnw.android.di.message.MessagesFragmentModule
import im.bnw.android.di.newpost.NewPostFragmentModule
import im.bnw.android.di.profile.ProfileFragmentModule
import im.bnw.android.di.splash.SplashFragmentModule
import im.bnw.android.presentation.auth.AuthFragment
import im.bnw.android.presentation.core.navigation.tab.TabFragment
import im.bnw.android.presentation.core.navigation.tab.TabsContainerFragment
import im.bnw.android.presentation.imageview.ImageFragment
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.newpost.NewPostFragment
import im.bnw.android.presentation.profile.ProfileFragment
import im.bnw.android.presentation.splash.SplashFragment

@Module
interface FragmentBindingModule {
    @ContributesAndroidInjector
    fun bindTabsContainerFragment(): TabsContainerFragment

    @ContributesAndroidInjector
    fun bindTabFragment(): TabFragment

    @ContributesAndroidInjector(modules = [NewPostFragmentModule::class])
    fun bindNewPostFragment(): NewPostFragment

    @ContributesAndroidInjector(modules = [MessagesFragmentModule::class])
    fun bindMessagesFragment(): MessagesFragment

    @ContributesAndroidInjector(modules = [SplashFragmentModule::class])
    fun bindSplashFragment(): SplashFragment

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    fun bindProfileFragment(): ProfileFragment

    @ContributesAndroidInjector(modules = [AuthFragmentModule::class])
    fun bindAuthFragment(): AuthFragment

    @ContributesAndroidInjector(modules = [ImageFragmentModule::class])
    fun bindImageFragment(): ImageFragment
}
