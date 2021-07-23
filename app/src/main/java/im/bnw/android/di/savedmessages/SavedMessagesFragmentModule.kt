package im.bnw.android.di.savedmessages

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.savedmessages.SavedMessagesFragment
import im.bnw.android.presentation.savedmessages.SavedMessagesState
import im.bnw.android.presentation.savedmessages.SavedMessagesViewModel

@Module(includes = [SavedMessagesFragmentModule.ViewModelModule::class])
class SavedMessagesFragmentModule : FragmentModule<SavedMessagesFragment, SavedMessagesState>() {
    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(SavedMessagesViewModel::class)
        fun bindViewModel(viewModel: SavedMessagesViewModel): ViewModel
    }
}
