package im.bnw.android.presentation.messagedetails

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import im.bnw.android.R
import im.bnw.android.databinding.FragmentMessageDetailsBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.recyclerview.LinearLayoutManagerSmoothScroll
import im.bnw.android.presentation.core.view.FailureView
import im.bnw.android.presentation.messagedetails.adapter.ReplyAdapter
import im.bnw.android.presentation.messagedetails.adapter.replyItemDecorator
import im.bnw.android.presentation.util.PostNotFound
import im.bnw.android.presentation.util.UI
import im.bnw.android.presentation.util.disableItemChangedAnimation
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.setThrottledClickListener
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments
import javax.net.ssl.SSLException

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
            { position -> viewModel.saveMessageClicked(position) }
        )
    }

    companion object {
        fun newInstance(params: MessageDetailsScreenParams) = MessageDetailsFragment().withInitialArguments(params)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.replies) {
            layoutManager = LinearLayoutManagerSmoothScroll(
                context = requireContext(),
                smoothScrollSpeedMillisecondsPerInch = LinearLayoutManagerSmoothScroll.SCROLL_SPEED_FAST
            )
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
            is MessageDetailsState.Idle -> {
                renderIdle(state)
            }
            is MessageDetailsState.Loading -> {
                renderLoading()
            }
            is MessageDetailsState.LoadingFailed -> {
                renderLoadingFailed(state)
            }
            is MessageDetailsState.Init -> {
                renderInit()
            }
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
            handler.postDelayed(0) {
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
        when (state.throwable) {
            is PostNotFound -> {
                failure.showFailure(R.string.error, R.string.message_not_found)
            }
            is SSLException -> {
                failure.showFailure(R.string.no_connection, R.string.possibly_domain_blocked)
            }
            else -> {
                failure.showFailure(R.string.no_connection, R.string.check_connection)
            }
        }
    }

    private fun renderInit() = with(binding) {
        swipeToRefresh.isRefreshing = false
        progressBar.isVisible = false
        failure.isVisible = false
        content.isVisible = false
    }

    private fun FailureView.showFailure(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int
    ) {
        isVisible = true
        title = getString(titleResId)
        message = getString(messageResId)
    }
}
