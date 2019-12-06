package im.bnw.android.di.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.ActivityModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.main.MainActivity
import im.bnw.android.presentation.main.MainState
import im.bnw.android.presentation.main.MainViewModel

@Module(includes = [MainActivityModule.ViewModelModule::class])
class MainActivityModule : ActivityModule<MainActivity, MainState>() {
    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(MainViewModel::class)
        fun bindViewModel(viewModel: MainViewModel): ViewModel
    }
}
