package im.bnw.android.presentation.settings

import com.github.terrakok.modo.Modo
import im.bnw.android.R
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.settings.LanguageSettings
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.settings.TabSettings
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.domain.user.ProfileInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.LanguageChangedEvent
import im.bnw.android.presentation.core.SettingsDialogEvent
import im.bnw.android.presentation.settings.adapter.SettingsItem
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.toItem
import im.bnw.android.presentation.util.toSetting
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("TooManyFunctions")
class SettingsViewModel @Inject constructor(
    restoredState: SettingsState?,
    modo: Modo,
    private val settingsInteractor: SettingsInteractor,
    private val profileInteractor: ProfileInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<SettingsState>(
    restoredState ?: SettingsState.Loading,
    modo
) {
    init {
        loadSettings()
    }

    fun anonymityClicked(enabled: Boolean) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<SettingsState.Idle>() ?: return@launch
            currentState.user ?: return@launch
            if (enabled == currentState.settings.incognito) {
                return@launch
            }
            settingsInteractor.updateSettings(
                currentState.settings.copy(incognito = enabled)
            )
        }
    }

    fun scrollToRepliesChanged(enabled: Boolean) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<SettingsState.Idle>() ?: return@launch
            if (enabled == currentState.settings.scrollToReplies) {
                return@launch
            }
            settingsInteractor.updateSettings(
                currentState.settings.copy(scrollToReplies = enabled)
            )
        }
    }

    fun savePostDraftChanged(enabled: Boolean) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<SettingsState.Idle>() ?: return@launch
            if (enabled == currentState.settings.savePostDraft) {
                return@launch
            }
            settingsInteractor.updateSettings(
                currentState.settings.copy(savePostDraft = enabled)
            )
        }
    }

    fun transitionAnimationsChanged(enabled: Boolean) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<SettingsState.Idle>() ?: return@launch
            if (enabled == currentState.settings.transitionAnimations) {
                return@launch
            }
            settingsInteractor.updateSettings(
                currentState.settings.copy(transitionAnimations = enabled)
            )
        }
    }

    fun choiceSettingsChanged(item: SettingsItem?) {
        when (item) {
            is ThemeItem -> {
                themeChanged(item.toSetting())
            }
            is LanguageItem -> {
                languageChanged(item.toSetting())
            }
            is TabSettingsItem -> {
                defaultTabChanged(item.toSetting())
            }
            else -> throw IllegalArgumentException("Unknown setting class")
        }
    }

    fun chooseTheme() {
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.subscribeSettings()
                .first {
                    SettingsDialogEvent(
                        R.string.choose_theme,
                        it.theme.toItem(),
                        arrayListOf(
                            ThemeItem.Default,
                            ThemeItem.Light,
                            ThemeItem.Dark,
                        ),
                    ).also { dialogEvent ->
                        postEvent(dialogEvent)
                    }
                    true
                }
        }
    }

    fun chooseLanguage() {
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.subscribeSettings()
                .first {
                    SettingsDialogEvent(
                        R.string.choose_language,
                        it.language.toItem(),
                        arrayListOf(
                            LanguageItem.Default,
                            LanguageItem.English,
                            LanguageItem.Russian,
                        ),
                    ).also { dialogEvent ->
                        postEvent(dialogEvent)
                    }
                    true
                }
        }
    }

    fun chooseDefaultTab() {
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.subscribeSettings()
                .first {
                    SettingsDialogEvent(
                        R.string.default_tab,
                        it.defaultTab.toItem(),
                        arrayListOf(
                            TabSettingsItem.Messages,
                            TabSettingsItem.Feed,
                            TabSettingsItem.Hot,
                            TabSettingsItem.User
                        ),
                    ).also { dialogEvent ->
                        postEvent(dialogEvent)
                    }
                    true
                }
        }
    }

    private fun themeChanged(theme: ThemeSettings) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<SettingsState.Idle>() ?: return@launch
            if (theme == currentState.settings.theme) {
                return@launch
            }
            settingsInteractor.updateSettings(
                currentState.settings.copy(theme = theme)
            )
        }
    }

    private fun languageChanged(language: LanguageSettings) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<SettingsState.Idle>() ?: return@launch
            if (language == currentState.settings.language) {
                return@launch
            }
            settingsInteractor.updateSettings(
                currentState.settings.copy(language = language)
            )
            postEvent(LanguageChangedEvent(language))
        }
    }

    private fun defaultTabChanged(tab: TabSettings) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<SettingsState.Idle>() ?: return@launch
            if (tab == currentState.settings.defaultTab) {
                return@launch
            }
            settingsInteractor.updateSettings(
                currentState.settings.copy(defaultTab = tab)
            )
        }
    }

    private fun loadSettings() = vmScope.launch(dispatchersProvider.default) {
        combine(
            settingsInteractor.subscribeSettings(),
            profileInteractor.subscribeUserInfo()
        ) { settings, profile ->
            val user = when (profile) {
                is Result.Success -> profile.value
                is Result.Failure -> null
            }
            SettingsState.Idle(
                settings = settings,
                user = user
            )
        }
            .flowOn(dispatchersProvider.io)
            .collect { newState ->
                updateState { newState }
            }
    }
}
