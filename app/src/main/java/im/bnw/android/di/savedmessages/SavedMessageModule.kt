package im.bnw.android.di.savedmessages

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageInteractorImpl

@Module
interface SavedMessageModule {
    @Binds
    fun provideMessageInteractor(messageSource: MessageInteractorImpl): MessageInteractor
}
