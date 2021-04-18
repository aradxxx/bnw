package im.bnw.android.di.core

import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.State

@Module
abstract class FragmentModule<F : BaseFragment<*, S>, S : State> {
    @Provides
    fun provideRestoredState(fragment: F): S? {
        return fragment.restoredState
    }
}
