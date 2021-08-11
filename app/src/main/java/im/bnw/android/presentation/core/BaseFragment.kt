package im.bnw.android.presentation.core

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import im.bnw.android.R
import im.bnw.android.di.core.ViewModelFactory
import im.bnw.android.presentation.core.dialog.PopupDialogParams
import im.bnw.android.presentation.core.dialog.PopupDialogFragment
import im.bnw.android.presentation.core.dialog.PopupDialogFragment.Companion.POPUP_DIALOG_REQUEST_KEY
import im.bnw.android.presentation.core.dialog.PopupDialogFragment.Companion.POPUP_REQUEST_CODE
import im.bnw.android.presentation.core.dialog.PopupDialogFragment.Companion.POPUP_BUTTON
import im.bnw.android.presentation.core.lifecycle.LCHandler
import im.bnw.android.presentation.util.Const
import im.bnw.android.presentation.util.openMedia
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

    protected open fun onEvent(event: Event) {
        // for implementing
        when (event) {
            is DialogEvent -> showDialog {
                val message = if (event.message != null) {
                    getString(event.message)
                } else {
                    ""
                }
                PopupDialogFragment {
                    PopupDialogParams(
                        title = getString(event.title),
                        message = message,
                        positiveText = getString(R.string.ok),
                    )
                }
            }
            is BnwApiErrorEvent -> showDialog {
                PopupDialogFragment {
                    PopupDialogParams(
                        message = event.description,
                        positiveText = getString(R.string.error),
                    )
                }
            }
            is OpenMediaEvent -> {
                openMedia(event.urls, event.selectedItem)
            }
        }
    }

    protected open fun onPopupDialogResult(requestCode: Int, button: Int) {
        // for implementing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        restoredState = savedInstanceState?.getParcelable(BUNDLE_VIEW_STATE)
        super.onCreate(savedInstanceState)
        setPopupDialogResultListener()
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
        with(viewModel) {
            stateLiveData().observe(
                viewLifecycleOwner, {
                    Timber.d("new state: %s", it.toString())
                    updateState(it)
                }
            )
            eventLiveData().observe(
                viewLifecycleOwner, {
                    onEvent(it)
                }
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_VIEW_STATE, viewModel.stateLiveData().value)
        super.onSaveInstanceState(outState)
    }

    fun <A : Parcelable> initialArguments(): A {
        arguments?.getParcelable<A>(Const.BUNDLE_INITIAL_ARGS)?.also { return it }
        throw IllegalArgumentException("Fragment doesn't contain initial args")
    }

    private fun setPopupDialogResultListener() {
        setFragmentResultListener(POPUP_DIALOG_REQUEST_KEY) { _, bundle ->
            onPopupDialogResult(
                bundle.getInt(POPUP_REQUEST_CODE),
                bundle.getInt(POPUP_BUTTON),
            )
        }
    }

    private fun getDialogFragment(tag: String): DialogFragment? {
        val fragment = parentFragmentManager.findFragmentByTag(tag)
        if (fragment !is DialogFragment) {
            return null
        }
        if (fragment.dialog == null || fragment.dialog?.isShowing == false) {
            return null
        }
        return fragment
    }

    protected fun showDialog(dialogFragment: () -> DialogFragment) {
        showDialog(PopupDialogFragment.POPUP_DIALOG_TAG, dialogFragment)
    }

    protected fun showDialog(tag: String, dialogFragment: () -> DialogFragment) {
        getDialogFragment(tag)?.dismiss()
        try {
            dialogFragment().show(parentFragmentManager, tag)
        } catch (e: IllegalStateException) {
            Timber.e(e)
            handler.post {
                showDialog(tag, dialogFragment)
            }
        }
    }
}
