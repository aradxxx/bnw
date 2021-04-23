package im.bnw.android.di.user

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageInteractorImpl
import im.bnw.android.domain.user.ProfileInteractor
import im.bnw.android.domain.user.ProfileInteractorImpl

@Module
interface UserModule {
    @Binds
    fun provideProfileInteractor(profileSource: ProfileInteractorImpl): ProfileInteractor

    @Binds
    fun provideMessageInteractor(messageSource: MessageInteractorImpl): MessageInteractor
}
