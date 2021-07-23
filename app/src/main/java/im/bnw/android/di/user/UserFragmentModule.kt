package im.bnw.android.di.user

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.user.UserFragment
import im.bnw.android.presentation.user.UserState
import im.bnw.android.presentation.user.UserViewModel

@Module(includes = [UserFragmentModule.ViewModelModule::class])
class UserFragmentModule : FragmentModule<UserFragment, UserState>() {
    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(UserViewModel::class)
        fun bindViewModel(viewModel: UserViewModel): ViewModel
    }
}
