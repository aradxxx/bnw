package im.bnw.android.presentation.util

import android.content.res.Resources
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import im.bnw.android.presentation.core.FragmentViewBindingDelegate
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator

fun Fragment.tabNavigator(
    tabCicerone: TabCicerone<AppRouter>,
    container: Int
): TabNavigator<AppRouter> {
    val activity: MainActivity = requireActivity() as MainActivity
    return TabNavigator(activity, tabCicerone, childFragmentManager, container)
}

fun <F : Fragment> F.withInitialArguments(params: Parcelable) = apply {
    arguments = bundleOf(Const.BUNDLE_INITIAL_ARGS to params)
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
