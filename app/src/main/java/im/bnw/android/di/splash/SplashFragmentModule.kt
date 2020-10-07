package im.bnw.android.di.splash

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.splash.SplashFragment
import im.bnw.android.presentation.splash.SplashState
import im.bnw.android.presentation.splash.SplashViewModel

@Module(includes = [SplashFragmentModule.ViewModelModule::class])
class SplashFragmentModule : FragmentModule<SplashFragment, SplashState>() {
    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(SplashViewModel::class)
        fun bindViewModel(viewModel: SplashViewModel): ViewModel
    }
}
