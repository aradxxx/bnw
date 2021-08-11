package im.bnw.android.presentation.newpost

import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import im.bnw.android.R
import im.bnw.android.databinding.FragmentNewPostBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.CursorToEnd
import im.bnw.android.presentation.core.Event
import im.bnw.android.presentation.core.dialog.PopupDialogParams
import im.bnw.android.presentation.core.dialog.PopupDialogFragment
import im.bnw.android.presentation.util.DialogCode
import im.bnw.android.presentation.util.attrColor
import im.bnw.android.presentation.util.hideSystemUI
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.viewBinding

class NewPostFragment : BaseFragment<NewPostViewModel, NewPostState>(
    R.layout.fragment_new_post
) {
    private val binding by viewBinding(FragmentNewPostBinding::bind)

    override val vmClass = NewPostViewModel::class.java

    private val anonItemMenu by lazy {
        binding.toolbar.menu.findItem(R.id.item_anon)
    }

    companion object {
        fun newInstance() = NewPostFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.setNavigationOnClickListener {
                viewModel.backPressed()
            }
            toolbar.setOnMenuItemClickListener {
                menuItemSelected(it)
            }
            send.setOnClickListener {
                confirmSendingDialog()
            }
            postText.doAfterTextChanged {
                viewModel.textChanged(it.toString())
            }
            ViewCompat.getWindowInsetsController(postText)?.show(WindowInsetsCompat.Type.ime())
        }
    }

    override fun onDestroyView() {
        hideSystemUI(WindowInsetsCompat.Type.ime())
        super.onDestroyView()
    }

    override fun onPopupDialogResult(requestCode: Int, button: Int) {
        when (requestCode) {
            DialogCode.CONFIRM_SENDING_DIALOG_REQUEST_CODE -> {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    viewModel.sendConfirmed()
                }
            }
        }
    }

    override fun updateState(state: NewPostState) {
        renderIdleState(state)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is CursorToEnd -> {
                val draft = event.text
                viewModel.textChanged(draft)
                handler.post {
                    binding.postText.setSelection(draft.length)
                }
            }
            else -> super.onEvent(event)
        }
    }

    private fun renderIdleState(state: NewPostState) = with(binding) {
        postText.newText = state.text
        send.isEnabled = state.sendEnabled
        anonEnabled(state.asAnon)
    }

    private fun anonEnabled(enabled: Boolean) {
        val icon = anonItemMenu.icon ?: return
        val color = if (enabled) {
            requireContext().getColor(R.color.colorPrimary)
        } else {
            requireContext().attrColor(R.attr.primaryIconColor)
        }
        icon.mutate().apply {
            colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun menuItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_anon -> {
            viewModel.anonChanged()
            true
        }
        else -> false
    }

    private fun confirmSendingDialog() = showDialog {
        PopupDialogFragment {
            PopupDialogParams(
                requestCode = DialogCode.CONFIRM_SENDING_DIALOG_REQUEST_CODE,
                message = getString(R.string.sending_confirmation),
                positiveText = getString(R.string.send),
                negativeText = getString(R.string.cancel),
            )
        }
    }
}
