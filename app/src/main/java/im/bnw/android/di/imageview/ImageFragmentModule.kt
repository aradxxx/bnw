package im.bnw.android.di.imageview

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentWithArgsModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.imageview.ImageFragment
import im.bnw.android.presentation.imageview.ImageScreenParams
import im.bnw.android.presentation.imageview.ImageState
import im.bnw.android.presentation.imageview.ImageViewModel

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
