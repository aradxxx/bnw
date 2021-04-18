package im.bnw.android.presentation.messagedetails

import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.Reply
import im.bnw.android.presentation.core.State
import im.bnw.android.presentation.messages.adapter.MessageListItem
import kotlinx.parcelize.Parcelize

sealed class MessageDetailsState : State {
    @Parcelize
    object Init : MessageDetailsState()

    @Parcelize
    object Loading : MessageDetailsState()

    @Parcelize
    data class LoadingFailed(
        val throwable: Throwable? = null
    ) : MessageDetailsState()

    @Parcelize
    data class Idle(
        val messageId: String,
        val message: Message,
        val items: List<MessageListItem>,
        val anon: Boolean = false,
        val replyTo: Reply? = null,
        val sendProgress: Boolean = false,
        val replyText: String = ""
    ) : MessageDetailsState()
}
