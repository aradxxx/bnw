package im.bnw.android.presentation.core.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.bnw.android.presentation.util.Const.BUNDLE_INITIAL_ARGS
import im.bnw.android.presentation.util.withInitialArguments

class PopupDialogFragment : DialogFragment() {
    companion object {
        const val UNDEFINED_VALUE = -1
        const val POPUP_DIALOG_TAG = "POPUP_DIALOG_TAG"
        const val POPUP_DIALOG_REQUEST_KEY = "POPUP_DIALOG_REQUEST_KEY"

        const val POPUP_REQUEST_CODE = "POPUP_REQUEST_CODE"
        const val POPUP_BUTTON = "POPUP_BUTTON"

        @JvmStatic
        @JvmName("newInstance")
        operator fun invoke(params: () -> PopupDialogParams) =
            PopupDialogFragment().withInitialArguments(params())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        MaterialAlertDialogBuilder(requireContext()).run {
            val params = arguments?.getParcelable<PopupDialogParams>(BUNDLE_INITIAL_ARGS)
                ?: throw IllegalArgumentException("Popup dialog params is null")
            with(params) {
                if (title.isNotEmpty()) {
                    setTitle(title)
                }
                if (message.isNotEmpty()) {
                    setMessage(message)
                }
                setPositiveButton(positiveText) { _, button ->
                    setButtonResult(dialog, requestCode, button)
                }
                if (negativeText.isNotEmpty()) {
                    setNegativeButton(negativeText) { _, button ->
                        setButtonResult(dialog, requestCode, button)
                    }
                }
                if (neutralText.isNotEmpty()) {
                    setNeutralButton(neutralText) { _, button ->
                        setButtonResult(dialog, requestCode, button)
                    }
                }
            }
            create()
        }

    private fun setButtonResult(dialog: Dialog?, requestCode: Int, button: Int) {
        dialog ?: return
        if (requestCode == UNDEFINED_VALUE) {
            return
        }
        setFragmentResult(
            POPUP_DIALOG_REQUEST_KEY,
            bundleOf(
                POPUP_REQUEST_CODE to requestCode,
                POPUP_BUTTON to button,
            )
        )
    }
}
