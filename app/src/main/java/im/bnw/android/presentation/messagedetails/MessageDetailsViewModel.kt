package im.bnw.android.presentation.messagedetails

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.externalForward
import im.bnw.android.R
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Media
import im.bnw.android.domain.message.MessageDetails
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageMode
import im.bnw.android.domain.message.Reply
import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.usermanager.UserManager
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.ScrollTo
import im.bnw.android.presentation.core.ShowToast
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.messages.MessageClickListener
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.nullOr
import im.bnw.android.presentation.util.text
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val REPLY_ITEM_SORT_DELIMITER = '_'

data class Dependencies @Inject constructor(
    val restoredState: MessageDetailsState?,
    val modo: Modo,
    val messageInteractor: MessageInteractor,
    val messageDetailsScreenParams: MessageDetailsScreenParams,
    val dispatchersProvider: DispatchersProvider,
    val settingsInteractor: SettingsInteractor,
    val userManager: UserManager
)

@SuppressWarnings("TooManyFunctions")
class MessageDetailsViewModel @Inject constructor(
    dependencies: Dependencies
) : BaseViewModel<MessageDetailsState>(
    dependencies.restoredState ?: MessageDetailsState.Init,
    dependencies.modo
),
    MessageClickListener {
    private val initiator = MutableStateFlow(false)
    private val messageInteractor = dependencies.messageInteractor
    private val messageDetailsScreenParams = dependencies.messageDetailsScreenParams
    private val dispatchersProvider = dependencies.dispatchersProvider
    private val settingsInteractor = dependencies.settingsInteractor
    private val userManager = dependencies.userManager

    init {
        updateState { MessageDetailsState.Loading }
        getMessageDetails()
        subscribeSavedMessage()
    }

    override fun cardClicked(position: Int) {
        // no op
    }

    override fun userClicked(position: Int) {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        val userId = current.items[position].user
        modo.externalForward(Screens.Profile(userId))
    }

    override fun mediaClicked(position: Int, mediaPosition: Int) {
        val item = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(position) ?: return
        val media = item.media.getOrNull(mediaPosition) ?: return
        openMedia(item.media, media)
    }

    override fun saveClicked(position: Int) {
        vmScope.launch {
            val message = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(position)
                ?: return@launch
            when (message) {
                is MessageItem -> {
                    if (!message.saved) {
                        messageInteractor.save(message.message)
                    } else {
                        messageInteractor.remove(message.message)
                    }
                }
                is ReplyItem -> {
                    if (!message.saved) {
                        messageInteractor.save(message.reply)
                    } else {
                        messageInteractor.remove(message.reply)
                    }
                }
            }
        }
    }

    override fun tagClicked(tag: String) {
        modo.externalForward(Screens.Messages(mode = MessageMode.All))
    }

    override fun clubClicked(club: String) {
        modo.externalForward(Screens.Messages(club = club, mode = MessageMode.All))
    }

    override fun idLongClicked(position: Int) {
        val id = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(position)?.id ?: return
        messageInteractor.copyIdToClipBoard(id)
        postEvent(ShowToast(R.string.copied_to_clipboard))
    }

    override fun textLongClicked(position: Int) {
        val text = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(position)?.text ?: return
        messageInteractor.copyTextToClipBoard(text)
        postEvent(ShowToast(R.string.copied_to_clipboard))
    }

    fun swiped(position: Int) {
        vmScope.launch {
            val current = state.nullOr<MessageDetailsState.Idle>() ?: return@launch
            val reply = current.items.getOrNull(position)
            if (reply !is ReplyItem) {
                return@launch
            }
            updateState { current.copy(replyTo = reply.reply) }
        }
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

    fun replyTextChanged(replyText: String) {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return
        updateState { current.copy(replyText = replyText) }
    }

    fun closeReplyClicked() = vmScope.launch {
        val current = state.nullOr<MessageDetailsState.Idle>() ?: return@launch
        updateState { current.copy(replyTo = null) }
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

    fun quoteClicked(position: Int) {
        vmScope.launch {
            val reply = state.nullOr<MessageDetailsState.Idle>()?.items?.getOrNull(position) ?: return@launch
            if (reply !is ReplyItem) {
                return@launch
            }
            val replyId = reply.reply.replyTo
            val quotedReplyIndex = state.nullOr<MessageDetailsState.Idle>()?.items?.indexOfFirst { it.id == replyId }
                ?: return@launch
            postEvent(ScrollTo(quotedReplyIndex))
        }
    }

    private fun subscribeSavedMessage() = vmScope.launch {
        combine(
            messageInteractor.observeSavedMessages(listOf(messageDetailsScreenParams.messageId)),
            messageInteractor.observeSavedReplies(),
            initiator
        ) { savedMessages, savedReplies, _ ->
            updateState { currentState ->
                if (currentState is MessageDetailsState.Idle) {
                    currentState.copy(
                        items = currentState.items.map { item ->
                            when (item) {
                                is MessageItem -> {
                                    item.copy(
                                        saved = savedMessages.any { item.id == it.id }
                                    )
                                }
                                is ReplyItem -> {
                                    item.copy(
                                        saved = savedReplies.any { item.id == it.id }
                                    )
                                }
                                else -> {
                                    item
                                }
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
        val replyToUser = replies.firstOrNull { it.id == replyTo }?.user ?: ""
        return ReplyItem(this, sortTag, offset, replyToUser)
    }

    private fun Reply.buildSortTag(replies: List<Reply>): String {
        if (replyTo.isEmpty()) {
            return "$timestamp"
        }
        val parent = replies.first { it.id == replyTo }
        return "${parent.buildSortTag(replies)}$REPLY_ITEM_SORT_DELIMITER$timestamp"
    }

    private suspend fun settings(): Settings = settingsInteractor.subscribeSettings().first()
}
