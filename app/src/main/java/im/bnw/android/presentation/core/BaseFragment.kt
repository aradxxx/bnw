package im.bnw.android.presentation.core

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import im.bnw.android.di.core.ViewModelFactory
import im.bnw.android.presentation.core.dialog.NotificationDialog
import im.bnw.android.presentation.core.lifecycle.LCHandler
import im.bnw.android.presentation.util.Const
import timber.log.Timber
import javax.inject.Inject

private const val BUNDLE_VIEW_STATE = "VIEW_STATE"

@SuppressWarnings("TooManyFunctions")
abstract class BaseFragment<VM : BaseViewModel<S>, S : State>(
    layoutRes: Int
) : DaggerFragment(layoutRes) {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val backPressedDispatcher: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.backPressed()
            }
        }
    protected abstract val vmClass: Class<VM>
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
        when (event) {
            is DialogEvent -> showDialog {
                NotificationDialog.newInstance(getString(event.message))
            }
        }
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
        viewModel.stateLiveData().observe(
            viewLifecycleOwner,
            {
                Timber.d("new state: %s", it.toString())
                updateState(it)
            }
        )
        viewModel.eventLiveData().observe(viewLifecycleOwner, { onEvent(it) })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_VIEW_STATE, viewModel.stateLiveData().value)
        super.onSaveInstanceState(outState)
    }

    fun <A : Parcelable> initialArguments(): A {
        arguments?.getParcelable<A>(Const.BUNDLE_INITIAL_ARGS)?.also { return it }
        throw IllegalArgumentException("Fragment doesn't contain initial args")
    }

    private fun getDialogFragment(tag: String): DialogFragment? {
        val fragment: Fragment? = parentFragmentManager.findFragmentByTag(tag)
        if (fragment is DialogFragment) {
            val dialogFragment: DialogFragment = fragment
            return if (dialogFragment.dialog != null && dialogFragment.dialog?.isShowing == true) {
                dialogFragment
            } else {
                null
            }
        }
        return null
    }

    protected fun dismissDialog(tag: String) {
        val dialogFragment: DialogFragment? = getDialogFragment(tag)
        dialogFragment?.dismiss()
    }

    protected fun showDialog(dialogFragment: () -> DialogFragment) {
        showDialog(NotificationDialog.NOTIFICATION_DIALOG_TAG, dialogFragment)
    }

    protected fun showDialog(tag: String, dialogFragment: () -> DialogFragment) {
        dismissDialog(tag)
        val fragmentManager: FragmentManager = childFragmentManager
        val df = dialogFragment()
        try {
            df.show(fragmentManager, tag)
        } catch (e: IllegalStateException) {
            Timber.e(e)
            handler.post { showDialog(tag, dialogFragment) }
        }
    }
}
