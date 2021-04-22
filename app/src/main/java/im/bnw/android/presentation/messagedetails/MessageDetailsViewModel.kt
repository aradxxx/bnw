package im.bnw.android.presentation.messagedetails

import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.externalForward
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Media
import im.bnw.android.domain.message.MessageDetails
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.Reply
import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.usermanager.UserManager
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
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
    private val userManager: UserManager
) : BaseViewModel<MessageDetailsState>(
    restoredState ?: MessageDetailsState.Init
) {
    private val initiator = MutableStateFlow(false)

    init {
        updateState { MessageDetailsState.Loading }
        getMessageDetails()
        subscribeSavedMessage()
    }

    fun mediaClicked(replyPosition: Int, mediaPosition: Int) {
        val item = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(replyPosition) ?: return
        val media = item.media.getOrNull(mediaPosition) ?: return
        openMedia(item.media, media)
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
            val item = current.items.getOrNull(position)
            val replyTo = if (item is ReplyItem) {
                item.reply
            } else {
                null
            }
            updateState { current.copy(replyTo = replyTo) }
        } else {
            updateState { current.copy(replyTo = null) }
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
                current.replyTo?.id ?: "",
                current.anon
            )
        ) {
            is Result.Success -> {
                updateState { current.copy(sendProgress = false, replyText = "", replyTo = null) }
                getMessageDetails()
            }
            is Result.Failure -> {
                handleException(result.throwable)
                updateState { current.copy(sendProgress = false) }
            }
        }
    }

    fun saveMessageClicked(position: Int) = vmScope.launch {
        val message = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(position) ?: return@launch
        if (message is MessageItem) {
            if (!message.saved) {
                messageInteractor.save(message.message)
            } else {
                messageInteractor.remove(message.message)
            }
        }
    }

    private fun subscribeSavedMessage() = vmScope.launch {
        combine(
            messageInteractor.observeSavedMessages(listOf(messageDetailsScreenParams.messageId)),
            initiator
        ) { savedMessages, _ ->
            updateState { currentState ->
                if (currentState is MessageDetailsState.Idle) {
                    currentState.copy(
                        items = currentState.items.map { item ->
                            if (item is MessageItem) {
                                item.copy(
                                    saved = savedMessages.any { item.id == it.id }
                                )
                            } else {
                                item
                            }
                        }
                    )
                } else {
                    currentState
                }
            }
        }
            .flowOn(dispatchersProvider.io)
            .collect()
    }

    private fun openMedia(mediaList: List<Media>, media: Media) {
        if (media.isYoutube()) {
            modo.launch(Screens.externalHyperlink(media.fullUrl))
        } else {
            postEvent(OpenMediaEvent(mediaList.filter { !it.isYoutube() }.map { it.fullUrl }, media.fullUrl))
        }
    }

    private fun getMessageDetails(oldIdleState: MessageDetailsState.Idle? = null) =
        vmScope.launch(dispatchersProvider.default) {
            when (val result = messageInteractor.messageDetails(messageId = messageDetailsScreenParams.messageId)) {
                is Result.Success -> {
                    val settings = settings()
                    val isAuthenticated = userManager.isAuthenticated().first()
                    val idle = result.value.toIdleState(settings, isAuthenticated)
                    updateState {
                        if (oldIdleState == null) {
                            idle
                        } else {
                            idle.copy(
                                anon = oldIdleState.anon,
                                replyTo = oldIdleState.replyTo,
                                replyText = oldIdleState.replyText,
                            )
                        }
                    }
                    initiator.value = !initiator.value
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

    private fun MessageDetails.toIdleState(settings: Settings, isAuthenticated: Boolean): MessageDetailsState.Idle {
        val messageItem = listOf(MessageItem(message))
        val sortedReplies = replies.map { it.toReplyListItem(replies) }.sortedBy { it.sortTag }
        return MessageDetailsState.Idle(
            messageId = messageId,
            message = message,
            items = messageItem + sortedReplies,
            anon = settings.incognito,
            needScrollToReplies = settings.scrollToReplies,
            allowReply = isAuthenticated
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

    private suspend fun settings(): Settings {
        return settingsInteractor.subscribeSettings()
            .first()
    }
}
