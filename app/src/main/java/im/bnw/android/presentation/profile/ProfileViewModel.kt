package im.bnw.android.presentation.profile

import com.github.terrakok.modo.externalForward
import im.bnw.android.R
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.profile.ProfileInteractor
import im.bnw.android.domain.settings.LanguageSettings
import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.toItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    restoredState: ProfileState?,
    private val profileInteractor: ProfileInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<ProfileState>(
    restoredState ?: ProfileState.Init
) {
    init {
        updateState { ProfileState.Loading }
        subscribeUserInfo()
    }

    fun loginClicked() {
        state.nullOr<ProfileState.Unauthorized>() ?: return
        modo.externalForward(Screens.Auth)
    }

    fun logoutConfirmed() {
        state.nullOr<ProfileState.ProfileInfo>() ?: return
        logout()
    }

    fun messagesClicked() {
        val currentState = state.nullOr<ProfileState.ProfileInfo>() ?: return
        modo.externalForward(Screens.Messages(currentState.user.name))
    }

    fun anonymityClicked(enabled: Boolean) {
        val currentState = state.nullOr<ProfileState.ProfileInfo>() ?: return
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.updateSettings(
                currentState.settings.copy(
                    incognito = enabled
                )
            )
        }
    }

    fun chooseTheme() {
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.subscribeSettings()
                .first {
                    val themeDialogEvent = SettingsDialogEvent(
                        R.string.choose_theme,
                        it.theme.toItem(),
                        arrayListOf(
                            ThemeItem.Default,
                            ThemeItem.Light,
                            ThemeItem.Dark,
                        ),
                    )
                    postEvent(themeDialogEvent)
                    true
                }
        }
    }

    fun chooseLanguage() {
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.subscribeSettings()
                .first {
                    val languageDialogEvent = SettingsDialogEvent(
                        R.string.choose_language,
                        it.language.toItem(),
                        arrayListOf(
                            LanguageItem.Default,
                            LanguageItem.English,
                            LanguageItem.Russian,
                        ),
                    )
                    postEvent(languageDialogEvent)
                    true
                }
        }
    }

    fun themeChanged(theme: ThemeSettings) {
        val settings = currentSettings() ?: return
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.updateSettings(
                settings.copy(
                    theme = theme
                )
            )
        }
    }

    fun languageChanged(language: LanguageSettings) {
        val settings = currentSettings() ?: return
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.updateSettings(
                settings.copy(
                    language = language
                )
            )
        }
    }

    fun retryClicked() {
        state.nullOr<ProfileState.LoadingFailed>() ?: return
        retry()
    }

    private fun logout() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { ProfileState.Loading }
            profileInteractor.logout()
        }
    }

    private fun retry() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { ProfileState.Loading }
            profileInteractor.retry()
        }
    }

    private fun subscribeUserInfo() = vmScope.launch {
        profileInteractor.subscribeUserInfo()
            .combine(settingsInteractor.subscribeSettings()) { result, settings ->
                when (result) {
                    is Result.Success -> {
                        val user = result.value
                        if (user == null) {
                            ProfileState.Unauthorized(settings)
                        } else {
                            ProfileState.ProfileInfo(user, settings)
                        }
                    }
                    is Result.Failure -> {
                        ProfileState.LoadingFailed(result.throwable)
                    }
                }
            }
            .flowOn(dispatchersProvider.io)
            .collect { newState ->
                updateState { newState }
            }
    }

    private fun currentSettings(): Settings? = when (val current = state) {
        is ProfileState.Unauthorized -> {
            current.settings
        }
        is ProfileState.ProfileInfo -> {
            current.settings
        }
        else -> {
            null
        }
    }
}
