package im.bnw.android.presentation.savedreplies

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import im.bnw.android.R
import im.bnw.android.databinding.FragmentSavedRepliesListBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.Event
import im.bnw.android.presentation.core.RemoveReplyFromLocalStorage
import im.bnw.android.presentation.core.dialog.NotificationDialog
import im.bnw.android.presentation.messagedetails.adapter.ReplyAdapter
import im.bnw.android.presentation.messagedetails.adapter.replyItemDecorator
import im.bnw.android.presentation.util.DialogCode
import im.bnw.android.presentation.util.UI
import im.bnw.android.presentation.util.disableItemChangedAnimation
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.viewBinding

class SavedRepliesFragment : BaseFragment<SavedRepliesViewModel, SavedRepliesState>(
    R.layout.fragment_saved_replies_list
) {
    private val binding by viewBinding(FragmentSavedRepliesListBinding::bind)
    override val vmClass = SavedRepliesViewModel::class.java

    private lateinit var repliesAdapter: ReplyAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        fun newInstance() = SavedRepliesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repliesAdapter = ReplyAdapter(
            0F,
            UI.MESSAGE_DETAILS_MEDIA_HEIGHT.dpToPx,
            { position -> viewModel.userClicked(position) },
            { replyPosition, mediaPosition ->
                viewModel.mediaClicked(replyPosition, mediaPosition)
            },
            { position -> viewModel.cardClicked(position) },
            { },
            { position -> viewModel.saveReplyClicked(position) },
            {}
        )
        linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.repliesList) {
            layoutManager = linearLayoutManager
            adapter = repliesAdapter
            addItemDecoration(replyItemDecorator)
            disableItemChangedAnimation()
        }
        binding.failure.setActionListener {
            viewModel.emptyButtonClicked()
        }
    }

    override fun updateState(state: SavedRepliesState) {
        when (state) {
            SavedRepliesState.Init -> renderInitState()
            is SavedRepliesState.Idle -> renderIdleState(state)
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is RemoveReplyFromLocalStorage -> {
                showReplyRemoveConfirmDialog()
            }
            else -> super.onEvent(event)
        }
    }

    override fun onAcceptClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        if (requestCode == DialogCode.CONFIRM_REPLY_REMOVE_FROM_SAVED) {
            viewModel.removeReplyConfirmed()
        }
    }

    private fun renderInitState() = with(binding) {
        repliesList.isVisible = false
        failure.isVisible = false
        loadingBar.root.isVisible = true
    }

    private fun renderIdleState(state: SavedRepliesState.Idle) = with(binding) {
        repliesAdapter.items = state.replies
        repliesList.isVisible = true
        loadingBar.root.isVisible = false
        failure.isVisible = state.replies.isEmpty()
    }

    private fun showReplyRemoveConfirmDialog() = showDialog {
        NotificationDialog.newInstance(
            message = getString(R.string.remove_saved_reply_confirm),
            requestCode = DialogCode.CONFIRM_REPLY_REMOVE_FROM_SAVED,
            positiveTitle = getString(R.string.ok),
            negativeTitle = getString(R.string.cancel)
        )
    }
}
