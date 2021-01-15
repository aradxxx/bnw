package im.bnw.android.di.auth

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.auth.AuthFragment
import im.bnw.android.presentation.auth.AuthState
import im.bnw.android.presentation.auth.AuthViewModel

@Module(includes = [AuthFragmentModule.ViewModelModule::class])
class AuthFragmentModule : FragmentModule<AuthFragment, AuthState>() {
    @Module(includes = [AuthModule::class])
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(AuthViewModel::class)
        fun bindViewModel(viewModel: AuthViewModel): ViewModel
    }
}
