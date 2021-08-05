package im.bnw.android.presentation.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.back
import im.bnw.android.presentation.core.lifecycle.LiveEvent
import im.bnw.android.presentation.util.toEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.plus
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

abstract class BaseViewModel<S : State>(
    initialState: S,
    protected val modo: Modo
) : ViewModel() {
    private val stateLiveData = MutableLiveData(initialState)
    private val eventLiveData = LiveEvent<Event>()
    private val atomicState = AtomicReference(initialState)
    protected val state: S get() = atomicState.get()
    private val eHandler = CoroutineExceptionHandler { _, e ->
        handleException(e)
    }
    protected val vmScope = viewModelScope + eHandler

    protected open fun handleException(e: Throwable) {
        if (e is CancellationException) {
            return
        }
        Timber.e(e)
        val event = e.toEvent() ?: return
        postEvent(event)
    }

    open fun stateLiveData(): LiveData<S> = stateLiveData
    open fun eventLiveData(): LiveData<Event> = eventLiveData

    protected fun updateState(function: (S) -> S) {
        var currentState: S
        var updatedState: S
        do {
            currentState = state
            updatedState = function(currentState)
        } while (!atomicState.compareAndSet(currentState, updatedState))
        stateLiveData.postValue(updatedState)
    }

    protected fun postEvent(event: Event) {
        eventLiveData.postValue(event)
    }

    open fun backPressed() {
        modo.back()
    }
}
