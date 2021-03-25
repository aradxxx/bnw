package im.bnw.android.di.messagedetails

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageInteractorImpl

@Module
interface MessageDetailsModule {
    @Binds
    fun provideMessageInteractor(messageSource: MessageInteractorImpl): MessageInteractor
}
