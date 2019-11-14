package im.bnw.android.di.core

import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.State

@Module
abstract class ActivityModule<A : BaseActivity<VM, S>, VM : BaseViewModel<S>, S : State> {
    @Provides
    fun provideRestoredState(activity: A): S? {
        return activity.restoredState
    }
}
