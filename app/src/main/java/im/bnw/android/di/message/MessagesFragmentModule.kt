package im.bnw.android.di.message

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentWithArgsModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.messages.MessagesScreenParams
import im.bnw.android.presentation.messages.MessagesState
import im.bnw.android.presentation.messages.MessagesViewModel

@Module(includes = [MessagesFragmentModule.ViewModelModule::class])
class MessagesFragmentModule :
    FragmentWithArgsModule<MessagesFragment, MessagesState, MessagesScreenParams>() {

    @Module(includes = [MessageModule::class])
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(MessagesViewModel::class)
        fun bindViewModel(viewModel: MessagesViewModel): ViewModel
    }
}
