package im.bnw.android.di.message

import dagger.Binds
import dagger.Module
import im.bnw.android.data.message.MessageSource
import im.bnw.android.data.message.MessageSourceImpl
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageInteractorImpl

@Module
interface MessageModule {
    @Binds
    fun provideMessageSource(messageSource: MessageSourceImpl): MessageSource

    @Binds
    fun provideMessageInteractor(messageSource: MessageInteractorImpl): MessageInteractor
}
