package im.bnw.android.di.core

import android.os.Parcelable
import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.State

@Module
abstract class FragmentWithArgsModule<F : BaseFragment<VM, S>,
        VM : BaseViewModel<S>, S : State, A : Parcelable>
    : FragmentModule<F, VM, S>() {

    @Provides
    fun provideInitialArgs(fragment: F): A {
        fragment.arguments?.getParcelable<A>(BaseFragment.BUNDLE_INITIAL_ARGS)?.let {
            return it
        }
        throw IllegalArgumentException("Fragment doesn't contain initial args")
    }
}
