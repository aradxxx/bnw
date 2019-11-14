package im.bnw.android.presentation.core

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import im.bnw.android.di.core.AndroidXInjection
import im.bnw.android.di.core.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment<VM : BaseViewModel<S>, S : State>
    : Fragment(), HasAndroidInjector {
    companion object {
        const val BUNDLE_INITIAL_ARGS = "INITIAL_ARGS"
        private const val BUNDLE_VIEW_STATE = "VIEW_STATE"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: VM
    var restoredState: S? = null

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @LayoutRes
    protected abstract fun layoutResource(): Int

    protected abstract fun viewModelClass(): Class<VM>
    protected open fun updateState(state: S) {
        // for implementing
    }

    protected open fun onEvent(event: Any?) {
        // for implementing
    }

    override fun onAttach(context: Context) {
        AndroidXInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            restoredState = it.getParcelable(BUNDLE_VIEW_STATE)
        }
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(layoutResource(), container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass())
        viewModel.stateLiveData().observe(viewLifecycleOwner, Observer {
            Timber.d("new state: %s", it.toString())
            updateState(it)
        })
        viewModel.eventLiveData().observe(viewLifecycleOwner, Observer { onEvent(it) })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_VIEW_STATE, viewModel.stateLiveData().value)
        super.onSaveInstanceState(outState)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    protected fun action(action: Any?) {
        viewModel.action(action)
    }
}
