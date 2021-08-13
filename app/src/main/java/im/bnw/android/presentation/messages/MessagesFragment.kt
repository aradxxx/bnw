package im.bnw.android.presentation.messages

import android.os.Bundle
import android.view.View
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.bnw.android.R
import im.bnw.android.databinding.FragmentMessagesListBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.Event
import im.bnw.android.presentation.core.ScrollTo
import im.bnw.android.presentation.messages.adapter.MessageAdapter
import im.bnw.android.presentation.messages.adapter.messageItemDecorator
import im.bnw.android.presentation.util.UI
import im.bnw.android.presentation.util.disableItemChangedAnimation
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.dpToPxF
import im.bnw.android.presentation.util.networkFailureMessage
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments

class MessagesFragment : BaseFragment<MessagesViewModel, MessagesState>(
    R.layout.fragment_messages_list
) {
    private val binding by viewBinding(FragmentMessagesListBinding::bind)
    override val vmClass = MessagesViewModel::class.java

    private val messageAdapter by lazy {
        MessageAdapter(
            viewModel,
            UI.MESSAGE_CARD_RADIUS.dpToPxF,
            UI.MESSAGE_MEDIA_HEIGHT.dpToPx,
        )
    }
    private val messageScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (linearLayoutManager.findLastVisibleItemPosition() + AHEAD_VISIBLE_ITEMS_COUNT ==
                messageAdapter.itemCount
            ) {
                viewModel.bottomNear()
            }
        }
    }
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        private const val AHEAD_VISIBLE_ITEMS_COUNT = 10
        private const val SCROLL_TO_POSITION_DELAY = 200L

        fun newInstance(params: MessagesScreenParams) =
            MessagesFragment().withInitialArguments(params)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.messagesList) {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
            addItemDecoration(messageItemDecorator)
            addOnScrollListener(messageScrollListener)
            addOnScrollListener(FabVisibilityScrollListener(binding.createMessage))
            disableItemChangedAnimation()
        }
        with(binding.swipeToRefresh) {
            setColorSchemeResources(R.color.colorPrimary)
            setOnRefreshListener {
                viewModel.swipeRefresh()
            }
        }
        with(binding) {
            createMessage.setOnClickListener {
                viewModel.createPostClicked()
            }
            failure.setActionListener {
                viewModel.swipeRefresh()
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is ScrollTo -> handler.postDelayed(SCROLL_TO_POSITION_DELAY) {
                linearLayoutManager.smoothScrollToPosition(
                    binding.messagesList,
                    null,
                    event.position,
                )
            }
            else -> super.onEvent(event)
        }
    }

    override fun updateState(state: MessagesState) = with(binding) {
        messageAdapter.items = state.messages
        progressBarLine.isVisible = state.beforeLoading && state.messages.isNotEmpty()
        swipeToRefresh.isRefreshing = state.showSwipeToRefresh
        val createMessageEnabled = state.createMessageVisible && state.messages.isNotEmpty()
        createMessage.isEnabled = createMessageEnabled
        createMessage.isVisible = createMessageEnabled
        failureScroll.isVisible = state.messages.isEmpty() && state.error != null
        failure.setFailure(
            titleResId = R.string.no_connection,
            messageString = requireContext().networkFailureMessage(state.error),
        )
    }
}
