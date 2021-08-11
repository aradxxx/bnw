package im.bnw.android.di.app

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import timber.log.Timber

object MainScope {
    private val eHandler = CoroutineExceptionHandler { _, t ->
        handleException(t)
    }
    val scope = MainScope() + eHandler

    private fun handleException(t: Throwable) {
        if (t is CancellationException) {
            return
        }
        Timber.e(t)
    }
}
