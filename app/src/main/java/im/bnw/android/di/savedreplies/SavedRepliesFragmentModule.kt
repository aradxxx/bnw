package im.bnw.android.di.savedreplies

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.savedreplies.SavedRepliesFragment
import im.bnw.android.presentation.savedreplies.SavedRepliesState
import im.bnw.android.presentation.savedreplies.SavedRepliesViewModel

@Module(includes = [SavedRepliesFragmentModule.ViewModelModule::class])
class SavedRepliesFragmentModule : FragmentModule<SavedRepliesFragment, SavedRepliesState>() {
    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(SavedRepliesViewModel::class)
        fun bindViewModel(viewModel: SavedRepliesViewModel): ViewModel
    }
}
