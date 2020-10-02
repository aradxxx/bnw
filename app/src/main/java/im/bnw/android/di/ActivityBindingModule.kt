package im.bnw.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import im.bnw.android.di.main.MainActivityModule
import im.bnw.android.presentation.main.MainActivity

@InstallIn(ApplicationComponent::class)
@Module
interface ActivityBindingModule {
    @ContributesAndroidInjector(modules = [MainActivityModule::class, FragmentBindingModule::class])
    fun bindMainActivity(): MainActivity
}
