package im.bnw.android.presentation.messagedetails

import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Media
import im.bnw.android.domain.message.MessageDetails
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.Reply
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val REPLY_ITEM_SORT_DELIMITER = '_'

class MessageDetailsViewModel @Inject constructor(
    router: AppRouter,
    restoredState: MessageDetailsState?,
    private val messageInteractor: MessageInteractor,
    private val messageDetailsScreenParams: MessageDetailsScreenParams,
    private val dispatchersProvider: DispatchersProvider
) : BaseViewModel<MessageDetailsState>(
    restoredState ?: MessageDetailsState.Init,
    router
) {
    init {
        updateState { MessageDetailsState.Loading }
        getMessageDetails()
    }

    fun mediaClicked(replyPosition: Int, mediaPosition: Int) {
        val item = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(replyPosition) ?: return
        val media = item.media.getOrNull(mediaPosition) ?: return
        openMedia(media)
    }

    fun userClicked(position: Int) {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        val userId = current.items[position].user
        router.navigateTo(Tab.GLOBAL, Screens.messagesScreen(userId))
    }

    fun retryClicked() {
        updateState { MessageDetailsState.Loading }
        getMessageDetails()
    }

    fun anonClicked() {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        updateState { current.copy(anon = !current.anon) }
    }

    fun replyClicked(position: Int) {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        val replyId = current.items[position].id
        updateState { current.copy(replyMessageId = replyId) }
    }

    fun replyResetClicked() {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        updateState { current.copy(replyMessageId = "") }
    }

    fun replyTextChanged(replyText: String) {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        updateState { current.copy(replyText = replyText) }
    }

    fun sendReplyClicked() = vmScope.launch {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return@launch
        if (current.sendProgress) {
            return@launch
        }
        updateState { current.copy(sendProgress = true) }
        when (
            val result =
                messageInteractor.reply(current.replyText, current.messageId, current.replyMessageId, current.anon)
        ) {
            is Result.Success -> {
                updateState { current.copy(sendProgress = false, replyText = "", replyMessageId = "") }
                getMessageDetails()
            }
            is Result.Failure -> {
                handleException(result.throwable)
                updateState { current.copy(sendProgress = false) }
            }
        }
    }

    private fun openMedia(media: Media) {
        if (media.isYoutube()) {
            router.navigateTo(Tab.GLOBAL, Screens.externalHyperlinkScreen(media.fullUrl))
        } else {
            router.navigateTo(Tab.GLOBAL, Screens.imageViewScreen(media.fullUrl))
        }
    }

    private fun getMessageDetails() = vmScope.launch(dispatchersProvider.default) {
        when (val result = messageInteractor.messageDetails(messageId = messageDetailsScreenParams.messageId)) {
            is Result.Success -> {
                val idle = result.value.toIdleState()
                updateState {
                    if (it !is MessageDetailsState.Idle) {
                        idle
                    } else {
                        it.copy(
                            idle.messageId,
                            idle.message,
                            idle.items
                        )
                    }
                }
            }
            is Result.Failure -> {
                handleException(result.throwable)
                updateState { MessageDetailsState.LoadingFailed(result.throwable) }
            }
        }
    }

    private fun MessageDetails.toIdleState(): MessageDetailsState.Idle {
        val messageItem = listOf(MessageItem(message))
        val sortedReplies = replies.map { it.toReplyListItem(replies) }.sortedBy { it.sortTag }
        return MessageDetailsState.Idle(
            messageId,
            message,
            messageItem + sortedReplies
        )
    }

    private fun Reply.toReplyListItem(replies: List<Reply>): ReplyItem {
        val sortTag = buildSortTag(replies)
        val offset = sortTag.count { it == REPLY_ITEM_SORT_DELIMITER }
        return ReplyItem(this, sortTag, offset)
    }

    private fun Reply.buildSortTag(replies: List<Reply>): String {
        if (replyTo.isEmpty()) {
            return "$timestamp"
        }
        val parent = replies.first { it.id == replyTo }
        return "${parent.buildSortTag(replies)}$REPLY_ITEM_SORT_DELIMITER$timestamp"
    }
}
