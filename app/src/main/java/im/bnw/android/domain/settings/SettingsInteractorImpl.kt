package im.bnw.android.domain.settings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsInteractorImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : SettingsInteractor {
    override suspend fun updateSettings(settings: Settings) {
        settingsRepository.updateSettings(settings)
    }

    override fun subscribeSettings(): Flow<Settings> {
        return settingsRepository.subscribeSettings()
    }
}
