package im.bnw.android.di.newpost

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.newpost.NewPostFragment
import im.bnw.android.presentation.newpost.NewPostState
import im.bnw.android.presentation.newpost.NewPostViewModel

@Module(includes = [NewPostFragmentModule.ViewModelModule::class])
class NewPostFragmentModule : FragmentModule<NewPostFragment, NewPostState>() {
    @Module(includes = [NewPostModule::class])
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(NewPostViewModel::class)
        fun bindViewModel(viewModel: NewPostViewModel): ViewModel
    }
}
