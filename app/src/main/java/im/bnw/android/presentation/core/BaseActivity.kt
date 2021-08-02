package im.bnw.android.presentation.core

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import im.bnw.android.di.core.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

private const val BUNDLE_VIEW_STATE = "VIEW_STATE"

abstract class BaseActivity<VM : BaseViewModel<S>, S : State>(
    private val vmClass: Class<VM>
) : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    protected val viewModel: VM by lazy {
        ViewModelProvider(this, viewModelFactory).get(vmClass)
    }
    var restoredState: S? = null

    protected open fun updateState(state: S) {
        // for implementing
    }

    protected open fun onEvent(event: Any?) {
        // for implementing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        restoredState = savedInstanceState?.getParcelable(BUNDLE_VIEW_STATE)
        super.onCreate(savedInstanceState)
        viewModel.stateLiveData().observe(
            this@BaseActivity,
            {
                Timber.d("new activity state: %s", it.toString())
                updateState(it)
            }
        )
        viewModel.eventLiveData().observe(this@BaseActivity, { onEvent(it) })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_VIEW_STATE, viewModel.stateLiveData().value)
        super.onSaveInstanceState(outState)
    }
}
