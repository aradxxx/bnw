package im.bnw.android.presentation.core.lifecycle

import android.os.Handler
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Process messages only when started
 * Note that is stopped by default after creation
 */
class LCHandler(
    private val lifecycleOwner: LifecycleOwner,
    private var started: Boolean = false
) : Handler(), LifecycleObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    fun start() {
        started = true
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_STOP)
    fun stop() {
        started = false
        removeCallbacksAndMessages(null)
    }

    override fun dispatchMessage(msg: Message) {
        if (!started) {
            return
        }
        super.dispatchMessage(msg)
    }
}
