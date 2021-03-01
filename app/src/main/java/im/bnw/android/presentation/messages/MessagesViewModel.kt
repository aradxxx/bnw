package im.bnw.android.presentation.messages

import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.messages.adapter.MessageWithMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

private const val PAGE_SIZE = 20

class MessagesViewModel @Inject constructor(
    router: AppRouter,
    restoredState: MessagesState?,
    screenParams: MessagesScreenParams,
    private val messageInteractor: MessageInteractor,
    private val authInteractor: AuthInteractor
) : BaseViewModel<MessagesState>(
    restoredState ?: MessagesState(user = screenParams.user),
    router
) {
    init {
        loadBefore()
        subscribeUserAuthState()
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

    fun createPostClicked() {
        router.navigateTo(Tab.GLOBAL, Screens.newPostScreen())
    }

    private fun loadBefore() {
        if (state.beforeLoading || state.fullLoaded) {
            return
        }
        vmScope.launch(Dispatchers.Default) {
            updateState { it.copy(beforeLoading = true) }
            val last = if (state.messages.isNotEmpty()) {
                state.messages.last().message().id
            } else {
                ""
            }
            try {
                val newPage = messageInteractor.messages("", last, state.user).toListItems()
                updateState {
                    it.copy(
                        beforeLoading = false,
                        messages = it.messages + newPage
                    )
                }
            } catch (t: IOException) {
                handleException(t)
                updateState { it.copy(beforeLoading = false) }
            }
        }
    }

    private fun loadAfter() {
        if (state.afterLoading && state.messages.isEmpty()) {
            return
        }
        vmScope.launch(Dispatchers.Default) {
            updateState { it.copy(afterLoading = true) }
            val first = if (state.messages.isNotEmpty()) {
                state.messages.first().message().id
            } else {
                ""
            }

            try {
                val newPage = messageInteractor.messages(first, "", state.user).toListItems()
                val needLoadMore = newPage.size == PAGE_SIZE
                updateState {
                    it.copy(
                        afterLoading = needLoadMore,
                        messages = newPage + it.messages
                    )
                }
                if (needLoadMore) {
                    loadAfter()
                } else if (newPage.isNotEmpty()) {
                    postEvent(Event.ScrollToTop)
                }
            } catch (t: IOException) {
                handleException(t)
                updateState { it.copy(afterLoading = false) }
            }
        }
    }

    private fun List<Message>.toListItems() = map {
        when {
            it.content.media.isEmpty() -> MessageItem(it)
            else -> MessageWithMediaItem(it)
        }
    }

    private fun subscribeUserAuthState() = vmScope.launch {
        authInteractor.subscribeAuth()
            .map {
                state.copy(createMessageVisible = it && state.user.isEmpty())
            }
            .flowOn(Dispatchers.IO)
            .collect { newState ->
                updateState { newState }
            }
    }
}
