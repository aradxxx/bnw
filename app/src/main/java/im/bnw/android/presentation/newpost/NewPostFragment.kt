package im.bnw.android.presentation.newpost

import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import im.bnw.android.R
import im.bnw.android.databinding.FragmentNewPostBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.dialog.NotificationDialog
import im.bnw.android.presentation.util.DialogCode
import im.bnw.android.presentation.util.hideKeyboard
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.showKeyboard
import im.bnw.android.presentation.util.viewBinding

class NewPostFragment : BaseFragment<NewPostViewModel, NewPostState>(
    R.layout.fragment_new_post
) {
    private val binding by viewBinding(FragmentNewPostBinding::bind)
    override val vmClass = NewPostViewModel::class.java

    private val anonItemMenu by lazy {
        binding.toolbar.menu.findItem(R.id.item_anon)
    }
    private val doneItemMenu by lazy {
        binding.toolbar.menu.findItem(R.id.item_done)
    }

    companion object {
        fun newInstance() = NewPostFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.setNavigationOnClickListener { viewModel.backPressed() }
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_anon -> {
                        viewModel.anonChanged()
                        true
                    }
                    R.id.item_done -> {
                        confirmSendingDialog()
                        true
                    }
                    else -> false
                }
            }
            postText.doAfterTextChanged {
                viewModel.textChanged(it.toString())
            }
            showKeyboard()
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onAcceptClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        if (requestCode == DialogCode.CONFIRM_SENDING_DIALOG_REQUEST_CODE) {
            viewModel.sendConfirmed()
        }
    }

    private fun anonEnabled(enabled: Boolean) {
        val icon = anonItemMenu.icon
        if (icon != null) {
            val color = if (enabled) {
                R.color.colorAccent
            } else {
                R.color.colorPrimaryDark
            }
            icon.mutate()
            icon.colorFilter =
                PorterDuffColorFilter(requireContext().getColor(color), PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun sendEnabled(enabled: Boolean) {
        doneItemMenu.isEnabled = enabled
        val icon = doneItemMenu.icon
        if (icon != null) {
            val color = if (enabled) {
                R.color.colorPrimaryDark
            } else {
                R.color.colorDisabled
            }
            icon.mutate()
            icon.colorFilter =
                PorterDuffColorFilter(requireContext().getColor(color), PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun updateState(state: NewPostState) {
        renderIdleState(state)
    }

    private fun renderIdleState(state: NewPostState) {
        with(binding) {
            postText.newText = state.text
            anonEnabled(state.asAnon)
            sendEnabled(state.sendEnabled)
        }
    }

    private fun confirmSendingDialog() {
        showDialog {
            NotificationDialog.newInstance(
                null,
                getString(R.string.sending_confirmation),
                DialogCode.CONFIRM_SENDING_DIALOG_REQUEST_CODE,
                getString(R.string.send),
                getString(R.string.cancel)
            )
        }
    }
}
