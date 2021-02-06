package im.bnw.android.di.newpost

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageInteractorImpl

@Module
interface NewPostModule {
    @Binds
    fun provideMessageInteractor(messageSource: MessageInteractorImpl): MessageInteractor
}
