package im.bnw.android.presentation.core.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import im.bnw.android.R
import im.bnw.android.presentation.util.Symbols

private const val ARGUMENT_MESSAGE = "ARGUMENT_MESSAGE"
private const val ARGUMENT_POSITIVE_TITLE = "ARGUMENT_POSITIVE_TITLE"
private const val ARGUMENT_NEGATIVE_TITLE = "ARGUMENT_NEGATIVE_TITLE"
private const val ARGUMENT_NEUTRAL_TITLE = "ARGUMENT_NEUTRAL_TITLE"

class NotificationDialog : DialogFragment() {
    private var message: String? = null
    private var positiveTitle: String? = null
    private var negativeTitle: String? = null
    private var neutralTitle: String? = null
    private var dialogResult: DialogResult? = null

    companion object {
        const val NOTIFICATION_DIALOG_TAG = "NOTIFICATION_DIALOG_TAG"

        fun newInstance(
            message: String,
            positiveTitle: String? = null,
            negativeTitle: String? = null,
            neutralTitle: String? = null
        ): NotificationDialog {
            val dialog = NotificationDialog()
            val bundle = Bundle()
            bundle.putString(ARGUMENT_MESSAGE, message)
            bundle.putString(ARGUMENT_POSITIVE_TITLE, positiveTitle)
            bundle.putString(ARGUMENT_NEGATIVE_TITLE, negativeTitle)
            bundle.putString(ARGUMENT_NEUTRAL_TITLE, neutralTitle)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args == null || !args.containsKey(ARGUMENT_MESSAGE)
            || !args.containsKey(ARGUMENT_POSITIVE_TITLE)
        ) {
            return
        }
        message = args.getString(ARGUMENT_MESSAGE, Symbols.EMPTY)
        positiveTitle = args.getString(ARGUMENT_POSITIVE_TITLE, getString(R.string.ok))
        negativeTitle = args.getString(ARGUMENT_NEGATIVE_TITLE, Symbols.EMPTY)
        neutralTitle = args.getString(ARGUMENT_NEUTRAL_TITLE, Symbols.EMPTY)
        val fragment: Fragment? = targetFragment
        if (fragment is DialogResult) {
            dialogResult = fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(positiveTitle, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (dialog == null) {
                        return
                    }
                    dialogResult?.onAcceptClick(dialog, which, targetRequestCode)
                }
            })
        if (!TextUtils.isEmpty(negativeTitle)) {
            builder.setNegativeButton(negativeTitle, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (dialog == null) {
                        return
                    }
                    dialogResult?.onNegativeClick(dialog, which, targetRequestCode)
                }
            })
        }
        if (!TextUtils.isEmpty(neutralTitle)) {
            builder.setNeutralButton(neutralTitle, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (dialog == null) {
                        return
                    }
                    dialogResult?.onNeutralClick(dialog, which, targetRequestCode)
                }
            })
        }
        return builder.create()
    }
}
