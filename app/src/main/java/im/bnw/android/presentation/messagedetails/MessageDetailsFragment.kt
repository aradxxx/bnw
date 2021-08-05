package im.bnw.android.presentation.messagedetails

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.os.postDelayed
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import im.bnw.android.R
import im.bnw.android.databinding.FragmentMessageDetailsBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.Event
import im.bnw.android.presentation.core.ScrollTo
import im.bnw.android.presentation.core.recyclerview.LinearLayoutManagerSmoothScroll
import im.bnw.android.presentation.messagedetails.adapter.ReplyAdapter
import im.bnw.android.presentation.messagedetails.adapter.replyItemDecorator
import im.bnw.android.presentation.util.PostNotFound
import im.bnw.android.presentation.util.UI
import im.bnw.android.presentation.util.disableItemChangedAnimation
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.networkFailureMessage
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.setThrottledClickListener
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments

private const val DECORATIONS_INVALIDATE_DELAY = 200L
private const val FLASH_DELAY = 380L
private const val FLASH_DURATION = 500L
private const val FLASH_PROPERTY = "backgroundColor"

class MessageDetailsFragment : BaseFragment<MessageDetailsViewModel, MessageDetailsState>(
    R.layout.fragment_message_details
) {
    private val binding by viewBinding(FragmentMessageDetailsBinding::bind)
    override val vmClass = MessageDetailsViewModel::class.java
    private val replyAdapter: ReplyAdapter by lazy {
        ReplyAdapter(
            0F,
            UI.MESSAGE_DETAILS_MEDIA_HEIGHT.dpToPx,
            { position -> viewModel.userClicked(position) },
            { position, mediaPosition -> viewModel.mediaClicked(position, mediaPosition) },
            { position -> viewModel.replyClicked(position) },
            { position -> viewModel.saveMessageClicked(position) },
            { position -> viewModel.saveReplyClicked(position) },
            { position -> viewModel.quoteClicked(position) },
        )
    }
    private val flashColor by lazy {
        requireContext().getColor(R.color.colorRipple)
    }
    private val transparentColor by lazy {
        requireContext().getColor(android.R.color.transparent)
    }
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var toFlash: Animator? = null
    private var fromFlash: Animator? = null

    companion object {
        fun newInstance(params: MessageDetailsScreenParams) = MessageDetailsFragment().withInitialArguments(params)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManagerSmoothScroll(
            context = requireContext(),
            smoothScrollSpeedMillisecondsPerInch = LinearLayoutManagerSmoothScroll.SCROLL_SPEED_FAST
        )
        with(binding.replies) {
            layoutManager = linearLayoutManager
            adapter = replyAdapter
            addItemDecoration(replyItemDecorator)
            disableItemChangedAnimation()
        }
        with(binding) {
            failure.setActionListener {
                viewModel.retryClicked()
            }
            swipeToRefresh.setOnRefreshListener {
                viewModel.swipeRefresh()
            }
            swipeToRefresh.setColorSchemeResources(
                R.color.colorPrimary
            )
        }
        with(binding.reply) {
            anon.setOnClickListener {
                viewModel.anonClicked()
            }
            send.setThrottledClickListener {
                viewModel.sendReplyClicked()
            }
            replyToClose.setOnClickListener {
                viewModel.replyClicked()
            }
            replyText.doAfterTextChanged {
                viewModel.replyTextChanged(it.toString())
            }
        }
    }

    override fun updateState(state: MessageDetailsState) {
        when (state) {
            is MessageDetailsState.Idle -> renderIdle(state)
            is MessageDetailsState.Loading -> renderLoading()
            is MessageDetailsState.LoadingFailed -> renderLoadingFailed(state)
            is MessageDetailsState.Init -> renderInit()
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is ScrollTo -> {
                handler.post {
                    val needScroll = event.position < linearLayoutManager.findFirstCompletelyVisibleItemPosition() ||
                        event.position > linearLayoutManager.findLastCompletelyVisibleItemPosition()
                    if (needScroll) {
                        binding.replies.smoothScrollToPosition(event.position)
                    }
                    val delay = if (needScroll) {
                        FLASH_DELAY
                    } else {
                        0
                    }
                    handler.postDelayed(delay) {
                        linearLayoutManager.findViewByPosition(event.position)?.let {
                            animateView(it)
                        }
                    }
                }
            }
            else -> super.onEvent(event)
        }
    }

    override fun onDestroyView() {
        toFlash?.cancel()
        fromFlash?.cancel()
        super.onDestroyView()
    }

    private fun renderIdle(state: MessageDetailsState.Idle) = with(binding) {
        swipeToRefresh.isRefreshing = false
        progressBar.isVisible = false
        failure.isVisible = false
        content.isVisible = true
        if (replyAdapter.items.isEmpty() &&
            state.items.size > 1 &&
            state.needScrollToReplies
        ) {
            handler.post {
                replies.smoothScrollToPosition(1)
            }
        }
        replyAdapter.items = state.items

        @Suppress("ForbiddenComment")
        // todo: remove this hotfix
        handler.postDelayed(DECORATIONS_INVALIDATE_DELAY) {
            binding.replies.invalidateItemDecorations()
        }

        reply.root.isVisible = state.allowReply
        with(reply) {
            anon.isActivated = state.anon
            replyTo.isVisible = state.replyTo != null
            state.replyTo?.let {
                replyToUserName.newText = it.user
                replyToText.newText = it.text
            }
            sendProgress.isVisible = state.sendProgress
            replyText.newText = state.replyText
            send.isVisible = state.replyText.trim().isNotEmpty()
        }
    }

    private fun renderLoading() = with(binding) {
        swipeToRefresh.isRefreshing = false
        progressBar.isVisible = true
        failure.isVisible = false
        content.isVisible = false
    }

    private fun renderLoadingFailed(state: MessageDetailsState.LoadingFailed) = with(binding) {
        swipeToRefresh.isRefreshing = false
        progressBar.isVisible = false
        failure.isVisible = true
        content.isVisible = false
        if (state.throwable is PostNotFound) {
            failure.setFailure(
                titleResId = R.string.error,
                messageString = getString(R.string.message_not_found),
            )
        } else {
            failure.setFailure(
                titleResId = R.string.no_connection,
                messageString = requireContext().networkFailureMessage(state.throwable),
            )
        }
    }

    private fun renderInit() = with(binding) {
        swipeToRefresh.isRefreshing = false
        progressBar.isVisible = false
        failure.isVisible = false
        content.isVisible = false
    }

    private fun animateView(it: View) {
        if (it !is ViewGroup) {
            return
        }
        if (toFlash != null || fromFlash != null) {
            return
        }
        val view = it.getChildAt(0) ?: return
        fromFlash = ObjectAnimator.ofArgb(view, FLASH_PROPERTY, transparentColor).apply {
            startDelay = FLASH_DURATION
            duration = FLASH_DURATION
            doOnEnd {
                fromFlash = null
            }
        }
        toFlash = ObjectAnimator.ofArgb(view, FLASH_PROPERTY, flashColor).apply {
            duration = FLASH_DURATION / 3
            doOnEnd {
                fromFlash?.start()
                toFlash = null
            }
            start()
        }
        view.doOnDetach {
            fromFlash?.cancel()
            toFlash?.cancel()
            it.setBackgroundColor(transparentColor)
        }
    }
}
