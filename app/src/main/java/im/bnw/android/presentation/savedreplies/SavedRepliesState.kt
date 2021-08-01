package im.bnw.android.presentation.savedreplies

import im.bnw.android.presentation.core.State
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.messages.adapter.MessageItem
import kotlinx.parcelize.Parcelize

sealed class SavedRepliesState : State {
    @Parcelize
    object Init : SavedRepliesState()

    @Parcelize
    data class Idle(
        val replies: List<ReplyItem>,
        val candidateToRemove: ReplyItem? = null
    ) : SavedRepliesState()
}
