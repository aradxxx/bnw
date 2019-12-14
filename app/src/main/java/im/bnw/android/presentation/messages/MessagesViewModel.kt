package im.bnw.android.presentation.messages

import im.bnw.android.R
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.DialogEvent
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.messages.adapter.MessageListItem
import im.bnw.android.presentation.messages.adapter.MessageWithMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLException

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
            messageInteractor.messages("", last, state.user)
                .catch { e ->
                    handleException(e)
                    updateState { state ->
                        state.copy(
                            beforeLoading = false
                        )
                    }
                }
                .map { messagesToListItems(it) }
                .collect { newPage ->
                    updateState { state ->
                        state.copy(
                            beforeLoading = false,
                            messages = state.messages.plus(newPage)
                        )
                    }
                    Timber.d(Thread.currentThread().name)
                    Timber.d(newPage.toString())
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
            messageInteractor.messages(first, "", state.user)
                .catch { e ->
                    handleException(e)
                    updateState { state ->
                        state.copy(
                            afterLoading = false
                        )
                    }
                }
                .map { messagesToListItems(it) }
                .collect { newPage ->
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
                    Timber.d(Thread.currentThread().name)
                    Timber.d(newPage.toString())
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

    fun swipeRefresh() {
        loadAfter()
    }

    fun bottomNear() {
        loadBefore()
    }

    override fun handleException(e: Throwable) {
        super.handleException(e)
        when (e) {
            is SSLException -> postEvent(DialogEvent(R.string.connection_error_blocking))
            is IOException -> postEvent(DialogEvent(R.string.connection_error))
        }
    }
}
