package im.bnw.android.presentation.main

import android.content.Intent
import com.github.terrakok.modo.externalForward
import com.github.terrakok.modo.selectStack
import im.bnw.android.BuildConfig
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.usermanager.UserManager
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    restoredState: MainState?,
    private val userManager: UserManager,
    private val settingsInteractor: SettingsInteractor,
    private val dispatchersProvider: DispatchersProvider,
) : BaseViewModel<MainState>(
    restoredState ?: MainState.Init
) {
    init {
        subscribeSettings()
    }

    private fun subscribeSettings() = vmScope.launch {
        settingsInteractor.subscribeSettings()
            .flowOn(dispatchersProvider.io)
            .collect { settings ->
                updateState {
                    MainState.Main(settings.theme)
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
}
