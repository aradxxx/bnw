package im.bnw.android.presentation.util

import android.content.res.Resources
import androidx.fragment.app.Fragment
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator

fun Fragment.tabNavigator(tabCicerone: TabCicerone<AppRouter>, container: Int)
        : TabNavigator<AppRouter> {
    val activity: MainActivity = requireActivity() as MainActivity
    return TabNavigator(activity, tabCicerone, childFragmentManager, container)
}

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
