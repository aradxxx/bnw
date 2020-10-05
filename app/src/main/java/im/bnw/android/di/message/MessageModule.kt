package im.bnw.android.di.message

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import im.bnw.android.data.core.network.Api
import im.bnw.android.domain.message.MessageInteractor

@Module
@InstallIn(ActivityRetainedComponent::class)
object MessageModule {
    @Provides
    @ActivityRetainedScoped
    fun provideMessageInteractor(api: Api): MessageInteractor {
        return MessageInteractor(api);
    }
}
