package im.bnw.android.presentation.messagedetails

import im.bnw.android.domain.message.Message
import im.bnw.android.presentation.core.State
import im.bnw.android.presentation.messages.adapter.MessageListItem
import kotlinx.parcelize.Parcelize

sealed class MessageDetailsState : State {
    @Parcelize
    object Init : MessageDetailsState()

    @Parcelize
    object Loading : MessageDetailsState()

    @Parcelize
    object LoadingFailed : MessageDetailsState()

    @Parcelize
    data class Idle(
        val messageId: String,
        val message: Message,
        val items: List<MessageListItem>
    ) : MessageDetailsState()
}
