package im.bnw.android.di.core

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.State

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule<A : BaseActivity<*, S>, S : State> {
    @Provides
    fun provideRestoredState(activity: A) = activity.restoredState
}
