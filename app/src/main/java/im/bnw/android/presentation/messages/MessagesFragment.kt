@file:Suppress("MagicNumber")

package im.bnw.android.presentation.messages

import android.os.Bundle
import android.view.View
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.bnw.android.R
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.messages.adapter.MessageAdapter
import im.bnw.android.presentation.util.withInitialArguments
import kotlinx.android.synthetic.main.fragment_messages_list.*

class MessagesFragment : BaseFragment<MessagesViewModel, MessagesState>(
    R.layout.fragment_messages_list
) {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        fun newInstance(params: MessagesScreenParams) =
            MessagesFragment().withInitialArguments(params)
    }

    override val vmClass = MessagesViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageAdapter = MessageAdapter(
            { position -> viewModel.userClicked(position) },
            { messagePosition, mediaPosition -> viewModel.mediaClicked(messagePosition, mediaPosition) }
        )
        linearLayoutManager = LinearLayoutManager(requireContext())
        with(messages_list) {
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
        swipe_to_refresh.setProgressBackgroundColorSchemeColor(requireContext().getColor(R.color.white))
        swipe_to_refresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )
        swipe_to_refresh.setOnRefreshListener { viewModel.swipeRefresh() }
    }

    override fun onEvent(event: Any?) {
        super.onEvent(event)
        when (event) {
            is Event.ScrollToTop -> handler.postDelayed(200) {
                linearLayoutManager.smoothScrollToPosition(messages_list, null, 0)
            }
        }
    }

    override fun updateState(state: MessagesState) {
        messageAdapter.items = state.messages
        progress_bar_line.isVisible = state.beforeLoading && state.messages.isNotEmpty()
        swipe_to_refresh.isRefreshing =
            state.afterLoading || (state.beforeLoading && state.messages.isEmpty())
    }
}
