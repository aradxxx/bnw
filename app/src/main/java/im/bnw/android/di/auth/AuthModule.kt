package im.bnw.android.di.auth

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.domain.auth.AuthInteractorImpl

@Module
interface AuthModule {
    @Binds
    fun provideAuthInteractor(authSource: AuthInteractorImpl): AuthInteractor
}
