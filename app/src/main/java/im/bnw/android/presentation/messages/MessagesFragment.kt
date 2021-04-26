@file:Suppress("MagicNumber")

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
import im.bnw.android.presentation.messages.adapter.MessageAdapter
import im.bnw.android.presentation.messages.adapter.messageItemDecorator
import im.bnw.android.presentation.util.UI
import im.bnw.android.presentation.util.disableItemChangedAnimation
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.dpToPxF
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments
import javax.net.ssl.SSLException

class MessagesFragment : BaseFragment<MessagesViewModel, MessagesState>(
    R.layout.fragment_messages_list
) {
    private val binding by viewBinding(FragmentMessagesListBinding::bind)
    override val vmClass = MessagesViewModel::class.java

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        fun newInstance(params: MessagesScreenParams) = MessagesFragment().withInitialArguments(params)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageAdapter = MessageAdapter(
            UI.MESSAGE_CARD_RADIUS.dpToPxF,
            UI.MESSAGE_MEDIA_HEIGHT.dpToPx,
            { position -> viewModel.cardClicked(position) },
            { position -> viewModel.userClicked(position) },
            { messagePosition, mediaPosition ->
                viewModel.mediaClicked(messagePosition, mediaPosition)
            },
            { position -> viewModel.saveMessageClicked(position) }
        )
        linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.messagesList) {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
            addItemDecoration(messageItemDecorator)
            disableItemChangedAnimation()
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (linearLayoutManager.findLastVisibleItemPosition() + 10 == adapter?.itemCount) {
                            viewModel.bottomNear()
                        }
                    }
                }
            )
            with(binding.createMessage) {
                addOnScrollListener(FabVisibilityScrollListener(this))
                setOnClickListener { viewModel.createPostClicked() }
            }
        }
        with(binding.swipeToRefresh) {
            setColorSchemeResources(
                R.color.colorPrimary
            )
            setOnRefreshListener {
                viewModel.swipeRefresh()
            }
        }
        binding.failure.setActionListener {
            viewModel.swipeRefresh()
        }
    }

    override fun onEvent(event: Any?) {
        super.onEvent(event)
        when (event) {
            is Event.ScrollToTop -> handler.postDelayed(200) {
                linearLayoutManager.smoothScrollToPosition(binding.messagesList, null, 0)
            }
        }
    }

    override fun updateState(state: MessagesState) {
        messageAdapter.items = state.messages
        with(binding) {
            progressBarLine.isVisible = state.beforeLoading && state.messages.isNotEmpty()
            swipeToRefresh.isRefreshing = state.afterLoading || (state.beforeLoading && state.messages.isEmpty())
            val createMessageEnabled = state.createMessageVisible && state.messages.isNotEmpty()
            createMessage.isEnabled = createMessageEnabled
            createMessage.isVisible = createMessageEnabled
            failure.isVisible = state.messages.isEmpty() && state.error != null
            failure.message = when (state.error) {
                is SSLException -> {
                    getString(R.string.possibly_domain_blocked)
                }
                else -> {
                    getString(R.string.check_connection)
                }
            }
        }
    }
}
