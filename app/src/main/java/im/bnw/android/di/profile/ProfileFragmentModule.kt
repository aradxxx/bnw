package im.bnw.android.di.profile

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.profile.ProfileFragment
import im.bnw.android.presentation.profile.ProfileState
import im.bnw.android.presentation.profile.ProfileViewModel

@Module(includes = [ProfileFragmentModule.ViewModelModule::class])
class ProfileFragmentModule : FragmentModule<ProfileFragment, ProfileState>() {
    @Module(includes = [ProfileModule::class])
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(ProfileViewModel::class)
        fun bindViewModel(viewModel: ProfileViewModel): ViewModel
    }
}
