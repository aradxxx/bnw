package im.bnw.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import im.bnw.android.di.main.MainActivityModule
import im.bnw.android.presentation.main.MainActivity

@Module
interface ActivityBindingModule {
    @ContributesAndroidInjector(modules = [MainActivityModule::class, FragmentBindingModule::class])
    fun bindMainActivity(): MainActivity
}
