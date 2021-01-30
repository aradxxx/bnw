package im.bnw.android.di.profile

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.profile.ProfileInteractor
import im.bnw.android.domain.profile.ProfileInteractorImpl

@Module
interface ProfileModule {
    @Binds
    fun provideProfileInteractor(profileSource: ProfileInteractorImpl): ProfileInteractor
}
