package im.bnw.android.presentation.util

import android.content.res.Resources
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowNavigator
import im.bnw.android.presentation.core.FragmentViewBindingDelegate
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity

fun Fragment.tabNavigator(
    flowCicerone: FlowCicerone<AppRouter>,
    container: Int
): FlowNavigator<AppRouter> {
    val activity: MainActivity = requireActivity() as MainActivity
    return FlowNavigator(activity, container, flowCicerone, activity.supportFragmentManager, childFragmentManager)
}

fun <F : Fragment> F.withInitialArguments(params: Parcelable) = apply {
    arguments = bundleOf(Const.BUNDLE_INITIAL_ARGS to params)
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
