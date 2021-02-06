package im.bnw.android.presentation.newpost

import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewPostViewModel @Inject constructor(
    router: AppRouter,
    restoredState: NewPostState?,
    private val messageInteractor: MessageInteractor
) : BaseViewModel<NewPostState>(
    restoredState ?: NewPostState(),
    router
) {
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

    fun sendConfirmed() = vmScope.launch {
        messageInteractor.post(state.text, state.asAnon)
        router.exit()
    }
}
