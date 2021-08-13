package im.bnw.android.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import im.bnw.android.domain.settings.LanguageSettings
import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.settings.SettingsRepository
import im.bnw.android.domain.settings.TabSettings
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.util.exhaustive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    companion object {
        private val INCOGNITO = booleanPreferencesKey("incognito")
        private val THEME = stringPreferencesKey("theme")
        private val LANGUAGE = stringPreferencesKey("language")
        private val SCROLL_TO_REPLIES = booleanPreferencesKey("scrollToReplies")
        private val SAVE_POST_DRAFT = booleanPreferencesKey("savePostDraft")
        private val TRANSITION_ANIMATIONS = booleanPreferencesKey("transitionAnimations")
        private val DEFAULT_TAB = stringPreferencesKey("defaultTab")
    }

    override suspend fun updateSettings(settings: Settings) {
        dataStore.edit {
            it[INCOGNITO] = settings.incognito
            it[THEME] = settings.theme.value
            it[LANGUAGE] = settings.language.value
            it[SCROLL_TO_REPLIES] = settings.scrollToReplies
            it[SAVE_POST_DRAFT] = settings.savePostDraft
            it[TRANSITION_ANIMATIONS] = settings.transitionAnimations
            it[DEFAULT_TAB] = settings.defaultTab.value
        }
    }

    override fun subscribeSettings(): Flow<Settings> {
        return dataStore.data.map {
            Settings(
                it[INCOGNITO] ?: false,
                it[SCROLL_TO_REPLIES] ?: true,
                it[SAVE_POST_DRAFT] ?: true,
                it[TRANSITION_ANIMATIONS] ?: true,
                themeMap(it[THEME].orEmpty()),
                languageMap(it[LANGUAGE].orEmpty()),
                defaultTabMap(it[DEFAULT_TAB].orEmpty())
            )
        }
    }

    private fun themeMap(value: String): ThemeSettings = when (value) {
        "light" -> ThemeSettings.Light
        "dark" -> ThemeSettings.Dark
        else -> ThemeSettings.Default
    }.exhaustive

    private fun languageMap(value: String): LanguageSettings = when (value) {
        "en" -> LanguageSettings.English
        "ru" -> LanguageSettings.Russian
        else -> LanguageSettings.Default
    }.exhaustive

    private fun defaultTabMap(value: String): TabSettings = when (value) {
        "user" -> TabSettings.User
        "hot" -> TabSettings.Hot
        else -> TabSettings.Messages
    }.exhaustive
}
