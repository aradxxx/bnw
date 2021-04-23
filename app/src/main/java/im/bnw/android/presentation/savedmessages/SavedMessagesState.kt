package im.bnw.android.presentation.savedmessages

import im.bnw.android.presentation.core.State
import im.bnw.android.presentation.messages.adapter.MessageItem
import kotlinx.parcelize.Parcelize

sealed class SavedMessagesState : State {
    @Parcelize
    object Init : SavedMessagesState()

    @Parcelize
    data class Idle(
        val messages: List<MessageItem>,
        val candidateToRemove: MessageItem? = null
    ) : SavedMessagesState()
}
