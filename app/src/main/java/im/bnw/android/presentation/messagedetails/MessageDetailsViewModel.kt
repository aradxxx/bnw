package im.bnw.android.presentation.messagedetails

import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.externalForward
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Media
import im.bnw.android.domain.message.MessageDetails
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.Reply
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val REPLY_ITEM_SORT_DELIMITER = '_'

@SuppressWarnings("TooManyFunctions")
class MessageDetailsViewModel @Inject constructor(
    restoredState: MessageDetailsState?,
    private val messageInteractor: MessageInteractor,
    private val messageDetailsScreenParams: MessageDetailsScreenParams,
    private val dispatchersProvider: DispatchersProvider,
    private val settingsInteractor: SettingsInteractor,
) : BaseViewModel<MessageDetailsState>(
    restoredState ?: MessageDetailsState.Init
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
        modo.externalForward(Screens.Messages(userId))
    }

    fun retryClicked() {
        updateState { MessageDetailsState.Loading }
        getMessageDetails()
    }

    fun swipeRefresh() {
        getMessageDetails(state.nullOr())
    }

    fun anonClicked() {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        updateState { current.copy(anon = !current.anon) }
    }

    fun replyClicked(position: Int? = null) {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        if (position != null) {
            val replyId = current.items[position].id
            updateState { current.copy(replyMessageId = replyId) }
        } else {
            updateState { current.copy(replyMessageId = "") }
        }
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
            val result = messageInteractor.reply(
                current.replyText.trim(),
                current.messageId,
                current.replyMessageId,
                current.anon
            )
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
            modo.launch(Screens.externalHyperlink(media.fullUrl))
        } else {
            modo.externalForward(Screens.ImageView(media.fullUrl))
        }
    }

    private fun getMessageDetails(oldIdleState: MessageDetailsState.Idle? = null) =
        vmScope.launch(dispatchersProvider.default) {
            when (val result = messageInteractor.messageDetails(messageId = messageDetailsScreenParams.messageId)) {
                is Result.Success -> {
                    val incognito = incognito()
                    val idle = result.value.toIdleState(incognito)
                    updateState {
                        if (oldIdleState == null) {
                            idle
                        } else {
                            idle.copy(
                                anon = oldIdleState.anon,
                                replyMessageId = oldIdleState.replyMessageId,
                                replyText = oldIdleState.replyText,
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    handleException(result.throwable)
                    updateState {
                        if (it is MessageDetailsState.Idle) {
                            it.copy(
                                sendProgress = false
                            )
                        } else {
                            MessageDetailsState.LoadingFailed(result.throwable)
                        }
                    }
                }
            }
        }

    private fun MessageDetails.toIdleState(incognito: Boolean): MessageDetailsState.Idle {
        val messageItem = listOf(MessageItem(message))
        val sortedReplies = replies.map { it.toReplyListItem(replies) }.sortedBy { it.sortTag }
        return MessageDetailsState.Idle(
            messageId = messageId,
            message = message,
            items = messageItem + sortedReplies,
            anon = incognito
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

    private suspend fun incognito(): Boolean {
        return settingsInteractor.subscribeSettings()
            .map {
                it.incognito
            }
            .first()
    }
}
