package im.bnw.android.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.github.terrakok.modo.forward
import im.bnw.android.BuildConfig
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.Screens
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    restoredState: MainState?,
    private val settingsInteractor: SettingsInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<MainState>(
    restoredState ?: MainState()
) {
    init {
        subscribeSettings()
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
                modo.forward(Screens.MessageDetails(id))
            }
            BuildConfig.USER_PATH_SEGMENT -> {
            }
            else -> {
                Timber.d("Unknown deep link found $segment $id")
            }
        }
    }
}
