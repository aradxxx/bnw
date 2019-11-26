package im.bnw.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import im.bnw.android.di.splash.SplashFragmentModule
import im.bnw.android.presentation.core.navigation.tab.TabFragment
import im.bnw.android.presentation.core.navigation.tab.TabsContainerFragment
import im.bnw.android.presentation.splash.SplashFragment

@Module
interface FragmentBindingModule {
    @ContributesAndroidInjector(modules = [SplashFragmentModule::class])
    fun bindSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    fun bindTabsContainerFragment(): TabsContainerFragment

    @ContributesAndroidInjector
    fun bindTabFragment(): TabFragment
}
