package im.bnw.android.presentation.messages

import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.user
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
    private val authInteractor: AuthInteractor,
    private val dispatchersProvider: DispatchersProvider
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

    fun cardClicked(position: Int) {
        val messageId = state.messages.getOrNull(position)?.id ?: return
        router.navigateTo(Tab.GLOBAL, Screens.messageDetailsScreen(messageId))
    }

    fun userClicked(position: Int) {
        val userId = state.messages[position].user
        if (state.user == userId) {
            return
        }
        router.navigateTo(Tab.GLOBAL, Screens.messagesScreen(userId))
    }

    fun mediaClicked(messagePosition: Int, mediaPosition: Int) {
        val media = state.messages.getOrNull(messagePosition)?.media?.getOrNull(mediaPosition) ?: return
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
        vmScope.launch(dispatchersProvider.default) {
            updateState { it.copy(beforeLoading = true, error = null) }
            val last = if (state.messages.isNotEmpty()) {
                state.messages.last().id
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
                updateState { it.copy(beforeLoading = false, error = t) }
            }
        }
    }

    private fun loadAfter() {
        if (state.afterLoading && state.messages.isEmpty()) {
            return
        }
        vmScope.launch(dispatchersProvider.default) {
            updateState { it.copy(afterLoading = true, error = null) }
            val first = if (state.messages.isNotEmpty()) {
                state.messages.first().id
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
                updateState { it.copy(afterLoading = false, error = t) }
            }
        }
    }

    private fun List<Message>.toListItems() = map {
        MessageItem(it)
    }

    private fun subscribeUserAuthState() = vmScope.launch {
        authInteractor.subscribeAuth()
            .map {
                state.copy(createMessageVisible = it && state.user.isEmpty())
            }
            .flowOn(dispatchersProvider.io)
            .collect { newState ->
                updateState { newState }
            }
    }
}
