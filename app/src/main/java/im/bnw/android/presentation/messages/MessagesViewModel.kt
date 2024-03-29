package im.bnw.android.presentation.messages

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.externalForward
import im.bnw.android.R
import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.message.MessageMode
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.ShowToast
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.text
import im.bnw.android.presentation.util.toEvent
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class Dependencies @Inject constructor(
    val restoredState: MessagesState?,
    val modo: Modo,
    val screenParams: MessagesScreenParams,
    val messageInteractor: MessageInteractor,
    val authInteractor: AuthInteractor,
    val dispatchersProvider: DispatchersProvider
)

@Suppress("TooManyFunctions")
class MessagesViewModel @Inject constructor(
    dependencies: Dependencies
) : BaseViewModel<MessagesState>(
    dependencies.restoredState ?: MessagesState(user = dependencies.screenParams.user),
    dependencies.modo
), MessageClickListener {
    private val initiator = MutableStateFlow(false)
    private val dispatchersProvider = dependencies.dispatchersProvider
    private val messageInteractor = dependencies.messageInteractor
    private val screenParams = dependencies.screenParams
    private val authInteractor = dependencies.authInteractor

    init {
        loadBefore()
        subscribeUserAuthState()
        subscribeSavedMessages()
    }

    override fun cardClicked(position: Int) {
        val messageId = state.messages.getOrNull(position)?.id ?: return
        modo.externalForward(Screens.MessageDetails(messageId))
    }

    override fun userClicked(position: Int) {
        val userId = state.messages[position].user
        modo.externalForward(Screens.Profile(userId))
    }

    override fun mediaClicked(position: Int, mediaPosition: Int) {
        val message = state.messages.getOrNull(position) ?: return
        val media = message.media.getOrNull(mediaPosition) ?: return
        if (media.isYoutube()) {
            modo.launch(Screens.externalHyperlink(media.fullUrl))
        } else {
            postEvent(
                OpenMediaEvent(
                    message.media
                        .filter { !it.isYoutube() }
                        .map { it.fullUrl },
                    media.fullUrl
                )
            )
        }
    }

    override fun saveClicked(position: Int) {
        vmScope.launch {
            val message = state.messages.getOrNull(position) ?: return@launch
            if (message is MessageItem) {
                if (!message.saved) {
                    messageInteractor.save(message.message)
                } else {
                    messageInteractor.remove(message.message)
                }
            }
        }
    }

    override fun tagClicked(tag: String) {
        modo.externalForward(Screens.Messages(tag = tag, mode = MessageMode.All))
    }

    override fun clubClicked(club: String) {
        modo.externalForward(Screens.Messages(club = club, mode = MessageMode.All))
    }

    override fun idLongClicked(position: Int) {
        val id = state.messages.getOrNull(position)?.id ?: return
        messageInteractor.copyIdToClipBoard(id)
        postEvent(ShowToast(R.string.copied_to_clipboard))
    }

    override fun textLongClicked(position: Int) {
        val text = state.messages.getOrNull(position)?.text ?: return
        messageInteractor.copyTextToClipBoard(text)
        postEvent(ShowToast(R.string.copied_to_clipboard))
    }

    fun swipeRefresh() {
        loadAfter()
    }

    fun bottomNear() {
        loadBefore()
    }

    fun createPostClicked() {
        modo.externalForward(Screens.NewPost)
    }

    private fun loadBefore() {
        if (state.beforeLoading) {
            return
        }
        vmScope.launch(dispatchersProvider.default) {
            updateState { it.copy(beforeLoading = true, showSwipeToRefresh = it.messages.isEmpty(), error = null) }
            val last = if (state.messages.isNotEmpty()) {
                state.messages.last().id
            } else {
                ""
            }
            when (val newPageResult =
                messageInteractor.messages(last, state.user, screenParams.tag, screenParams.club, screenParams.mode)) {
                is Result.Success -> {
                    val newPage = newPageResult.value.toListItems()
                    updateState {
                        it.copy(
                            beforeLoading = false,
                            showSwipeToRefresh = false,
                            messages = it.messages + newPage
                        )
                    }
                    initiator.value = !initiator.value
                }
                is Result.Failure -> {
                    if (state.messages.isNotEmpty()) {
                        newPageResult.throwable.toEvent()?.let {
                            postEvent(it)
                        }
                    }
                    Timber.e(newPageResult.throwable)
                    updateState {
                        it.copy(
                            beforeLoading = false,
                            showSwipeToRefresh = false,
                            error = newPageResult.throwable
                        )
                    }
                }
            }
        }
    }

    private fun loadAfter(silent: Boolean = false) {
        if (state.afterLoading) {
            return
        }
        vmScope.launch(dispatchersProvider.default) {
            updateState { it.copy(afterLoading = true, showSwipeToRefresh = !silent, error = null) }
            when (val newPageResult = messageInteractor.messages(
                "", state.user, screenParams.tag, screenParams.club, screenParams.mode
            )) {
                is Result.Success -> {
                    val newPage = newPageResult.value.toListItems()
                    updateState {
                        it.copy(
                            afterLoading = false,
                            showSwipeToRefresh = false,
                            messages = newPage
                        )
                    }
                    initiator.value = !initiator.value
                }
                is Result.Failure -> {
                    if (state.messages.isNotEmpty()) {
                        newPageResult.throwable.toEvent()?.let {
                            postEvent(it)
                        }
                    }
                    Timber.e(newPageResult.throwable)
                    updateState {
                        it.copy(
                            afterLoading = false,
                            showSwipeToRefresh = false,
                            error = newPageResult.throwable
                        )
                    }
                }
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

    private fun subscribeSavedMessages() = vmScope.launch {
        combine(messageInteractor.observeSavedMessages(), initiator) { savedMessages, _ ->
            val modified = state.copy(
                messages = state.messages.map { item ->
                    if (item is MessageItem) {
                        item.copy(
                            saved = savedMessages.any { item.id == it.id }
                        )
                    } else {
                        item
                    }
                }
            )
            updateState { modified }
        }
            .flowOn(dispatchersProvider.default)
            .collect()
    }
}
