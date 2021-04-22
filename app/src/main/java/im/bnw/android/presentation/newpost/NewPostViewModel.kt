package im.bnw.android.presentation.newpost

import com.github.terrakok.modo.back
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.presentation.core.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewPostViewModel @Inject constructor(
    restoredState: NewPostState?,
    private val messageInteractor: MessageInteractor,
    private val settingsInteractor: SettingsInteractor
) : BaseViewModel<NewPostState>(
    restoredState ?: NewPostState()
) {
    init {
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
        updateState { it.copy(asAnon = !it.asAnon) }
    }

    @Suppress("TooGenericExceptionCaught")
    fun sendConfirmed() = vmScope.launch {
        updateState { it.copy(sendEnabled = false) }
        when (val result = messageInteractor.post(state.text.trim(), state.asAnon)) {
            is Result.Success -> {
                modo.back()
            }
            is Result.Failure -> {
                updateState { it.copy(sendEnabled = true) }
                handleException(result.throwable)
            }
        }
    }

    private fun subscribeSettings() = vmScope.launch {
        settingsInteractor.subscribeSettings()
            .map {
                it.incognito
            }
            .collect { incognito ->
                updateState {
                    it.copy(
                        asAnon = incognito
                    )
                }
            }
    }
}
