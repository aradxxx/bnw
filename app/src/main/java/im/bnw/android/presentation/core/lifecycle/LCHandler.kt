package im.bnw.android.presentation.core.lifecycle

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Process messages only when started
 * Note that is stopped by default after creation
 */
class LCHandler : Handler, LifecycleObserver {
    private var started = false

    constructor(lifecycleOwner: LifecycleOwner) : this(false) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @JvmOverloads
    constructor(started: Boolean = false) {
        this.started = started
    }

    constructor(lifecycleOwner: LifecycleOwner, started: Boolean) : this(started) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    constructor(looper: Looper) : super(looper)

    constructor(looper: Looper, lifecycleOwner: LifecycleOwner) : this(looper) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    constructor(looper: Looper, started: Boolean) : this(looper) {
        this.started = started
    }

    constructor(looper: Looper, lifecycleOwner: LifecycleOwner, started: Boolean) : this(looper, started) {
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
