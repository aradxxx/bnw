package im.bnw.android.presentation.newpost

import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import dev.chrisbanes.insetter.applyInsetter
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import im.bnw.android.R
import im.bnw.android.databinding.FragmentNewPostBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.dialog.NotificationDialog
import im.bnw.android.presentation.util.*

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
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        with(binding) {
            /*root.applyInsetter {
                type(navigationBars = true, statusBars = true, ime = true) {
                    padding()
                }
                type(navigationBars = true, statusBars = true, ime = true) {
                    margin()
                }
            }*/
            toolbar.applyInsetter {
                type(statusBars = true) {
                    padding()
                }
            }
            post.applyInsetter {
                /*type(navigationBars = true) {
                    padding()
                }*/
                type(navigationBars = true, ime = true) {
                    margin()
                }
            }
            toolbar.setNavigationOnClickListener {
                viewModel.backPressed()
            }
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
            post.setOnClickListener {
                confirmSendingDialog()
            }
            postText.doAfterTextChanged {
                viewModel.textChanged(it.toString())
            }
            //ViewCompat.getWindowInsetsController(postText)?.show(WindowInsetsCompat.Type.ime())
        }
    }

    override fun onDestroyView() {
        //hideSystemUI(WindowInsetsCompat.Type.ime())
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        super.onDestroyView()
    }

    override fun onAcceptClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        if (requestCode == DialogCode.CONFIRM_SENDING_DIALOG_REQUEST_CODE) {
            viewModel.sendConfirmed()
        }
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

    private fun sendEnabled(enabled: Boolean) {
        binding.post.isEnabled = enabled
        doneItemMenu.isEnabled = enabled
        val icon = doneItemMenu.icon ?: return
        val color = if (enabled) {
            R.color.colorPrimary
        } else {
            R.color.colorDisabled
        }
        icon.mutate()
        icon.colorFilter =
            PorterDuffColorFilter(requireContext().getColor(color), PorterDuff.Mode.SRC_ATOP)
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
