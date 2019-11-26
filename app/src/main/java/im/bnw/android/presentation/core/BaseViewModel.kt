package im.bnw.android.presentation.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import im.bnw.android.presentation.core.lifecycle.LiveEvent
import im.bnw.android.presentation.core.navigation.AppRouter
import java.util.concurrent.atomic.AtomicReference

abstract class BaseViewModel<S : State>(
    initialState: S,
    protected val router: AppRouter
) : ViewModel() {
    private val stateLiveData = MutableLiveData(initialState)
    private val eventLiveData = LiveEvent<Any?>()
    private val atomicState = AtomicReference<S>(initialState)
    protected val state: S get() = atomicState.get()

    open fun stateLiveData(): LiveData<S> = stateLiveData
    open fun eventLiveData(): LiveData<Any?> = eventLiveData

    open fun action(action: Any?) {
        when (action) {
            Action.BackPressed -> {
                backPressed()
            }
        }
    }

    protected fun updateState(function: (S) -> S) {
        var currentState: S
        var updatedState: S
        do {
            currentState = state
            updatedState = function(currentState)
        } while (!atomicState.compareAndSet(currentState, updatedState))
        stateLiveData.postValue(updatedState)
    }

    private fun backPressed() {
        router.exit()
    }
}
