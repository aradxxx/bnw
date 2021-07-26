package im.bnw.android.di.messagedetails

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentWithArgsModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.messagedetails.MessageDetailsFragment
import im.bnw.android.presentation.messagedetails.MessageDetailsScreenParams
import im.bnw.android.presentation.messagedetails.MessageDetailsState
import im.bnw.android.presentation.messagedetails.MessageDetailsViewModel

@Module(includes = [MessageDetailsFragmentModule.ViewModelModule::class])
class MessageDetailsFragmentModule :
    FragmentWithArgsModule<MessageDetailsFragment, MessageDetailsState, MessageDetailsScreenParams>() {
    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(MessageDetailsViewModel::class)
        fun bindViewModel(viewModel: MessageDetailsViewModel): ViewModel
    }
}
