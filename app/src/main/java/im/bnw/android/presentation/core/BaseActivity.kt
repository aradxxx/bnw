package im.bnw.android.presentation.core

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import im.bnw.android.presentation.main.MainViewModel
import timber.log.Timber

private const val BUNDLE_VIEW_STATE = "VIEW_STATE"

@AndroidEntryPoint
abstract class BaseActivity<VM : BaseViewModel<S>, S : State>(
    layoutRes: Int,
    private val vmClass: Class<VM>
) : AppCompatActivity(layoutRes) {
    private val viewModel: VM by lazy { ViewModelProvider(this).get(vmClass) }
    var restoredState: S? = null

    protected open fun updateState(state: S) {
        // for implementing
    }

    protected open fun onEvent(event: Any?) {
        // for implementing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        restoredState = savedInstanceState?.getParcelable(BUNDLE_VIEW_STATE)
        //AndroidXInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.apply {
            stateLiveData().observe(this@BaseActivity, Observer {
                Timber.d("new activity state: %s", it.toString())
                updateState(it)
            })
            eventLiveData().observe(this@BaseActivity, Observer { onEvent(it) })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_VIEW_STATE, viewModel.stateLiveData().value)
        super.onSaveInstanceState(outState)
    }
}
