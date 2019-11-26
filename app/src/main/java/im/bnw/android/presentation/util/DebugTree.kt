package im.bnw.android.presentation.util

import timber.log.Timber

class DebugTree(private val customTag: String) : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, customTag, message, t)
    }
}
