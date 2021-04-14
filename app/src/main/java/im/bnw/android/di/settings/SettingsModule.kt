package im.bnw.android.di.settings

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.settings.SettingsInteractorImpl

@Module
interface SettingsModule {
    @Binds
    fun provideSettingsInteractor(settingsInteractor: SettingsInteractorImpl): SettingsInteractor
}
