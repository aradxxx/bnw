package im.bnw.android.presentation.core.navigation.tab

import androidx.fragment.app.Fragment
import im.bnw.android.R
import ru.terrakok.cicerone.android.support.SupportAppScreen

sealed class Tab(val tag: Int) {
    object General : Tab(R.id.tab_general)
    object About : Tab(R.id.tab_about)

    fun screen() = object : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return TabFragment.newInstance(tag)
        }

        override fun getScreenKey(): String {
            return screenKey()
        }
    }

    open fun screenKey(): String {
        return tag.toString()
    }

    companion object {
        const val GLOBAL = ""

        fun from(tag: Int): Tab {
            return when (tag) {
                R.id.tab_general -> General
                else -> About
            }
        }
    }
}
