package im.bnw.android.presentation.core.dialog

import android.content.DialogInterface

interface DialogResult {
    fun onAcceptClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        // dialog ok click
    }

    fun onNegativeClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        // dialog negative click
    }

    fun onNeutralClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        // dialog neutral click
    }
}
