package im.bnw.android.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import im.bnw.android.domain.settings.LanguageSettings
import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.settings.SettingsRepository
import im.bnw.android.domain.settings.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    private object PreferencesKeys {
        val INCOGNITO = preferencesKey<Boolean>("incognito")
        val THEME = preferencesKey<String>("theme")
        val LANGUAGE = preferencesKey<String>("language")
        val SCROLL_TO_REPLIES = preferencesKey<Boolean>("scrollToReplies")
    }

    override suspend fun updateSettings(settings: Settings) {
        dataStore.edit {
            it[PreferencesKeys.INCOGNITO] = settings.incognito
            it[PreferencesKeys.THEME] = settings.theme.value
            it[PreferencesKeys.LANGUAGE] = settings.language.value
            it[PreferencesKeys.SCROLL_TO_REPLIES] = settings.scrollToReplies
        }
    }

    override fun subscribeSettings(): Flow<Settings> {
        return dataStore.data.map {
            Settings(
                it[PreferencesKeys.INCOGNITO] ?: false,
                it[PreferencesKeys.SCROLL_TO_REPLIES] ?: true,
                themeMap(it[PreferencesKeys.THEME].orEmpty()),
                languageMap(it[PreferencesKeys.LANGUAGE].orEmpty()),
            )
        }
    }

    private fun themeMap(value: String): ThemeSettings = when (value) {
        "light" -> ThemeSettings.Light
        "dark" -> ThemeSettings.Dark
        else -> ThemeSettings.Default
    }

    private fun languageMap(value: String): LanguageSettings = when (value) {
        "en" -> LanguageSettings.English
        "ru" -> LanguageSettings.Russian
        else -> LanguageSettings.Default
    }
}
