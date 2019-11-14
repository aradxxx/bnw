package im.bnw.android.presentation.core

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import im.bnw.android.di.core.AndroidXInjection
import im.bnw.android.di.core.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel<S>, S : State>
    : AppCompatActivity(), HasAndroidInjector {
    companion object {
        private const val BUNDLE_VIEW_STATE = "VIEW_STATE"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: VM
    var restoredState: S? = null

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @LayoutRes
    protected abstract fun layoutRes(): Int

    protected abstract fun viewModelClass(): Class<VM>
    protected open fun updateState(state: S) {
        // for implementing
    }

    protected open fun onEvent(event: Any?) {
        // for implementing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            restoredState = it.getParcelable(BUNDLE_VIEW_STATE)
        }
        AndroidXInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutRes())
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass())
        viewModel.stateLiveData().observe(this, Observer {
            Timber.d("new activity state: %s", it.toString())
            updateState(it)
        })
        viewModel.eventLiveData().observe(this, Observer { onEvent(it) })
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
