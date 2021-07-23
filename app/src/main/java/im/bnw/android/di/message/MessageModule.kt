package im.bnw.android.di.message

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageInteractorImpl

@Module
interface MessageModule {
    @Binds
    fun provideMessageInteractor(messageSource: MessageInteractorImpl): MessageInteractor
}
