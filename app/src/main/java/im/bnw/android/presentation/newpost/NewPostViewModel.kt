package im.bnw.android.presentation.newpost

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.back
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.usermanager.UserManager
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.CursorToEnd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewPostViewModel @Inject constructor(
    restoredState: NewPostState?,
    modo: Modo,
    private val messageInteractor: MessageInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val userManager: UserManager,
    private val mainScope: CoroutineScope,
) : BaseViewModel<NewPostState>(
    restoredState ?: NewPostState(),
    modo
) {
    init {
        getDraft()
        subscribeSettings()
    }

    fun textChanged(text: String) {
        updateState {
            it.copy(
                text = text,
                sendEnabled = text.trim().isNotEmpty()
            )
        }
    }

    fun anonChanged() {
        updateState {
            it.copy(asAnon = !it.asAnon)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun sendConfirmed() = vmScope.launch {
        updateState { it.copy(sendEnabled = false) }
        when (val result = messageInteractor.post(state.text.trim(), state.asAnon)) {
            is Result.Success -> {
                userManager.deleteDraft()
                modo.back()
            }
            is Result.Failure -> {
                updateState { it.copy(sendEnabled = true) }
                handleException(result.throwable)
            }
        }
    }

    fun saveDraft() = mainScope.launch {
        userManager.saveDraft(state.text)
    }

    private fun getDraft() = vmScope.launch {
        val result = userManager.draft()
        if (result !is Result.Success) {
            return@launch
        }
        postEvent(CursorToEnd(result.value))
    }

    private fun subscribeSettings() = vmScope.launch {
        settingsInteractor.subscribeSettings()
            .map {
                it.incognito
            }
            .collect { incognito ->
                updateState {
                    it.copy(asAnon = incognito)
                }
            }
    }
}
