package im.bnw.android.presentation.messages

import im.bnw.android.presentation.core.State
import im.bnw.android.presentation.messages.adapter.MessageListItem
import kotlinx.parcelize.Parcelize
import okhttp3.internal.immutableListOf

@Parcelize
data class MessagesState(
    val messages: List<MessageListItem> = immutableListOf(),
    val user: String = "",
    val beforeLoading: Boolean = false,
    val afterLoading: Boolean = false,
    val fullLoaded: Boolean = false,
    val createMessageVisible: Boolean = false,
    val error: Throwable? = null
) : State

sealed class Event {
    object ScrollToTop : Event()
}
