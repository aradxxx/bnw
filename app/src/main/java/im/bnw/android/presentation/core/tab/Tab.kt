package im.bnw.android.presentation.core.tab

import im.bnw.android.R

sealed class Tab {
    protected abstract val menuId: Int
    protected abstract val tag: String

    fun tag(): String {
        return tag
    }

    object General : Tab() {
        override val menuId: Int
            get() = R.id.tab_general
        override val tag: String
            get() = "tab_general"
    }

    object About : Tab() {
        override val menuId: Int
            get() = R.id.tab_about
        override val tag: String
            get() = "tab_about"
    }

    companion object {
        fun from(menuId: Int): Tab {
            return when (menuId) {
                R.id.tab_general -> General
                else -> About
            }
        }
    }
}