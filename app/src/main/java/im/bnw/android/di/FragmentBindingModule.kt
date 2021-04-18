package im.bnw.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import im.bnw.android.di.auth.AuthFragmentModule
import im.bnw.android.di.imageview.ImageFragmentModule
import im.bnw.android.di.message.MessagesFragmentModule
import im.bnw.android.di.messagedetails.MessageDetailsFragmentModule
import im.bnw.android.di.newpost.NewPostFragmentModule
import im.bnw.android.di.profile.ProfileFragmentModule
import im.bnw.android.di.splash.SplashFragmentModule
import im.bnw.android.di.user.UserFragmentModule
import im.bnw.android.presentation.auth.AuthFragment
import im.bnw.android.presentation.imageview.ImageFragment
import im.bnw.android.presentation.messagedetails.MessageDetailsFragment
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.newpost.NewPostFragment
import im.bnw.android.presentation.profile.ProfileFragment
import im.bnw.android.presentation.splash.SplashFragment
import im.bnw.android.presentation.user.UserFragment

@Module
interface FragmentBindingModule {
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

    @ContributesAndroidInjector(modules = [ImageFragmentModule::class])
    fun bindImageFragment(): ImageFragment

    @ContributesAndroidInjector(modules = [MessageDetailsFragmentModule::class])
    fun bindMessageDetailsFragment(): MessageDetailsFragment

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    fun bindProfileFragment(): ProfileFragment
}
