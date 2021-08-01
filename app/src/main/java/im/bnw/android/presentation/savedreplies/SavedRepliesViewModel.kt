package im.bnw.android.presentation.savedreplies

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.back
import com.github.terrakok.modo.externalForward
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.Reply
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.RemoveReplyFromLocalStorage
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SavedRepliesViewModel @Inject constructor(
    restoredState: SavedRepliesState?,
    modo: Modo,
    private val messageInteractor: MessageInteractor,
    private val dispatchersProvider: DispatchersProvider
) : BaseViewModel<SavedRepliesState>(
    restoredState ?: SavedRepliesState.Init,
    modo
) {
    init {
        subscribeSavedReplies()
    }

    fun cardClicked(position: Int) {
        val reply = state.nullOr<SavedRepliesState.Idle>()?.replies?.getOrNull(position)?.reply ?: return
        modo.externalForward(Screens.MessageDetails(reply.messageId))
    }

    fun userClicked(position: Int) {
        val userId = state.nullOr<SavedRepliesState.Idle>()?.replies?.getOrNull(position)?.user ?: return
        modo.externalForward(Screens.Profile(userId))
    }

    fun mediaClicked(replyPosition: Int, mediaPosition: Int) {
        val reply = state.nullOr<SavedRepliesState.Idle>()?.replies?.getOrNull(replyPosition) ?: return
        val media = reply.media.getOrNull(mediaPosition) ?: return
        if (media.isYoutube()) {
            modo.launch(Screens.externalHyperlink(media.fullUrl))
        } else {
            postEvent(
                OpenMediaEvent(
                    reply.media
                        .filter { !it.isYoutube() }
                        .map { it.fullUrl },
                    media.fullUrl
                )
            )
        }
    }

    fun saveReplyClicked(position: Int) = vmScope.launch {
        val reply = state.nullOr<SavedRepliesState.Idle>()?.replies?.getOrNull(position) ?: return@launch
        if (reply.saved) {
            updateState {
                if (it is SavedRepliesState.Idle) {
                    it.copy(
                        candidateToRemove = reply
                    )
                } else {
                    it
                }
            }
        }
        postEvent(RemoveReplyFromLocalStorage)
    }

    fun emptyButtonClicked() {
        modo.back()
    }

    fun removeReplyConfirmed() = vmScope.launch {
        val candidate = state.nullOr<SavedRepliesState.Idle>()?.candidateToRemove ?: return@launch
        messageInteractor.remove(candidate.reply)
    }

    private fun List<Reply>.toListItems() = map {
        ReplyItem(it, "", 0, "", true)
    }

    private fun subscribeSavedReplies() = vmScope.launch {
        messageInteractor.observeSavedReplies()
            .map {
                SavedRepliesState.Idle(
                    replies = it.toListItems()
                )
            }
            .flowOn(dispatchersProvider.default)
            .collect { newState ->
                updateState { newState }
            }
    }
}
