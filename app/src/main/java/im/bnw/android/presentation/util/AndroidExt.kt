package im.bnw.android.presentation.util

import android.view.View
import androidx.fragment.app.Fragment
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator

fun View.setVisibility(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}

inline fun createString(block: StringBuilder.() -> Unit): String {
    val sb = StringBuilder()
    sb.block()
    return sb.toString()
}

fun Fragment.tabNavigator(tabCicerone: TabCicerone<AppRouter>, container: Int)
        : TabNavigator<AppRouter> {
    val activity: MainActivity = requireActivity() as MainActivity
    return TabNavigator(activity, tabCicerone, childFragmentManager, container)
}
