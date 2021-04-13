package im.bnw.android.domain.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun updateSettings(settings: Settings)
    fun subscribeSettings(): Flow<Settings>
}
