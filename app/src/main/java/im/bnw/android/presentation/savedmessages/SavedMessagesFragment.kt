@file:Suppress("MagicNumber")

package im.bnw.android.presentation.savedmessages

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import im.bnw.android.R
import im.bnw.android.databinding.FragmentSavedMessagesListBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.Event
import im.bnw.android.presentation.core.RemoveMessageFromLocalStorage
import im.bnw.android.presentation.messages.MessageClickListener
import im.bnw.android.presentation.core.dialog.PopupDialogParams
import im.bnw.android.presentation.core.dialog.PopupDialogFragment
import im.bnw.android.presentation.messages.adapter.MessageAdapter
import im.bnw.android.presentation.messages.adapter.messageItemDecorator
import im.bnw.android.presentation.util.DialogCode
import im.bnw.android.presentation.util.UI
import im.bnw.android.presentation.util.disableItemChangedAnimation
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.dpToPxF
import im.bnw.android.presentation.util.viewBinding

class SavedMessagesFragment : BaseFragment<SavedMessagesViewModel, SavedMessagesState>(
    R.layout.fragment_saved_messages_list
) {
    private val binding by viewBinding(FragmentSavedMessagesListBinding::bind)
    override val vmClass = SavedMessagesViewModel::class.java

    private val messageClickListener = object : MessageClickListener {
        override fun cardClicked(position: Int) = viewModel.cardClicked(position)

        override fun userClicked(position: Int) = viewModel.userClicked(position)

        override fun mediaClicked(position: Int, mediaPosition: Int) =
            viewModel.mediaClicked(position, mediaPosition)

        override fun saveMessageClicked(position: Int) = viewModel.saveMessageClicked(position)

        override fun saveReplyClicked(position: Int) = Unit

        override fun replyCardClicked(position: Int) = Unit

        override fun quoteClicked(position: Int) = Unit
    }
    private val messageAdapter by lazy {
        MessageAdapter(
            UI.MESSAGE_CARD_RADIUS.dpToPxF,
            UI.MESSAGE_MEDIA_HEIGHT.dpToPx,
            messageClickListener,
        )
    }
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        fun newInstance() = SavedMessagesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.messagesList) {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
            addItemDecoration(messageItemDecorator)
            disableItemChangedAnimation()
        }
        binding.failure.setActionListener {
            viewModel.emptyButtonClicked()
        }
    }

    override fun updateState(state: SavedMessagesState) {
        when (state) {
            is SavedMessagesState.Idle -> renderIdleState(state)
            SavedMessagesState.Init -> renderInitState()
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is RemoveMessageFromLocalStorage -> {
                showMessageRemoveConfirmDialog()
            }
            else -> super.onEvent(event)
        }
    }

    override fun onPopupDialogResult(requestCode: Int, button: Int) {
        when (requestCode) {
            DialogCode.CONFIRM_MESSAGE_REMOVE_FROM_SAVED -> {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    viewModel.removeMessageConfirmed()
                }
            }
        }
    }

    private fun renderInitState() {
        with(binding) {
            messagesList.isVisible = false
            failure.isVisible = false
            loadingBar.root.isVisible = true
        }
    }

    private fun renderIdleState(state: SavedMessagesState.Idle) {
        messageAdapter.items = state.messages
        with(binding) {
            messagesList.isVisible = true
            loadingBar.root.isVisible = false
            failure.isVisible = state.messages.isEmpty()
        }
    }

    private fun showMessageRemoveConfirmDialog() = showDialog {
        PopupDialogFragment {
            PopupDialogParams(
                requestCode = DialogCode.CONFIRM_MESSAGE_REMOVE_FROM_SAVED,
                message = getString(R.string.remove_saved_message_confirm),
                positiveText = getString(R.string.ok),
                negativeText = getString(R.string.cancel),
            )
        }
    }
}
