package im.bnw.android.presentation.messagedetails

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.os.postDelayed
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
    private lateinit var linearLayoutManager: LinearLayoutManager

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
                    binding.replies.smoothScrollToPosition(event.position)
                    linearLayoutManager.findViewByPosition(event.position)?.let {
                        animateView(it)
                    }
                }
            }
            else -> super.onEvent(event)
        }
    }

    private fun animateView(it: View) {
        if (it !is CardView) {
            return
        }

        ObjectAnimator.ofFloat(it, "alpha", 1F, 0.4F, 1F).apply {
            startDelay = 200L
            start()
        }
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
}
