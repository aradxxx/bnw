package im.bnw.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import im.bnw.android.presentation.core.tab.TabFragment
import im.bnw.android.presentation.core.tab.TabsContainerFragment

@Module
interface FragmentBindingModule {
    @ContributesAndroidInjector
    fun bindTabsContainerFragment(): TabsContainerFragment

    @ContributesAndroidInjector
    fun bindTabFragment(): TabFragment
}
