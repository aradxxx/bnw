package im.bnw.android.presentation.main

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.externalForward
import com.github.terrakok.modo.selectStack
import im.bnw.android.BuildConfig
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.settings.TabSettings
import im.bnw.android.domain.usermanager.UserManager
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    restoredState: MainState?,
    modo: Modo,
    private val userManager: UserManager,
    private val settingsInteractor: SettingsInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<MainState>(
    restoredState ?: MainState.Init,
    modo
) {
    init {
        subscribeSettings()
    }

    private val handler = Handler(Looper.getMainLooper())

    private fun subscribeSettings() = vmScope.launch {
        settingsInteractor.subscribeSettings()
            .flowOn(dispatchersProvider.io)
            .collectIndexed { index, settings ->
                updateState {
                    MainState.Main(settings.theme, settings.transitionAnimations)
                }
                if (index == 0) {
                    checkCurrentTab(settings.defaultTab)
                }
            }
    }

    private fun navigateToUser(user: String) {
        vmScope.launch(dispatchersProvider.main) {
            userManager.getUserName()
                .first {
                    if (it == user) {
                        modo.selectStack(Tab.PROFILE.ordinal)
                    } else {
                        modo.externalForward(Screens.Profile(user))
                    }
                    true
                }
        }
    }

    fun checkDeepLink(intent: Intent) {
        val segment = intent.data?.pathSegments?.getOrNull(0) ?: return
        val id = intent.data?.pathSegments?.getOrNull(1) ?: return
        when (segment) {
            BuildConfig.POST_PATH_SEGMENT -> {
                modo.externalForward(Screens.MessageDetails(id))
            }
            BuildConfig.USER_PATH_SEGMENT -> {
                navigateToUser(id)
            }
            else -> {
                Timber.d("Unknown deep link found $segment $id")
            }
        }
    }

    private fun checkCurrentTab(defaultTab: TabSettings) {
        val currentScreen = modo.state.chain.lastOrNull()
        if (currentScreen is MultiScreen) {
            val selected = currentScreen.selectedStack
            val needSwitchTo = when {
                defaultTab == TabSettings.Messages && selected != 0 -> {
                    0
                }
                defaultTab == TabSettings.Hot && selected != 1 -> {
                    1
                }
                defaultTab == TabSettings.User && selected != 2 -> {
                    2
                }
                else -> null
            }
            needSwitchTo ?: return
            handler.post {
                modo.selectStack(needSwitchTo)
            }
        }
    }
}
