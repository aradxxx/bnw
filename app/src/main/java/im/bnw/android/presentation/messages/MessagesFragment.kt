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
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments

class MessagesFragment : BaseFragment<MessagesViewModel, MessagesState>(
    R.layout.fragment_messages_list
) {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override val vmClass = MessagesViewModel::class.java
    private val binding by viewBinding(FragmentMessagesListBinding::bind)

    companion object {
        fun newInstance(params: MessagesScreenParams) = MessagesFragment().withInitialArguments(params)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageAdapter = MessageAdapter(
            { position -> viewModel.userClicked(position) },
            { messagePosition, mediaPosition -> viewModel.mediaClicked(messagePosition, mediaPosition) }
        )
        linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.messagesList) {
            layoutManager = linearLayoutManager.apply { recycleChildrenOnDetach = true }
            this.adapter = messageAdapter
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (linearLayoutManager.findLastVisibleItemPosition() + 10 == adapter?.itemCount) {
                            viewModel.bottomNear()
                        }
                    }
                }
            )
        }
        with(binding.swipeToRefresh) {
            setProgressBackgroundColorSchemeColor(requireContext().getColor(R.color.white))
            setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryDark
            )
            setOnRefreshListener { viewModel.swipeRefresh() }
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
        binding.progressBarLine.isVisible = state.beforeLoading && state.messages.isNotEmpty()
        binding.swipeToRefresh.isRefreshing =
            state.afterLoading || (state.beforeLoading && state.messages.isEmpty())
    }
}
