package im.bnw.android.di.core

import android.os.Parcelable
import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.State

@Module
abstract class FragmentWithArgsModule<F : BaseFragment<*, S>, S : State, A : Parcelable>
    : FragmentModule<F, S>() {

    @Provides
    fun provideInitialArgs(fragment: F): A = fragment.initialArguments()
}
