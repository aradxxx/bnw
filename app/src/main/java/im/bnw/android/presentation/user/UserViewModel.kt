package im.bnw.android.presentation.user

import com.github.terrakok.modo.externalForward
import com.github.terrakok.modo.forward
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.settings.LanguageSettings
import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.domain.user.ProfileInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.toItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("TooManyFunctions")
class UserViewModel @Inject constructor(
    restoredState: UserState?,
    private val messageInteractor: MessageInteractor,
    private val profileInteractor: ProfileInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<UserState>(
    restoredState ?: UserState.Init,
) {
    init {
        updateState { UserState.Loading }
        subscribeUserInfo()
    }

    fun loginClicked() {
        state.nullOr<UserState.Unauthorized>() ?: return
        modo.externalForward(Screens.Auth)
    }

    fun logoutConfirmed() {
        state.nullOr<UserState.UserInfo>() ?: return
        logout()
    }

    fun messagesClicked() {
        val currentState = state.nullOr<UserState.UserInfo>() ?: return
        modo.forward(Screens.Messages(currentState.user.name))
    }

    fun anonymityClicked(enabled: Boolean) {
        val currentState = state.nullOr<UserState.UserInfo>() ?: return
        vmScope.launch(dispatchersProvider.default) {
            settingsInteractor.updateSettings(
                currentState.settings.copy(
                    incognito = enabled
                )
            )
        }
    }

    fun scrollToRepliesChanged(checked: Boolean) {
        vmScope.launch(dispatchersProvider.default) {
            val currentState = state.nullOr<UserState.UserInfo>() ?: return@launch
            settingsInteractor.updateSettings(
                currentState.settings.copy(
                    scrollToReplies = checked
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
        retry()
    }

    fun avatarClicked() {
        val current = state.nullOr<UserState.UserInfo>() ?: return
        val imageUrl = String.format(BuildConfig.USER_AVA_URL, current.user.name)
        postEvent(OpenMediaEvent(listOf(imageUrl)))
    }

    fun savedMessagesClicked() {
        modo.externalForward(Screens.SavedMessages)
    }

    private fun logout() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { UserState.Loading }
            profileInteractor.logout()
        }
    }

    private fun retry() {
        vmScope.launch(dispatchersProvider.default) {
            updateState { UserState.Loading }
            profileInteractor.retry()
        }
    }

    private fun subscribeUserInfo() = vmScope.launch {
        combine(
            profileInteractor.subscribeUserInfo(),
            settingsInteractor.subscribeSettings(),
            messageInteractor.observeSavedMessages()
        ) { result, settings, savedMessages ->
            when (result) {
                is Result.Success -> {
                    val user = result.value
                    if (user == null) {
                        UserState.Unauthorized(savedMessages.count(), settings)
                    } else {
                        UserState.UserInfo(user, savedMessages.count(), settings)
                    }
                }
                is Result.Failure -> {
                    UserState.LoadingFailed(result.throwable)
                }
            }
        }
            .flowOn(dispatchersProvider.io)
            .collect { newState ->
                updateState { newState }
            }
    }

    private fun currentSettings(): Settings? = when (val current = state) {
        is UserState.Unauthorized -> {
            current.settings
        }
        is UserState.UserInfo -> {
            current.settings
        }
        else -> {
            null
        }
    }
}
