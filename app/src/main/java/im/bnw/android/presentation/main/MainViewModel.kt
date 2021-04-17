package im.bnw.android.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.github.aradxxx.ciceroneflow.FlowCicerone
import im.bnw.android.BuildConfig
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    cicerone: FlowCicerone<AppRouter>,
    restoredState: MainState?,
    private val settingsInteractor: SettingsInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<MainState>(
    restoredState ?: MainState(),
    cicerone.mainRouter()
) {
    init {
        subscribeSettings()
    }

    fun startNavigation() {
        router.newRootScreen(Screens.tabContainerScreen())
        router.switchTab(Tab.General)
        router.newRootScreen(Tab.General, Screens.messagesScreen(""))
        router.newRootScreen(Tab.Profile, Screens.profileScreen())
    }

    private fun subscribeSettings() = vmScope.launch {
        settingsInteractor.subscribeSettings()
            .map {
                themeSettingsToDelegateTheme(it.theme)
            }
            .flowOn(dispatchersProvider.io)
            .collect {
                AppCompatDelegate.setDefaultNightMode(it)
            }
    }

    private fun themeSettingsToDelegateTheme(theme: ThemeSettings): Int = when (theme) {
        ThemeSettings.Default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ThemeSettings.Light -> AppCompatDelegate.MODE_NIGHT_NO
        ThemeSettings.Dark -> AppCompatDelegate.MODE_NIGHT_YES
    }

    fun checkDeepLink(intent: Intent) {
        val segment = intent.data?.pathSegments?.getOrNull(0) ?: return
        val id = intent.data?.pathSegments?.getOrNull(1) ?: return
        when (segment) {
            BuildConfig.POST_PATH_SEGMENT -> {
                router.navigateTo(Screens.messageDetailsScreen(id))
            }
            BuildConfig.USER_PATH_SEGMENT -> {
            }
            else -> {
                Timber.d("Unknown deep link found $segment $id")
            }
        }
    }
}
