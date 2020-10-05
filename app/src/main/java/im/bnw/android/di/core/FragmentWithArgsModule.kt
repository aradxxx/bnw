package im.bnw.android.di.core

import android.os.Parcelable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.FragmentComponent
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.State

@Module
@InstallIn(FragmentComponent::class)
abstract class FragmentWithArgsModule<F : BaseFragment<*, S>, S : State, A : Parcelable>
    : FragmentModule<F, S>() {

    @Provides
    fun provideInitialArgs(fragment: F): A = fragment.initialArguments()
}
