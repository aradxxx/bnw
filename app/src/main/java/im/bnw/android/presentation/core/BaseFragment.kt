package im.bnw.android.presentation.core

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import im.bnw.android.di.core.AndroidXInjection
import im.bnw.android.di.core.ViewModelFactory
import im.bnw.android.presentation.core.lifecycle.LCHandler
import im.bnw.android.presentation.util.Const
import timber.log.Timber
import javax.inject.Inject

private const val BUNDLE_VIEW_STATE = "VIEW_STATE"

abstract class BaseFragment<VM : BaseViewModel<S>, S : State>(
    layoutRes: Int,
    private val vmClass: Class<VM>
) : Fragment(layoutRes), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val backPressedDispatcher: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.backPressed()
            }
        }
    protected val viewModel: VM by lazy {
        ViewModelProvider(this, viewModelFactory).get(vmClass)
    }
    var restoredState: S? = null
    protected val handler by lazy {
        LCHandler(viewLifecycleOwner)
    }

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
        restoredState = savedInstanceState?.getParcelable(BUNDLE_VIEW_STATE)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        requireActivity().onBackPressedDispatcher.addCallback(backPressedDispatcher)
        super.onResume()
    }

    override fun onPause() {
        backPressedDispatcher.remove()
        super.onPause()
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            stateLiveData().observe(viewLifecycleOwner, Observer {
                Timber.d("new state: %s", it.toString())
                updateState(it)
            })
            eventLiveData().observe(viewLifecycleOwner, Observer { onEvent(it) })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_VIEW_STATE, viewModel.stateLiveData().value)
        super.onSaveInstanceState(outState)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    fun <A : Parcelable> initialArguments(): A {
        arguments?.getParcelable<A>(Const.BUNDLE_INITIAL_ARGS)?.also { return it }
        throw IllegalArgumentException("Fragment doesn't contain initial args")
    }
}
