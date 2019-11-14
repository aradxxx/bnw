package im.bnw.android.di.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.ActivityModule
import im.bnw.android.di.core.ViewModelKey
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity
import im.bnw.android.presentation.main.MainState
import im.bnw.android.presentation.main.MainViewModel
import ru.aradxxx.ciceronetabs.TabCicerone

@Module(includes = [MainActivityModule.ViewModelModule::class])
class MainActivityModule : ActivityModule<MainActivity, MainViewModel, MainState>() {
    @Provides
    fun provideActivityRouter(tabCicerone: TabCicerone<AppRouter>): AppRouter =
        tabCicerone.activityRouter()

    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(MainViewModel::class)
        fun bindViewModel(viewModel: MainViewModel): ViewModel
    }
}
