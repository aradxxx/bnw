package im.bnw.android.presentation.messages

import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.messages.adapter.MessageListItem
import im.bnw.android.presentation.messages.adapter.MessageWithMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

private const val PAGE_SIZE = 20

class MessagesViewModel @Inject constructor(
    router: AppRouter,
    restoredState: MessagesState?,
    screenParams: MessagesScreenParams,
    private val messageInteractor: MessageInteractor
) : BaseViewModel<MessagesState>(
    restoredState ?: MessagesState(user = screenParams.user),
    router
) {
    init {
        loadBefore()
    }

    fun swipeRefresh() {
        loadAfter()
    }

    fun bottomNear() {
        loadBefore()
    }

    fun userClicked(position: Int) {
        val userId = state.messages[position].message().user
        if (state.user == userId) {
            return
        }
        router.navigateTo(Tab.GLOBAL, Screens.messagesScreen(userId))
    }

    fun mediaClicked(messagePosition: Int, mediaPosition: Int) {
        val message = state.messages.getOrNull(messagePosition) ?: return
        val media = message.message().content.media.getOrNull(mediaPosition) ?: return
        if (media.isYoutube()) {
            router.navigateTo(Tab.GLOBAL, Screens.externalHyperlinkScreen(media.fullUrl))
        } else {
            router.navigateTo(Tab.GLOBAL, Screens.imageViewScreen(media.fullUrl))
        }
    }

    private fun loadBefore() {
        if (state.beforeLoading || state.fullLoaded) {
            return
        }
        vmScope.launch(Dispatchers.Default) {
            updateState { state -> state.copy(beforeLoading = true) }
            val last = if (state.messages.isNotEmpty()) {
                state.messages.last().message().id
            } else {
                ""
            }
            try {
                val messages = messageInteractor.messages("", last, state.user)
                val newPage = messagesToListItems(messages)
                updateState { state ->
                    state.copy(
                        beforeLoading = false,
                        messages = state.messages.plus(newPage)
                    )
                }
            } catch (t: IOException) {
                handleException(t)
                updateState { state ->
                    state.copy(
                        beforeLoading = false
                    )
                }
            }
        }
    }

    private fun loadAfter() {
        if (state.beforeLoading && state.messages.isEmpty()) {
            return
        }
        vmScope.launch(Dispatchers.Default) {
            updateState { state -> state.copy(afterLoading = true) }
            val first = if (state.messages.isNotEmpty()) {
                state.messages.first().message().id
            } else {
                ""
            }

            try {
                val messages = messageInteractor.messages(first, "", state.user)
                val newPage = messagesToListItems(messages)
                val needLoadMore = newPage.size == PAGE_SIZE
                updateState { state ->
                    state.copy(
                        afterLoading = needLoadMore,
                        messages = newPage.plus(state.messages)
                    )
                }
                if (needLoadMore) {
                    loadAfter()
                } else if (newPage.isNotEmpty()) {
                    postEvent(Event.ScrollToTop)
                }
            } catch (t: IOException) {
                handleException(t)
                updateState { state ->
                    state.copy(
                        afterLoading = false
                    )
                }
            }
        }
    }

    private fun messagesToListItems(messages: List<Message>): List<MessageListItem> {
        return messages.asSequence()
            .map {
                return@map when {
                    it.content.media.isEmpty() -> MessageItem(it)
                    else -> MessageWithMediaItem(it)
                }
            }
            .toList()
    }
}
