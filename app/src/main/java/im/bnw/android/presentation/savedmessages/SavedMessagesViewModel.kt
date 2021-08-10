package im.bnw.android.presentation.savedmessages

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.back
import com.github.terrakok.modo.externalForward
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.RemoveMessageFromLocalStorage
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.messages.MessageClickListener
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SavedMessagesViewModel @Inject constructor(
    restoredState: SavedMessagesState?,
    modo: Modo,
    private val messageInteractor: MessageInteractor,
    private val dispatchersProvider: DispatchersProvider
) : BaseViewModel<SavedMessagesState>(
    restoredState ?: SavedMessagesState.Init,
    modo
), MessageClickListener {
    init {
        subscribeSavedMessages()
    }

    override fun cardClicked(position: Int) {
        val messageId = state.nullOr<SavedMessagesState.Idle>()?.messages?.getOrNull(position)?.id ?: return
        modo.externalForward(Screens.MessageDetails(messageId))
    }

    override fun userClicked(position: Int) {
        val userId = state.nullOr<SavedMessagesState.Idle>()?.messages?.getOrNull(position)?.user ?: return
        modo.externalForward(Screens.Profile(userId))
    }

    override fun mediaClicked(position: Int, mediaPosition: Int) {
        val message = state.nullOr<SavedMessagesState.Idle>()?.messages?.getOrNull(position) ?: return
        val media = message.media.getOrNull(mediaPosition) ?: return
        if (media.isYoutube()) {
            modo.launch(Screens.externalHyperlink(media.fullUrl))
        } else {
            postEvent(
                OpenMediaEvent(
                    message.media
                        .filter { !it.isYoutube() }
                        .map { it.fullUrl },
                    media.fullUrl
                )
            )
        }
    }

    override fun saveClicked(position: Int) {
        vmScope.launch {
            val message = state.nullOr<SavedMessagesState.Idle>()?.messages?.getOrNull(position) ?: return@launch
            if (message.saved) {
                updateState {
                    if (it is SavedMessagesState.Idle) {
                        it.copy(
                            candidateToRemove = message
                        )
                    } else {
                        it
                    }
                }
            }
            postEvent(RemoveMessageFromLocalStorage)
        }
    }

    fun emptyButtonClicked() {
        modo.back()
    }

    fun removeMessageConfirmed() = vmScope.launch {
        val candidate = state.nullOr<SavedMessagesState.Idle>()?.candidateToRemove ?: return@launch
        messageInteractor.remove(candidate.message)
    }

    private fun List<Message>.toListItems() = map {
        MessageItem(it, true)
    }

    private fun subscribeSavedMessages() = vmScope.launch {
        messageInteractor.observeSavedMessages()
            .map { messages ->
                updateState {
                    SavedMessagesState.Idle(
                        messages = messages.toListItems()
                    )
                }
            }
            .flowOn(dispatchersProvider.default)
            .collect()
    }
}
