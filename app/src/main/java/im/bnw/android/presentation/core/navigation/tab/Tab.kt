package im.bnw.android.presentation.core.navigation.tab

import com.github.terrakok.cicerone.androidx.FragmentScreen
import im.bnw.android.R

sealed class Tab(val tag: Int) {
    object General : Tab(R.id.tab_general)
    object Profile : Tab(R.id.tab_profile)

    fun screen() = FragmentScreen(tag.toString()) {
        TabFragment.newInstance(tag)
    }

    open fun screenKey(): String {
        return tag.toString()
    }

    companion object {
        const val GLOBAL = ""

        fun from(tag: Int): Tab {
            return when (tag) {
                R.id.tab_general -> General
                else -> Profile
            }
        }
    }
}
