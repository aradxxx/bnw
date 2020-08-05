package im.bnw.android.di.login

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.login.LoginFragment
import im.bnw.android.presentation.login.LoginState
import im.bnw.android.presentation.login.LoginViewModel

@Module(includes = [LoginFragmentModule.ViewModelModule::class])
class LoginFragmentModule : FragmentModule<LoginFragment, LoginState>() {
    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(LoginViewModel::class)
        fun bindViewModel(viewModel: LoginViewModel): ViewModel
    }
}