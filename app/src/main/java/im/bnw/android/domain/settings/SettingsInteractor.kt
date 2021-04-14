package im.bnw.android.domain.settings

import kotlinx.coroutines.flow.Flow

interface SettingsInteractor {
    suspend fun updateSettings(settings: Settings)
    fun subscribeSettings(): Flow<Settings>
}
