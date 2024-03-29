package im.bnw.android.di.profile

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.user.ProfileInteractor
import im.bnw.android.domain.user.ProfileInteractorImpl

@Module
interface ProfileModule {
    @Binds
    fun provideProfileInteractor(profileSource: ProfileInteractorImpl): ProfileInteractor
}
