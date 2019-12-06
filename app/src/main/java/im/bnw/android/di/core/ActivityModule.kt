package im.bnw.android.di.core

import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.State

@Module
abstract class ActivityModule<A : BaseActivity<*, S>, S : State> {
    @Provides
    fun provideRestoredState(activity: A) = activity.restoredState
}
