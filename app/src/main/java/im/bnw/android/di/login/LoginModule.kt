package im.bnw.android.di.login

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.login.LoginInteractor
import im.bnw.android.domain.login.LoginInteractorImpl

@Module
interface LoginModule {
    @Binds
    fun provideLoginInteractor(loginSource: LoginInteractorImpl): LoginInteractor
}
