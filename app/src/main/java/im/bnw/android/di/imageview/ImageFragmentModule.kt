package im.bnw.android.di.imageview

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.FragmentWithArgsModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.di.message.MessageModule
import im.bnw.android.presentation.imageview.ImageFragment
import im.bnw.android.presentation.imageview.ImageScreenParams
import im.bnw.android.presentation.imageview.ImageState
import im.bnw.android.presentation.imageview.ImageViewModel
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.messages.MessagesScreenParams
import im.bnw.android.presentation.messages.MessagesState
import im.bnw.android.presentation.messages.MessagesViewModel

@Module(includes = [ImageFragmentModule.ViewModelModule::class])
class ImageFragmentModule :
    FragmentWithArgsModule<ImageFragment, ImageState, ImageScreenParams>() {

    @Module
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(ImageViewModel::class)
        fun bindViewModel(viewModel: ImageViewModel): ViewModel
    }
}
