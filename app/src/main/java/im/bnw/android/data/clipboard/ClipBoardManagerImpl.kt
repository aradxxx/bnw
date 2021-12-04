package im.bnw.android.data.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService

class ClipBoardManagerImpl(
    val context: Context
) : ClipBoardManager {
    override fun copyToClipBoard(text: String) {
        val clipBoardManager = getSystemService(context, ClipboardManager::class.java) ?: return
        val clipData = ClipData.newPlainText("", text)
        clipBoardManager.setPrimaryClip(clipData)
    }
}
