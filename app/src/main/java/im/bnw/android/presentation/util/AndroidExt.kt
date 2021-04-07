package im.bnw.android.presentation.util

import android.content.Context
import android.content.res.Resources
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowNavigator
import dev.chrisbanes.insetter.applyInsetter
import im.bnw.android.presentation.core.FragmentViewBindingDelegate
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity

fun Fragment.tabNavigator(
    flowCicerone: FlowCicerone<AppRouter>,
    container: Int
): FlowNavigator<AppRouter> {
    val activity: MainActivity = requireActivity() as MainActivity
    return FlowNavigator(
        activity,
        container,
        flowCicerone,
        activity.supportFragmentManager,
        childFragmentManager
    )
}

fun <F : Fragment> F.withInitialArguments(params: Parcelable) = apply {
    arguments = bundleOf(Const.BUNDLE_INITIAL_ARGS to params)
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.dpToPxF: Float
    get() = this * Resources.getSystem().displayMetrics.density

@ColorInt
fun Context.attrColor(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

var TextView.newText: String
    get() = text.toString()
    set(newText) {
        if (text.toString() != newText) {
            text = newText
        }
    }

fun Fragment.showSystemUI(windowInsetsTypes: Int) =
    view?.let {
        val window = requireActivity().window
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(windowInsetsTypes)
        }
    }

fun Fragment.hideSystemUI(windowInsetsTypes: Int) =
    view?.let {
        val window = requireActivity().window
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(windowInsetsTypes)
        }
    }

/*region throttled click listener*/
private var lastClickTimestamp = 0L
fun View.setThrottledClickListener(delay: Long = 500L, clickListener: (View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - lastClickTimestamp
        if (delta !in 0..delay) {
            lastClickTimestamp = currentTime
            clickListener(this)
        }
    }
}
/*endregion throttled click listener*/
