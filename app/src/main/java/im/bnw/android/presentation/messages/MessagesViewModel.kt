package im.bnw.android.presentation.messages

import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.externalForward
import com.github.terrakok.modo.selectStack
import im.bnw.android.domain.auth.AuthInteractor
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.message.Message
import im.bnw.android.domain.message.MessageInteractor
import im.bnw.android.domain.usermanager.UserManager
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.OpenMediaEvent
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.media
import im.bnw.android.presentation.util.user
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val PAGE_SIZE = 20

class MessagesViewModel @Inject constructor(
    restoredState: MessagesState?,
    private val screenParams: MessagesScreenParams,
    private val messageInteractor: MessageInteractor,
    private val authInteractor: AuthInteractor,
    private val userManager: UserManager,
    private val dispatchersProvider: DispatchersProvider
) : BaseViewModel<MessagesState>(
    restoredState ?: MessagesState(user = screenParams.user)
) {
    private val initiator = MutableStateFlow(false)

    init {
        loadBefore()
        subscribeUserAuthState()
        subscribeSavedMessages()
    }

    fun swipeRefresh() {
        loadAfter()
    }

    fun bottomNear() {
        loadBefore()
    }

    fun cardClicked(position: Int) {
        val messageId = state.messages.getOrNull(position)?.id ?: return
        modo.externalForward(Screens.MessageDetails(messageId))
    }

    fun userClicked(position: Int) {
        val userId = state.messages[position].user
        if (state.user == userId) {
            return
        }
        vmScope.launch(dispatchersProvider.main) {
            userManager.getUserName()
                .first {
                    if (it == userId) {
                        modo.selectStack(Tab.PROFILE.ordinal)
                    } else {
                        modo.externalForward(Screens.Profile(userId))
                    }
                    true
                }
        }
    }

    fun mediaClicked(messagePosition: Int, mediaPosition: Int) {
        val message = state.messages.getOrNull(messagePosition) ?: return
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

    fun createPostClicked() {
        modo.externalForward(Screens.NewPost)
    }

    fun saveMessageClicked(position: Int) = vmScope.launch {
        val message = state.messages.getOrNull(position) ?: return@launch
        if (message is MessageItem) {
            if (!message.saved) {
                messageInteractor.save(message.message)
            } else {
                messageInteractor.remove(message.message)
            }
        }
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
            when (val newPageResult = messageInteractor.messages(last, state.user, screenParams.today)) {
                is Result.Success -> {
                    val newPage = newPageResult.value.toListItems()
                    updateState {
                        it.copy(
                            beforeLoading = false,
                            messages = it.messages + newPage
                        )
                    }
                    initiator.value = !initiator.value
                }
                is Result.Failure -> {
                    Timber.e(newPageResult.throwable)
                    updateState {
                        it.copy(
                            beforeLoading = false,
                            error = newPageResult.throwable
                        )
                    }
                }
            }
        }
    }

    private fun loadAfter() {
        if (state.afterLoading && state.messages.isEmpty()) {
            return
        }
        vmScope.launch(dispatchersProvider.default) {
            updateState { it.copy(afterLoading = true, error = null) }
            when (val newPageResult = messageInteractor.messages("", state.user, screenParams.today)) {
                is Result.Success -> {
                    val newPage = newPageResult.value.toListItems()
                    updateState {
                        it.copy(
                            afterLoading = false,
                            messages = newPage
                        )
                    }
                    initiator.value = !initiator.value
                }
                is Result.Failure -> {
                    Timber.e(newPageResult.throwable)
                    updateState {
                        it.copy(
                            afterLoading = false,
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
