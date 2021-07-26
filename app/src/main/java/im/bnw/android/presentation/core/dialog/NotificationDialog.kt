package im.bnw.android.presentation.core.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import im.bnw.android.R

private const val ARGUMENT_TITLE = "ARGUMENT_TITLE"
private const val ARGUMENT_MESSAGE = "ARGUMENT_MESSAGE"
private const val ARGUMENT_REQUEST_CODE = "ARGUMENT_REQUEST_CODE"
private const val ARGUMENT_POSITIVE_TITLE = "ARGUMENT_POSITIVE_TITLE"
private const val ARGUMENT_NEGATIVE_TITLE = "ARGUMENT_NEGATIVE_TITLE"
private const val ARGUMENT_NEUTRAL_TITLE = "ARGUMENT_NEUTRAL_TITLE"

private const val UNDEFINED_VALUE = -1

@Suppress("LongParameterList")
class NotificationDialog : DialogFragment() {
    private var title: String? = null
    private var message: String? = null
    private var positiveTitle: String? = null
    private var negativeTitle: String? = null
    private var neutralTitle: String? = null
    private var dialogResult: DialogResult? = null
    private var requestCode = UNDEFINED_VALUE

    companion object {
        const val NOTIFICATION_DIALOG_TAG = "NOTIFICATION_DIALOG_TAG"

        fun newInstance(
            title: String? = null,
            message: String,
            requestCode: Int = UNDEFINED_VALUE,
            positiveTitle: String? = null,
            negativeTitle: String? = null,
            neutralTitle: String? = null
        ): NotificationDialog {
            val dialog = NotificationDialog()
            val bundle = Bundle()
            bundle.putString(ARGUMENT_TITLE, title)
            bundle.putString(ARGUMENT_MESSAGE, message)
            bundle.putString(ARGUMENT_POSITIVE_TITLE, positiveTitle)
            bundle.putString(ARGUMENT_NEGATIVE_TITLE, negativeTitle)
            bundle.putString(ARGUMENT_NEUTRAL_TITLE, neutralTitle)
            bundle.putInt(ARGUMENT_REQUEST_CODE, requestCode)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args == null || !args.containsKey(ARGUMENT_MESSAGE) ||
            !args.containsKey(ARGUMENT_POSITIVE_TITLE)
        ) {
            return
        }
        title = args.getString(ARGUMENT_TITLE, "")
        message = args.getString(ARGUMENT_MESSAGE, "")
        positiveTitle = args.getString(ARGUMENT_POSITIVE_TITLE, getString(R.string.ok))
        negativeTitle = args.getString(ARGUMENT_NEGATIVE_TITLE, "")
        neutralTitle = args.getString(ARGUMENT_NEUTRAL_TITLE, "")
        requestCode = args.getInt(ARGUMENT_REQUEST_CODE, UNDEFINED_VALUE)

        val fragment = parentFragment
        val activity = requireActivity()
        if (fragment is DialogResult) {
            dialogResult = fragment
        } else if (activity is DialogResult) {
            dialogResult = activity
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                positiveTitle,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (dialog == null) {
                            return
                        }
                        dialogResult?.onAcceptClick(
                            dialog,
                            which,
                            requestCode
                        )
                    }
                }
            )
        if (!TextUtils.isEmpty(negativeTitle)) {
            builder.setNegativeButton(
                negativeTitle,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (dialog == null) {
                            return
                        }
                        dialogResult?.onNegativeClick(
                            dialog,
                            which,
                            requestCode
                        )
                    }
                }
            )
        }
        if (!TextUtils.isEmpty(neutralTitle)) {
            builder.setNeutralButton(
                neutralTitle,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (dialog == null) {
                            return
                        }
                        dialogResult?.onNeutralClick(
                            dialog,
                            which,
                            requestCode
                        )
                    }
                }
            )
        }
        return builder.create()
    }
}
